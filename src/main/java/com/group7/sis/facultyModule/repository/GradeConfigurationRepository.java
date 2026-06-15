package com.group7.sis.facultyModule.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.group7.sis.facultyModule.entity.GradeConfigurationEntity;

public interface GradeConfigurationRepository extends JpaRepository<GradeConfigurationEntity, Long>{
    Optional<GradeConfigurationEntity> findByFacultyId(Long facultyId);
}
