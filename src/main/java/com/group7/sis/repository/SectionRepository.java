package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.SectionEntity;
import com.group7.sis.entity.academic.SectionEntity.Status;

import java.util.List;

public interface SectionRepository extends JpaRepository<SectionEntity, Long> {
    SectionEntity findTopByProgramIdOrderBySequenceDesc(Long programId);

    @Query("""
    SELECT s FROM SectionEntity s
    WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND (:programId IS NULL OR s.program.id = :programId)
    AND (:yearLevel IS NULL OR s.yearLevel = :yearLevel)
    AND (:status IS NULL OR s.status = :status)
    """)
    List<SectionEntity> search(
        @Param("name") String name,
        @Param("programId") Long programId,
        @Param("yearLevel") Integer yearLevel,
        @Param("status") Status status
    );

    @Query("""
    SELECT COUNT(s) FROM SectionEntity s
    WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND (:programId IS NULL OR s.program.id = :programId)
    AND (:yearLevel IS NULL OR s.yearLevel = :yearLevel)
    AND (:status IS NULL OR s.status = :status)
    """)
    long countSearch(
        @Param("name") String name,
        @Param("programId") Long programId,
        @Param("yearLevel") Integer yearLevel,
        @Param("status") Status status
    );
}