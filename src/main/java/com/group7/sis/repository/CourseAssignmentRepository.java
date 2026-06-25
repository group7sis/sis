package com.group7.sis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.CourseAssignmentEntity;
import com.group7.sis.entity.academic.CourseEntity.Status;

public interface CourseAssignmentRepository extends JpaRepository<CourseAssignmentEntity, Long>{

    @Query("""
    SELECT cae FROM CourseAssignmentEntity cae
    WHERE cae.program.id = :programId
    AND cae.curriculum.id = :curriculumId
    AND cae.yearLevel = :yearLevel
    AND cae.semester = :semester
    AND cae.course.status = :courseStatus
    """)
    List<CourseAssignmentEntity> getCourseAssignment(
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculumId,
        @Param("yearLevel") int yearLevel,
        @Param("semester") int semester,
        @Param("courseStatus") Status courseStatus
    );
}
