package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.entity.academic.ProgramEntity.ProgramStatus;

import java.util.List;


public interface ProgramRepository extends JpaRepository<ProgramEntity, Long> {
    List<ProgramEntity> findListByCode(String code);

    ProgramEntity findByCode(String code);

    @Query("""
        SELECT p FROM ProgramEntity p
        WHERE (:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:status IS NULL OR p.status = :status)    
        """)
        List<ProgramEntity> search(
            @Param("title") String title,
            @Param("status") ProgramStatus status
        );
}
