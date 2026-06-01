package com.group7.sis.service;

import com.group7.sis.repository.CourseRepository;

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
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;

    public FacultyEntity addFaculty(FacultyDTO facultyDTO){
        FacultyEntity faculty = facultyMapper.toEntity(facultyDTO);
        
        if (facultyDTO.getQualifiedCoursesIds() != null && !facultyDTO.getQualifiedCoursesIds().isEmpty()){
            for (Long id : facultyDTO.getQualifiedCoursesIds()){
                CourseEntity course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Faculty could not be created due to a Course not being found"));
                faculty.getQualifiedCourses().add(course);
            }
        }
        facultyRepository.save(faculty);
        return faculty;
    }

    public FacultyDTO updateFaculty(Long id, FacultyDTO facultyDTO){
        FacultyEntity facultyEntity = facultyRepository.findById(id).orElseThrow(() -> new RuntimeException("Faculty not found"));
        facultyMapper.updateEntityFromDto(facultyDTO, facultyEntity);
        
        if (facultyDTO.getQualifiedCoursesIds() != null){
            facultyEntity.getQualifiedCourses().clear();

            for (Long courseId : facultyDTO.getQualifiedCoursesIds()){
                CourseEntity course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Faculty could not be modified due to a Course not being found"));
                facultyEntity.getQualifiedCourses().add(course);
            }
        }
        facultyRepository.save(facultyEntity);
        return getFaculty(facultyEntity.getId());
    }

    public FacultyDTO getFaculty(Long id){
        FacultyEntity facultyEntity = facultyRepository.findById(id).orElseThrow(() -> new RuntimeException("Faculty not found"));

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

}//ALL THESE IGNORE SCHEDULEDCOURSES
