package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.academic.YearEntity;


public interface YearRepository extends JpaRepository<YearEntity, Long>{
    YearEntity findByLevel(int level);
}
