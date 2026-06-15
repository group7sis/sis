package com.group7.sis.facultyModule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.facultyModule.entity.StudentGradeRecordEntity;

public interface StudentGradeRecordRepository extends JpaRepository<StudentGradeRecordEntity, Long>{

    @Query("""
    SELECT sgr FROM StudentGradeRecordEntity sgr
    WHERE(
        :name IS NULL OR LOWER(
            CONCAT(
                sgr.enrolledCourse.academicInfo.student.profile.firstName, ' ',
                CASE WHEN sgr.enrolledCourse.academicInfo.student.profile.middleName IS NULL THEN ''
                ELSE CONCAT(sgr.enrolledCourse.academicInfo.student.profile.middleName, ' ') END,
                sgr.enrolledCourse.academicInfo.student.profile.lastName
            )
        ) LIKE LOWER(CONCAT('%', :name, '%'))
    )
    AND (:programId IS NULL OR sgr.enrolledCourse.schedule.courseAssignment.program.id = :programId)
    AND (:yearLevel IS NULL OR sgr.enrolledCourse.schedule.courseAssignment.yearLevel = :yearLevel)
    AND (:courseId IS NULL OR sgr.enrolledCourse.schedule.courseAssignment.course.id = :courseId)
    AND (:sectionId IS NULL OR sgr.enrolledCourse.schedule.section.id = :sectionId)
    AND (:academicYear IS NULL OR sgr.enrolledCourse.schedule.academicYear = :academicYear)
    AND (:semester IS NULL OR sgr.enrolledCourse.schedule.courseAssignment.semester = :semester)
    """)
    List<StudentGradeRecordEntity> searchGradeRecords(
        @Param("name") String name,
        @Param("programId") Long programId,
        @Param("yearLevel") Integer yearLevel,
        @Param("courseId") Long courseId,
        @Param("sectionId") Long sectionId,  
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester
    );
}
