package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.mapper.CourseMapper;
import com.group7.sis.repository.CourseRepository;

import java.util.List;
import java.util.ArrayList;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseEntity addCourse(CourseDTO request) {
        List <CourseEntity> prereqs = new ArrayList<>();

        if (request.getPrerequisiteIds() != null){
            for (Long id : request.getPrerequisiteIds()){
                prereqs.add(getCourseEntity(id));
            }
        }
        
        CourseEntity course = courseMapper.toEntity(request);
        course.setPrerequisites(prereqs);
        return courseRepository.save(course);
    }

    public CourseDTO updateCourse(Long id, CourseDTO courseToModify){
        CourseEntity course = getCourseEntity(id);

        if (courseToModify.getPrerequisiteIds() != null){
            List <CourseEntity> prereqs = new ArrayList<>();
            for (Long prereqId : courseToModify.getPrerequisiteIds()){
                prereqs.add(getCourseEntity(prereqId));
            }
            course.getPrerequisites().clear();
            course.setPrerequisites(prereqs);
        }

        courseMapper.updateEntityFromDTO(courseToModify, course);
        courseRepository.save(course);
        return getCourse(id);
    }

    public CourseDTO getCourse(Long id){
        CourseEntity course = getCourseEntity(id);

        List <Long> prereqIds = new ArrayList<>();
        if (course.getPrerequisites() != null){
            for (CourseEntity prereq : course.getPrerequisites()){
                prereqIds.add(prereq.getId());
            }
        }

        CourseDTO courseDto = courseMapper.toDto(course);
        courseDto.setPrerequisiteIds(prereqIds);
        return courseDto;
    }

    public List<CourseEntity> getAllCourses() {
        return courseRepository.findAll();
    }


    //Helpers
    public CourseEntity getCourseEntity(Long id){
        return courseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Course does not exist."));
    }
}
