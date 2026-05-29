package com.group7.sis.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.group7.sis.entity.academic.CourseEntity;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    CourseEntity findByCode(String code);
    boolean existsByCode(String code);
}
