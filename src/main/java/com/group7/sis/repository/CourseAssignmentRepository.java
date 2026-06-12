package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.academic.CourseAssignmentEntity;

public interface CourseAssignmentRepository extends JpaRepository<CourseAssignmentEntity, Long>{
}
