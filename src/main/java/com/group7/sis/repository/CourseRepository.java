package com.group7.sis.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.CourseEntity.Status;

import java.util.Optional;
import java.util.List;
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    Optional <CourseEntity> findByCode(String code);
    boolean existsByCode(String code);

    CourseEntity findTopByDepartmentIdOrderByCodeDesc(Long departmentId);

    @Query("""
    SELECT DISTINCT c from CourseEntity c
    LEFT JOIN c.courseAssignments ca
    WHERE (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')))
    AND (:departmentId IS NULL OR c.department.id = :departmentId)
    AND (:programId IS NULL OR ca.program.id = :programId)
    AND (:curriculumId IS NULL OR ca.curriculum.id = :curriculumId)
    AND (:yearLevel IS NULL OR ca.yearLevel = :yearLevel)
    AND (:semester IS NULL OR ca.semester = :semester)
    AND (:status IS NULL OR c.status = :status)
    """)
    List<CourseEntity> search(
        @Param("title") String title,
        @Param("departmentId") Long departmentId,
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculumId,
        @Param("yearLevel") Integer yearLevel,
        @Param("semester") Integer semester,
        @Param("status") Status status
    );

}
