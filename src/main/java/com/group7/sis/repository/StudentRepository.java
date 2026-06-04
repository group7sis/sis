package com.group7.sis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByAcademicInfoId(Long id);

    @Query("""
            SELECT DISTINCT s
            FROM StudentEntity s
            JOIN s.academicInfo ai
            WHERE ai.enrollmentStatus = :status
            AND ai.section.id = :sectionId
            """)
    List<StudentEntity> findStudentsByStatusNSection(
        @Param("status") EnrollmentStatus status,
        @Param("sectionId") Long sectionId
    );
}
