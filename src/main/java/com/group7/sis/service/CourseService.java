package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.dto.CourseDTO.CourseAssignmentDTO;
import com.group7.sis.entity.academic.CourseAssignmentEntity;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.YearEntity;
import com.group7.sis.entity.academic.CourseEntity.Status;
import com.group7.sis.mapper.CourseMapper;
import com.group7.sis.repository.CourseRepository;
import com.group7.sis.service.queryservices.CourseQueryService;
import com.group7.sis.service.queryservices.CurriculumQueryService;
import com.group7.sis.service.queryservices.DepartmentQueryService;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseService {
    private final CourseQueryService courseQueryService;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    private final CurriculumQueryService curriculumQueryService;
    private final DepartmentQueryService departmentQueryService;

    public CourseDTO addCourse(CourseDTO courseDTO){
        CourseEntity courseEntity = courseMapper.toEntity(courseDTO);

        if (courseDTO.getDepartmentId() != null){
            courseEntity.setDepartment(departmentQueryService.getDepartmentEntity(courseDTO.getDepartmentId()));
            courseEntity.setCode(courseCodeGenerator(courseDTO));
        }

        if (courseDTO.getPrerequisiteIds() != null){
            for(Long id : courseDTO.getPrerequisiteIds()){
                courseEntity.getPrerequisites().add(courseQueryService.getCourseEntity(id));
            }
        }

        if (courseDTO.getCourseAssignments() != null){

            checkCourseAssignmentsDuplicates(courseDTO);

            for (CourseAssignmentDTO courseAssignmentDTO : courseDTO.getCourseAssignments()){
                CourseAssignmentEntity courseAssignmentEntity = courseMapper.toEntity(courseAssignmentDTO);

                courseAssignmentEntity.setCourse(courseEntity);
                if(courseAssignmentDTO.getCurriculumId() != null){
                    courseAssignmentEntity.setCurriculum(curriculumQueryService.getCurriculumEntity(courseAssignmentDTO.getCurriculumId()));
                    courseAssignmentEntity.setProgram(courseAssignmentEntity.getCurriculum().getProgram());
                }

                validateCourseAssignment(courseAssignmentDTO);
                courseEntity.getCourseAssignments().add(courseAssignmentEntity);
            }
        }

        if(courseDTO.getDepartmentId() != null){
            courseEntity.setDepartment(departmentQueryService.getDepartmentEntity(courseDTO.getDepartmentId()));
        }

        courseRepository.save(courseEntity);
        return getCourse(courseEntity.getId());
    }

    public CourseDTO updateCourse(Long id, CourseDTO courseToModify){
        CourseEntity course = courseQueryService.getCourseEntity(id);

        if (courseToModify.getDepartmentId() != null){
            course.setDepartment(departmentQueryService.getDepartmentEntity(courseToModify.getDepartmentId()));
            course.setCode(courseCodeGenerator(courseToModify));
        }

        if (courseToModify.getPrerequisiteIds() != null){
            List <CourseEntity> prereqs = new ArrayList<>();
            for (Long prereqId : courseToModify.getPrerequisiteIds()){
                selfPrerequisiteCheck(course, prereqId);
                prereqs.add(courseQueryService.getCourseEntity(prereqId));
            }
            course.getPrerequisites().clear();
            course.setPrerequisites(prereqs);
        }

        if(courseToModify.getCourseAssignments() != null){

            checkCourseAssignmentsDuplicates(courseToModify);

            Map<Long, CourseAssignmentEntity> oldAssignments = new HashMap<>();

            for(CourseAssignmentEntity courseAssignmentEntity : course.getCourseAssignments()){
                oldAssignments.put(courseAssignmentEntity.getId(), courseAssignmentEntity);
            }

            List<CourseAssignmentEntity> newAssignments = new ArrayList<>();

            for(CourseAssignmentDTO courseAssignmentDTO : courseToModify.getCourseAssignments()){
                CourseAssignmentEntity courseAssignmentEntity = oldAssignments.remove(courseAssignmentDTO.getId());

                if (courseAssignmentEntity == null){
                    courseAssignmentEntity = new CourseAssignmentEntity();
                }

                courseMapper.updateEntityFromDTO(courseAssignmentDTO, courseAssignmentEntity);
                courseAssignmentEntity.setCurriculum(curriculumQueryService.getCurriculumEntity(courseAssignmentDTO.getCurriculumId()));
                courseAssignmentEntity.setProgram(courseAssignmentEntity.getCurriculum().getProgram());
                validateCourseAssignment(courseAssignmentDTO);
                newAssignments.add(courseAssignmentEntity);
            }

            course.getCourseAssignments().clear();
            course.getCourseAssignments().addAll(newAssignments);
        }
        
        if(courseToModify.getDepartmentId() != null){
            course.setDepartment(departmentQueryService.getDepartmentEntity(courseToModify.getDepartmentId()));
        }

        courseMapper.updateEntityFromDTO(courseToModify, course);
        courseRepository.save(course);
        return getCourse(id);
    }

    public CourseDTO getCourse(Long id){
        CourseEntity course = courseQueryService.getCourseEntity(id);

        List <Long> prereqIds = new ArrayList<>();
        if (course.getPrerequisites() != null){
            for (CourseEntity prereq : course.getPrerequisites()){
                prereqIds.add(prereq.getId());
            }
        }

        List<CourseAssignmentDTO> courseAssignmentDTOs = new ArrayList<>();
        if(course.getCourseAssignments() != null){
            for(CourseAssignmentEntity courseAssignmentEntity : course.getCourseAssignments()){
                courseAssignmentDTOs.add(courseMapper.toDto(courseAssignmentEntity));
            }
        }
        CourseDTO courseDto = courseMapper.toDto(course);
        courseDto.setPrerequisiteIds(prereqIds);
        courseDto.setCourseAssignments(courseAssignmentDTOs);
        return courseDto;
    }

    public List<CourseDTO> getAllCourses(String title, Long departmentId, Long programId, Long curriculumId, Integer yearLevel, Integer semester, Status status) {
        List<CourseDTO> courses = new ArrayList<>();

        for (CourseEntity course : courseRepository.search(title, departmentId, programId, curriculumId, yearLevel, semester, status)){
            courses.add(getCourse(course.getId()));
        }
        return courses;
    }

    private void validateCourseAssignment(CourseAssignmentDTO courseAssignmentDTO){
        CurriculumEntity curriculumAssignment = curriculumQueryService.getCurriculumEntity(courseAssignmentDTO.getCurriculumId());

        if (courseAssignmentDTO.getYearLevel() > curriculumAssignment.getDurationYear()){
            throw new RuntimeException("The year being assigned to does not exist");
        }

        YearEntity yearAssignment = new YearEntity();
        for (YearEntity year : curriculumAssignment.getYears()){
                if (courseAssignmentDTO.getYearLevel() == year.getLevel()){
                    yearAssignment = year;
                }
        }

        if (courseAssignmentDTO.getSemester() > yearAssignment.getTotalSemester()){
            throw new RuntimeException("The semester being assigned to does not exist");
        }
    }

    private void checkCourseAssignmentsDuplicates(CourseDTO courseDTO){
        List <CourseAssignmentDTO> courseAssignments = courseDTO.getCourseAssignments();
        
        if(courseAssignments != null){
            for (int i = 0; i < courseAssignments.size(); i++){
                CourseAssignmentDTO current = courseAssignments.get(i);

                for(int j = i + 1; j < courseAssignments.size(); j++){
                    CourseAssignmentDTO compare = courseAssignments.get(j);

                    if (current.getCurriculumId() == compare.getCurriculumId() &&
                        current.getSemester() == compare.getSemester() &&
                        current.getYearLevel() == compare.getYearLevel()
                    ){
                        throw new RuntimeException("Duplicated course assignments found");
                    }
                }
            }
        }
    }

    private void selfPrerequisiteCheck(CourseEntity courseEntity, Long prereqId ){
        if (prereqId == courseEntity.getId()){
            throw new RuntimeException("You cannot set a course to become it's own prerequisite");
        }
    }

    private String courseCodeGenerator(CourseDTO courseDTO){
        String depCode = departmentQueryService.getDepartmentEntity(courseDTO.getDepartmentId()).getCode();
        CourseEntity latest = courseRepository.findTopByDepartmentIdOrderByCodeDesc(courseDTO.getDepartmentId());
        int seq;
        
        if (latest == null){
            seq = 1;
        }
        else{
            String latestCode = latest.getCode().substring(latest.getCode().lastIndexOf("-")+1);
            seq = Integer.parseInt(latestCode) + 1;   
        }

        String code = depCode + "-" + String.format("%03d", seq);
        return code;
    }
}
