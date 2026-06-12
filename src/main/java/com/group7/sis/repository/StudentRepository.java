package com.group7.sis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByAcademicInfoId(Long id);

    List<StudentEntity> findByAcademicInfoEnrollmentStatusAndAcademicInfoSectionIdAndAcademicInfoAcademicYearAndAcademicInfoSemester(
        EnrollmentStatus enrollmentStatus, Long sectionId,
        String academicYear, int semester
    );

    List<StudentEntity> findByYearAdmitted(String yearAdmitted);

    StudentEntity findTopByYearAdmittedOrderByStudentNumberDesc(String yearAdmitted);
}
