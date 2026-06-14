package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.FacultyEntity.EmploymentType;
import com.group7.sis.entity.users.FacultyEntity.Status;

import java.util.List;


public interface FacultyRepository extends JpaRepository<FacultyEntity, Long> {
    FacultyEntity findTopByDepartmentIdOrderByFacultyNumberDesc(Long departmentId);

    @Query("""
    SELECT DISTINCT f FROM FacultyEntity f
    LEFT JOIN f.qualifiedCourses qc
    LEFT JOIN f.scheduledCourses sc
    WHERE(
        :name IS NULL OR LOWER(
            CONCAT(
                f.profile.firstName, ' ',
                CASE WHEN f.profile.middleName IS NULL THEN ''
                ELSE CONCAT(f.profile.middleName, ' ') END,
                f.profile.lastName
            )
        ) LIKE LOWER(CONCAT('%', :name, '%'))
    )
    AND (:status IS NULL OR f.status = :status)
    AND (:departmentId IS NULL OR f.department.id = :departmentId)
    AND (:employmentType IS NULL OR f.employmentType = :employmentType)
    AND (:qualifiedCourseId IS NULL OR qc.id = :qualifiedCourseId)
    AND (:courseScheduleId IS NULL OR sc.courseAssignment.course.id = :courseScheduleId)        
    """)
    List<FacultyEntity> search(
        @Param("name") String name,
        @Param("status") Status status,
        @Param("departmentId") Long departmentId,
        @Param("employmentType") EmploymentType employmentType,
        @Param("qualifiedCourseId") Long qualifiedCourseId,
        @Param("courseScheduleId") Long courseScheduleId
    );

    @Query("""
    SELECT COUNT(DISTINCT f) FROM FacultyEntity f
    LEFT JOIN f.qualifiedCourses qc
    LEFT JOIN f.scheduledCourses sc
    WHERE(
        :name IS NULL OR LOWER(
            CONCAT(
                f.profile.firstName, ' ',
                CASE WHEN f.profile.middleName IS NULL THEN ''
                ELSE CONCAT(f.profile.middleName, ' ') END,
                f.profile.lastName
            )
        ) LIKE LOWER(CONCAT('%', :name, '%'))
    )
    AND (:status IS NULL OR f.status = :status)
    AND (:departmentId IS NULL OR f.department.id = :departmentId)
    AND (:employmentType IS NULL OR f.employmentType = :employmentType)
    AND (:qualifiedCourseId IS NULL OR qc.id = :qualifiedCourseId)
    AND (:courseScheduleId IS NULL OR sc.courseAssignment.course.id = :courseScheduleId)
    """)
    long countSearch(
        @Param("name") String name,
        @Param("status") Status status,
        @Param("departmentId") Long departmentId,
        @Param("employmentType") EmploymentType employmentType,
        @Param("qualifiedCourseId") Long qualifiedCourseId,
        @Param("courseScheduleId") Long courseScheduleId
    );
}
