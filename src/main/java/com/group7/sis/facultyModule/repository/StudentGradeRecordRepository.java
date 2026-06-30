package com.group7.sis.facultyModule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.facultyModule.entity.StudentGradeRecordEntity;

public interface StudentGradeRecordRepository extends JpaRepository<StudentGradeRecordEntity, Long>{

    StudentGradeRecordEntity findByEnrolledCourseId(Long enrolledCourseId);
    
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
    AND sgr.enrolledCourse.academicInfo.student.studentStatus = :studentStatus
    AND sgr.enrolledCourse.schedule.academicYear = :academicYear 
    AND sgr.enrolledCourse.schedule.courseAssignment.semester = :semester
    AND sgr.enrolledCourse.schedule.section.id = :sectionId
    AND sgr.enrolledCourse.schedule.courseAssignment.course.id = :courseId
    AND sgr.enrolledCourse.schedule.assignedFaculty.id = :facultyId
    """)
    List<StudentGradeRecordEntity> getRecordsFromSection(
        @Param("name") String name,
        @Param("studentStatus") StudentStatus status,
        @Param("academicYear") String academicYear, 
        @Param("semester") int semester,
        @Param("sectionId") Long sectionId,
        @Param("courseId") Long courseId,
        @Param("facultyId") Long facultyId
    );

    @Query("""
    SELECT COUNT(sgr) FROM StudentGradeRecordEntity sgr 
    WHERE sgr.enrolledCourse.academicInfo.student.studentStatus = :studentStatus
    AND sgr.enrolledCourse.schedule.academicYear = :academicYear 
    AND sgr.enrolledCourse.schedule.courseAssignment.semester = :semester
    AND sgr.enrolledCourse.schedule.section.id = :sectionId
    AND sgr.enrolledCourse.schedule.courseAssignment.course.id = :courseId
    AND sgr.enrolledCourse.schedule.assignedFaculty.id = :facultyId
    """)
    long countStudentsFromSection(
        @Param("studentStatus") StudentStatus studentStatus,
        @Param("academicYear") String academicYear, 
        @Param("semester") int semester,
        @Param("sectionId") Long sectionId,
        @Param("courseId") Long courseId,
        @Param("facultyId") Long facultyId 
    );
}
