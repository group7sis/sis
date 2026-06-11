package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseQueryService {
    private final CourseRepository courseRepository;   

    public CourseEntity getCourseEntity(Long id){
        return courseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Course does not exist."));
    }
}