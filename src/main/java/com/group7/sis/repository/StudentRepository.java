package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.users.StudentEntity;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

}
