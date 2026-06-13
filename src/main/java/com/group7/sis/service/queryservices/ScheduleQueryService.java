package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.repository.ScheduledCourseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleQueryService {
    private final ScheduledCourseRepository scheduledCourseRepository;

    public ScheduledCourseEntity getScheduledCourseEntity(Long id){
        return scheduledCourseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    public void checkForScheduleDuplicates(Long courseAssignmentId, Long sectionId, String academicYear){
        if(scheduledCourseRepository.existsByCourseAssignmentIdAndSectionIdAndAcademicYear(courseAssignmentId, sectionId, academicYear)){
            throw new RuntimeException("Schedule with course assignment '" + courseAssignmentId + "', section '" + sectionId + "', and year '" + academicYear + "' already exists");
        }        
    }
}