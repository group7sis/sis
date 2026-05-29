package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.group7.sis.entity.academic.ProgramEntity;
import java.util.List;


public interface ProgramRepository extends JpaRepository<ProgramEntity, Long> {
    List<ProgramEntity> findByCode(String code);
}
