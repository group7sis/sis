package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.group7.sis.entity.users.student.AcademicInfoEntity;

public interface AcademicInfoRepository extends JpaRepository<AcademicInfoEntity, Long> {
    AcademicInfoEntity findEntityById(Long id);
}
