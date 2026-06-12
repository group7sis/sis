package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.group7.sis.entity.academic.SectionEntity;

public interface SectionRepository extends JpaRepository<SectionEntity, Long> {
    SectionEntity findTopByProgramIdOrderBySequenceDesc(Long programId);
}