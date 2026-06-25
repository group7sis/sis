//ALL THESE IGNORE SCHEDULEDCOURSES

package com.group7.sis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.FacultyEntity.EmploymentType;
import com.group7.sis.entity.users.FacultyEntity.Status;
import com.group7.sis.facultyModule.entity.GradeConfigurationEntity;
import com.group7.sis.facultyModule.repository.GradeConfigurationRepository;
import com.group7.sis.dto.FacultyDTO;
import com.group7.sis.dto.pagedResponse.FacultyPageDTO;
import com.group7.sis.dto.statsDtos.FacultyStatsDTO;
import com.group7.sis.mapper.FacultyMapper;
import com.group7.sis.repository.FacultyRepository;
import com.group7.sis.service.queryservices.CourseQueryService;
import com.group7.sis.service.queryservices.DepartmentQueryService;
import com.group7.sis.service.queryservices.FacultyQueryService;
// import com.group7.sis.service.queryservices.ScheduleQueryService;
import com.group7.sis.service.queryservices.ScheduleQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacultyService {
    private final FacultyQueryService facultyQueryService;
    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;

    private final CourseQueryService courseQueryService;
    private final DepartmentQueryService departmentQueryService;
    private final ScheduleQueryService scheduleQueryService;

    private final GradeConfigurationRepository gradeConfigurationRepository;

    public FacultyDTO addFaculty(FacultyDTO facultyDTO){
        FacultyEntity faculty = facultyMapper.toEntity(facultyDTO);
        departmentValidation(facultyDTO);

        if(facultyDTO.getDepartmentId() != null){
            faculty.setDepartment(departmentQueryService.getDepartmentEntity(facultyDTO.getDepartmentId()));
        }

        faculty.setFacultyNumber(facultyNumberGenerator(facultyDTO.getDepartmentId()));

        if (facultyDTO.getQualifiedCoursesIds() != null){

            checkForQualifiedCoursesIdsDuplicates(facultyDTO.getQualifiedCoursesIds());
            for (Long id : facultyDTO.getQualifiedCoursesIds()){
                CourseEntity course = courseQueryService.getCourseEntity(id);
                faculty.getQualifiedCourses().add(course);
            }
        }

        // if (facultyDTO.getScheduledCoursesIds() != null){
        //     for (Long id : facultyDTO.getScheduledCoursesIds()){
        //         ScheduledCourseEntity schedule = scheduleQueryService.getScheduledCourseEntity(id);
        //         faculty.getScheduledCourses().add(schedule);
        //     }
        // }
        GradeConfigurationEntity config = new GradeConfigurationEntity();
        config.setFaculty(faculty);
        config.setAssignmentWeight(30);
        config.setQuizWeight(20);
        config.setActivityWeight(20);
        config.setFinalExamWeight(30);

        facultyRepository.save(faculty);
        gradeConfigurationRepository.save(config);
        return getFaculty(faculty.getId());
    }

    public FacultyDTO updateFaculty(Long id, FacultyDTO facultyDTO){
        FacultyEntity facultyEntity = facultyQueryService.getFacultyEntity(id);

        departmentValidation(facultyDTO);
        facultyMapper.updateEntityFromDto(facultyDTO, facultyEntity);

        if(facultyDTO.getDepartmentId() != null){
            facultyEntity.setDepartment(departmentQueryService.getDepartmentEntity(facultyDTO.getDepartmentId()));
        }

        facultyEntity.setFacultyNumber(facultyNumberGenerator(facultyDTO.getDepartmentId()));

        if (facultyDTO.getQualifiedCoursesIds() != null){
            checkForQualifiedCoursesIdsDuplicates(facultyDTO.getQualifiedCoursesIds());
            List<CourseEntity> newQualifiedCourses = new ArrayList<>();
            for (Long courseId : facultyDTO.getQualifiedCoursesIds()){
                CourseEntity course = courseQueryService.getCourseEntity(courseId);
                newQualifiedCourses.add(course);
            }
            facultyEntity.getQualifiedCourses().clear();
            facultyEntity.getQualifiedCourses().addAll(newQualifiedCourses);
        }

        // if (facultyDTO.getScheduledCoursesIds() != null){
        //     List<ScheduledCourseEntity> newScheduledCourses = new ArrayList<>();
        //     for (Long scheduleId : facultyDTO.getQualifiedCoursesIds()){
        //         ScheduledCourseEntity course = scheduleQueryService.getScheduledCourseEntity(scheduleId);
        //         newScheduledCourses.add(course);
        //     }
        //     facultyEntity.getScheduledCourses().clear();
        //     facultyEntity.getScheduledCourses().addAll(newScheduledCourses);
        // }

        facultyRepository.save(facultyEntity);
        return getFaculty(facultyEntity.getId());
    }

    public FacultyDTO getFaculty(Long id){
        FacultyEntity facultyEntity = facultyQueryService.getFacultyEntity(id);

        FacultyDTO faculty = facultyMapper.toDto(facultyEntity);

        if (facultyEntity.getQualifiedCourses() != null){
            for (CourseEntity courseEntity : facultyEntity.getQualifiedCourses()){
                faculty.getQualifiedCoursesIds().add(courseEntity.getId());
            }
        }

        if (facultyEntity.getScheduledCourses() != null){
            for (ScheduledCourseEntity schedule : facultyEntity.getScheduledCourses()){
                faculty.getScheduledCoursesIds().add(schedule.getId());
            }
        }
        return faculty;
    }

    public FacultyPageDTO getAllFacultyDTO(String name, Status status, Long departmentId, EmploymentType employmentType, Long qualifiedCourseId, Long courseScheduleId){
        FacultyPageDTO page = new FacultyPageDTO();
        List<FacultyDTO> faculties = new ArrayList<>();

        for (FacultyEntity facultyEntity : facultyRepository.search(name, status, departmentId, employmentType, qualifiedCourseId, courseScheduleId)){
            faculties.add(getFaculty(facultyEntity.getId()));
        }

        FacultyStatsDTO stats = getFacultyStats(name, status, departmentId, employmentType, qualifiedCourseId, courseScheduleId);
        page.setFaculties(faculties);
        page.setStats(stats);
        return page;
    }

    private FacultyStatsDTO getFacultyStats(String name, Status status, Long departmentId, EmploymentType employmentType, Long qualifiedCourseId, Long courseScheduleId){
        FacultyStatsDTO stats = new FacultyStatsDTO();

        stats.setTotalFaculty(facultyRepository.countSearch(name, status, departmentId, employmentType, qualifiedCourseId, courseScheduleId));
        stats.setTotalActiveFaculty(facultyRepository.countSearch(name, Status.ACTIVE, departmentId, employmentType, qualifiedCourseId, courseScheduleId));
        stats.setTotalInactiveFaculty(facultyRepository.countSearch(name, Status.INACTIVE, departmentId, employmentType, qualifiedCourseId, courseScheduleId));
        stats.setTotalSchedulesAssignedPerFaculty(computeTotalSchedules(facultyRepository.search(name, status, departmentId, employmentType, qualifiedCourseId, courseScheduleId)));

        return stats;
    }

    private long computeTotalSchedules(List<FacultyEntity> faculties){
        long total = 0;

        for(FacultyEntity faculty : faculties){
            total += scheduleQueryService.getTotalSchedulesByFaculty(faculty.getId());
        }
        return total;
    }

    //for debugging
    public List<FacultyEntity> getAllFaculty(){
        return facultyRepository.findAll();
    }

    private String facultyNumberGenerator(Long departmentId){
        String depCode = departmentId == null ? "" : departmentQueryService.getDepartmentEntity(departmentId).getCode() + "-";
        FacultyEntity latest = facultyRepository.findTopByDepartmentIdOrderByFacultyNumberDesc(departmentId);
        int seq;

        if (latest == null){
            seq = 1;
        }
        else{ 
            String ref = latest.getFacultyNumber().substring(latest.getFacultyNumber().lastIndexOf("-")+1);
            seq = Integer.parseInt(ref) + 1;
        }
        
        String id = "FAC-" + depCode + String.format("%04d", seq);
        return id;
    }

    private void checkForQualifiedCoursesIdsDuplicates(List <Long> qualifiedCoursesIds) {

        for (int i = 0; i < qualifiedCoursesIds.size(); i++){
            Long current = qualifiedCoursesIds.get(i);
            
            for(int j = i + 1; j < qualifiedCoursesIds.size(); j++){
                Long compare = qualifiedCoursesIds.get(j);

                if(current == compare){
                    throw new RuntimeException("Duplicates found. Course ID '" + current + "' was already entered");
                }
            }
        }
    }

    private void departmentValidation(FacultyDTO facultyDTO){
        if(facultyDTO.getEmploymentType() == EmploymentType.FULL_TIME && facultyDTO.getDepartmentId() == null){
            throw new RuntimeException("A faculty with FULL TIME employment must be assigned to a department");
        }
    }
}
