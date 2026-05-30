package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.group7.sis.entity.academic.ProgramEntity;
import java.util.List;


public interface ProgramRepository extends JpaRepository<ProgramEntity, Long> {
    List<ProgramEntity> findListByCode(String code);

    ProgramEntity findByCode(String code);
}
