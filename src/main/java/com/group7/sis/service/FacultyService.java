package com.group7.sis.service;

import com.group7.sis.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.GetFacultyDTO;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.faculty.QualifiedCourseEntity;
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

    public GetFacultyDTO addFaculty(FacultyDTO facultyDTO){
        FacultyEntity faculty = facultyMapper.toEntity(facultyDTO);
        
        if (facultyDTO.getQualifiedCoursesIds() != null && !facultyDTO.getQualifiedCoursesIds().isEmpty()){
            for (Long id : facultyDTO.getQualifiedCoursesIds()){
                QualifiedCourseEntity qualifiedCourseEntity = new QualifiedCourseEntity();
                qualifiedCourseEntity.setCourse(courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Qualified courses could not be added due to a Course not being found")));
                qualifiedCourseEntity.setFaculty(faculty);

                faculty.getQualifiedCourses().add(qualifiedCourseEntity);
            }
        }
        facultyRepository.save(faculty);
        return getFaculty(faculty.getId());
    }

    public GetFacultyDTO updateFaculty(Long id, FacultyDTO facultyDTO){
        FacultyEntity facultyEntity = facultyRepository.findById(id).orElseThrow(() -> new RuntimeException("Faculty not found"));
        facultyMapper.updateEntityFromDto(facultyDTO, facultyEntity);

        if (facultyDTO.getQualifiedCoursesIds() != null){
            facultyEntity.getQualifiedCourses().clear();

            for (Long qcId : facultyDTO.getQualifiedCoursesIds()){
                CourseEntity course = courseRepository.findById(qcId).orElseThrow(() -> new RuntimeException("Faculty could not be modified due to a Course not being found"));
                QualifiedCourseEntity qualifiedCourseEntity = new QualifiedCourseEntity();

                qualifiedCourseEntity.setFaculty(facultyEntity);
                qualifiedCourseEntity.setCourse(course);

                facultyEntity.getQualifiedCourses().add(qualifiedCourseEntity);
            }
        }
        facultyRepository.save(facultyEntity);
        return getFaculty(facultyEntity.getId());
    }

    public GetFacultyDTO getFaculty(Long id){
        FacultyEntity facultyEntity = facultyRepository.findById(id).orElseThrow(() -> new RuntimeException("Faculty not found"));

        GetFacultyDTO faculty = facultyMapper.toDto(facultyEntity);

        if (facultyEntity.getQualifiedCourses() != null && !facultyEntity.getQualifiedCourses().isEmpty()){
            for (QualifiedCourseEntity qualifiedCourseEntity : facultyEntity.getQualifiedCourses()){
                CourseEntity course = qualifiedCourseEntity.getCourse();

                faculty.getQualifiedCourses().add(course);
            }
        }
        return faculty;
    }

    public List<GetFacultyDTO> getAllFaculty(){
        List<GetFacultyDTO> faculties = new ArrayList<>();

        for (FacultyEntity facultyEntity : facultyRepository.findAll()){
            faculties.add(getFaculty(facultyEntity.getId()));
        }

        return faculties;
    }

    //for debugging
    public List<FacultyEntity> getAllFacultyEntity(){
        return facultyRepository.findAll();
    }

}
