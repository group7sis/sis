package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.mapper.CourseMapper;
import com.group7.sis.repository.CourseRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseEntity addCourse(CourseDTO request) {
        if (courseRepository.existsByCode(request.getCode())){
            throw new RuntimeException("Course with code '" +request.getCode()+"' already exists");
        }
        return courseRepository.save(courseMapper.toEntity(request));
    }

    public CourseEntity updateCourseByCode(String code, CourseDTO courseToModify){
        CourseEntity course = courseRepository.findByCode(code)
        .orElseThrow(() -> new RuntimeException("Course does not exist."));

        courseMapper.updateEntityFromDTO(courseToModify, course);
        return courseRepository.save(course);
    }

    public CourseEntity getCourseByCode(String code) { 
        return courseRepository.findByCode(code)
        .orElseThrow(() -> new RuntimeException("Course does not exist."));
    }

    public List<CourseEntity> getAllCourses() {
        return courseRepository.findAll();
    }
}
