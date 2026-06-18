package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.users.student.EnrolledCourseEntity;

public interface EnrolledCourseRepository extends JpaRepository <EnrolledCourseEntity, Long>{
}
