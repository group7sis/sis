package com.group7.sis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.dto.FacultyDTO;
import com.group7.sis.mapper.FacultyMapper;
import com.group7.sis.repository.FacultyRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;

    private final CourseService courseService;

    public FacultyEntity addFaculty(FacultyDTO facultyDTO){
        FacultyEntity faculty = facultyMapper.toEntity(facultyDTO);
        
        if (facultyDTO.getQualifiedCoursesIds() != null && !facultyDTO.getQualifiedCoursesIds().isEmpty()){
            for (Long id : facultyDTO.getQualifiedCoursesIds()){
                CourseEntity course = courseService.getCourseEntity(id);
                faculty.getQualifiedCourses().add(course);
            }
        }
        facultyRepository.save(faculty);
        return faculty;
    }

    public FacultyDTO updateFaculty(Long id, FacultyDTO facultyDTO){
        FacultyEntity facultyEntity = getFacultyEntity(id);
        facultyMapper.updateEntityFromDto(facultyDTO, facultyEntity);
        
        if (facultyDTO.getQualifiedCoursesIds() != null){
            facultyEntity.getQualifiedCourses().clear();

            for (Long courseId : facultyDTO.getQualifiedCoursesIds()){
                CourseEntity course = courseService.getCourseEntity(courseId);
                facultyEntity.getQualifiedCourses().add(course);
            }
        }
        facultyRepository.save(facultyEntity);
        return getFaculty(facultyEntity.getId());
    }

    public FacultyDTO getFaculty(Long id){
        FacultyEntity facultyEntity = getFacultyEntity(id);

        FacultyDTO faculty = facultyMapper.toDto(facultyEntity);

        if (facultyEntity.getQualifiedCourses() != null && !facultyEntity.getQualifiedCourses().isEmpty()){
            for (CourseEntity courseEntity : facultyEntity.getQualifiedCourses()){
                faculty.getQualifiedCoursesIds().add(courseEntity.getId());
            }
        }

        if (facultyEntity.getScheduledCourses() != null && !facultyEntity.getScheduledCourses().isEmpty()){
            for (ScheduledCourseEntity schedule : facultyEntity.getScheduledCourses()){
                faculty.getScheduledCoursesIds().add(schedule.getId());
            }
        }
        return faculty;
    }

    public List<FacultyDTO> getAllFacultyDTO(){
        List<FacultyDTO> faculties = new ArrayList<>();

        for (FacultyEntity facultyEntity : facultyRepository.findAll()){
            faculties.add(getFaculty(facultyEntity.getId()));
        }
        return faculties;
    }

    //for debugging
    public List<FacultyEntity> getAllFaculty(){
        return facultyRepository.findAll();
    }


    //Helpers
    public FacultyEntity getFacultyEntity(Long id){
        return facultyRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Faculty not found"));
    }
}//ALL THESE IGNORE SCHEDULEDCOURSES
