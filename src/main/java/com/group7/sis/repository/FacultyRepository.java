package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.users.FacultyEntity;


public interface FacultyRepository extends JpaRepository<FacultyEntity, Long> {
    FacultyEntity findTopByOrderByIdDesc();
}
