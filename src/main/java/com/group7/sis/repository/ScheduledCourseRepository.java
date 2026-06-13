package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.academic.ScheduledCourseEntity;
import java.util.List;


public interface ScheduledCourseRepository extends JpaRepository<ScheduledCourseEntity, Long>{
    ScheduledCourseEntity findTopByAcademicYearOrderByCodeDesc(String academicYear);

    ScheduledCourseEntity findByCourseAssignmentIdAndSectionIdAndAcademicYear
    (
        Long courseAssignmentId,
        Long sectionId,
        String academicYear
    );

    List<ScheduledCourseEntity> findByRoomAndAcademicYearAndCourseAssignmentId(String room, String academicYear, Long courseAssignmentId);

    List<ScheduledCourseEntity> findBySectionIdAndAcademicYearAndCourseAssignmentId(Long id, String academicYear, Long courseAssignmentId);

    List<ScheduledCourseEntity> findByAssignedFacultyIdAndAcademicYearAndCourseAssignmentId(Long id, String academicYear, Long courseAssignmentId);
}
