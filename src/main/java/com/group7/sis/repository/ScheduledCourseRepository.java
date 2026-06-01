package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.academic.ScheduledCourseEntity;

public interface ScheduledCourseRepository extends JpaRepository<ScheduledCourseEntity, Long>{
}
