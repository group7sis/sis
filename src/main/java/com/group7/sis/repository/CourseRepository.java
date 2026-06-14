package com.group7.sis.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.group7.sis.entity.academic.CourseEntity;
import java.util.Optional;
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    Optional <CourseEntity> findByCode(String code);
    boolean existsByCode(String code);

    CourseEntity findTopByDepartmentIdOrderByCodeDesc(Long departmentId);
}
