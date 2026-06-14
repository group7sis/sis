package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.CourseAssignmentEntity;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.repository.CourseAssignmentRepository;
import com.group7.sis.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CourseQueryService {
    private final CourseRepository courseRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;   

    public CourseEntity getCourseEntity(Long id){
        return courseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Course does not exist."));
    }

    public CourseAssignmentEntity getCourseAssignmentEntity(Long id){
        return courseAssignmentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Course Assignment does not exist"));
    }

    public long getTotalCourses(){
        return courseAssignmentRepository.count();
    }
}