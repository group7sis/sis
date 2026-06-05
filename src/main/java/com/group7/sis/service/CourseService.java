package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.dto.CourseDTO.CourseAssignmentDTO;
import com.group7.sis.entity.academic.CourseAssignmentEntity;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.mapper.CourseMapper;
import com.group7.sis.repository.CourseRepository;
import com.group7.sis.service.queryservices.CourseQueryService;
import com.group7.sis.service.queryservices.CurriculumQueryService;

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

    public CourseEntity addCourse(CourseDTO courseDTO){
        CourseEntity courseEntity = courseMapper.toEntity(courseDTO);

        if (courseDTO.getPrerequisiteIds() != null){
            for(Long id : courseDTO.getPrerequisiteIds()){
                courseEntity.getPrerequisites().add(courseQueryService.getCourseEntity(id));
            }
        }

        if (courseDTO.getCourseAssignments() != null){
            for (CourseAssignmentDTO courseAssignmentDTO : courseDTO.getCourseAssignments()){
                CourseAssignmentEntity courseAssignmentEntity = courseMapper.toEntity(courseAssignmentDTO);

                courseAssignmentEntity.setCourse(courseEntity);
                if(courseAssignmentDTO.getCurriculumId() != null){
                    courseAssignmentEntity.setCurriculum(curriculumQueryService.getCurriculumEntity(courseAssignmentDTO.getCurriculumId()));
                }
                courseEntity.getCourseAssignments().add(courseAssignmentEntity);
            }
        }
        return courseRepository.save(courseEntity);
    }

    public CourseDTO updateCourse(Long id, CourseDTO courseToModify){
        CourseEntity course = courseQueryService.getCourseEntity(id);

        if (courseToModify.getPrerequisiteIds() != null){
            List <CourseEntity> prereqs = new ArrayList<>();
            for (Long prereqId : courseToModify.getPrerequisiteIds()){
                prereqs.add(courseQueryService.getCourseEntity(prereqId));
            }
            course.getPrerequisites().clear();
            course.setPrerequisites(prereqs);
        }

        if(courseToModify.getCourseAssignments() != null){
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
                newAssignments.add(courseAssignmentEntity);
            }

            course.getCourseAssignments().clear();
            course.getCourseAssignments().addAll(newAssignments);
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

    public List<CourseEntity> getAllCourses() {
        return courseRepository.findAll();
    }
}
