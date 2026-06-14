package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;

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

    @Query("""
    SELECT s FROM ScheduledCourseEntity s
    WHERE (:academicYear IS NULL OR s.academicYear = :academicYear)
    AND (:programId IS NULL OR s.courseAssignment.program.id = :programId)
    AND (:curriculumId IS NULL OR s.courseAssignment.curriculum.id = :curriculumId)
    AND (:semester IS NULL OR s.courseAssignment.semester = :semester)
    AND (:yearLevel IS NULL OR s.courseAssignment.yearLevel = :yearLevel)
    AND (:semester IS NULL OR s.courseAssignment.semester = :semester)
    AND (:sectionId IS NULL OR s.section.id = :sectionId)
    AND (:status IS NULL OR s.status = :status)
    """)
    List<ScheduledCourseEntity> search(
        @Param("academicYear") String academicYear,
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculumId,
        @Param("semester") Integer semester,
        @Param("sectionId") Long sectionId,
        @Param("status") ScheduleStatus status
    );
}
