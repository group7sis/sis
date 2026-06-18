package com.group7.sis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.entity.users.StudentEntity.ScholasticStatus;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByAcademicInfoId(Long id);

    List<StudentEntity> findByYearAdmitted(String yearAdmitted);

    long countDistinctByAcademicInfoSectionId(Long sectionId);

    long countDistinctByAcademicInfoEnrolledCoursesScheduleAssignedFacultyIdAndAcademicInfoAcademicYearAndAcademicInfoSemesterAndAcademicInfoEnrollmentStatus(Long facultyId, String academicYear, int semester, EnrollmentStatus enrollmentStatus);

    StudentEntity findTopByYearAdmittedOrderByStudentNumberDesc(String yearAdmitted);

    @Query("""
    SELECT DISTINCT s FROM StudentEntity s
    LEFT JOIN s.academicInfo ai
    WHERE(
        :name IS NULL OR LOWER(
            CONCAT(
                s.profile.firstName, ' ',
                CASE WHEN s.profile.middleName IS NULL THEN ''
                ELSE CONCAT(s.profile.middleName, ' ') END,
                s.profile.lastName
            )
        ) LIKE LOWER(CONCAT('%', :name, '%'))
    )
    AND (:studentStatus IS NULL OR s.studentStatus = :studentStatus)
    AND (:scholasticStatus IS NULL OR ai.scholasticStatus = :scholasticStatus)
    AND (:programId IS NULL OR ai.enrolledProgram.id = :programId)
    AND (:curriculumId IS NULL OR ai.programCurriculum.id = :curriculumId)
    AND (:academicYear IS NULL OR ai.academicYear = :academicYear)
    AND (:yearLevel IS NULL OR ai.yearLevel = :yearLevel)
    AND (:sectionId IS NULL OR ai.section.id = :sectionId)
    """)
    List<StudentEntity> search(
        @Param("name") String name,
        @Param("studentStatus") StudentStatus studentStatus,
        @Param("scholasticStatus") ScholasticStatus scholasticStatus,
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculum,
        @Param("academicYear") String academicYear,
        @Param("yearLevel") Integer yearLevel,
        @Param("sectionId") Long sectionId
    );

    @Query("""
    SELECT COUNT(DISTINCT s) FROM StudentEntity s
    LEFT JOIN s.academicInfo ai
    WHERE(
        :name IS NULL OR LOWER(
            CONCAT(
                s.profile.firstName, ' ',
                CASE WHEN s.profile.middleName IS NULL THEN ''
                ELSE CONCAT(s.profile.middleName, ' ') END,
                s.profile.lastName
            )
        ) LIKE LOWER(CONCAT('%', :name, '%'))
    )
    AND (:studentStatus IS NULL OR s.studentStatus = :studentStatus)
    AND (:scholasticStatus IS NULL OR ai.scholasticStatus = :scholasticStatus)
    AND (:programId IS NULL OR ai.enrolledProgram.id = :programId)
    AND (:curriculumId IS NULL OR ai.programCurriculum.id = :curriculumId)
    AND (:academicYear IS NULL OR ai.academicYear = :academicYear)
    AND (:yearLevel IS NULL OR ai.yearLevel = :yearLevel)
    AND (:sectionId IS NULL OR ai.section.id = :sectionId)
    """)
    long countSearch(
        @Param("name") String name,
        @Param("studentStatus") StudentStatus studentStatus,
        @Param("scholasticStatus") ScholasticStatus scholasticStatus,
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculum,
        @Param("academicYear") String academicYear,
        @Param("yearLevel") Integer yearLevel,
        @Param("sectionId") Long sectionId
    );

    @Query("""
    SELECT DISTINCT s FROM StudentEntity s
    LEFT JOIN s.academicInfo ai
    WHERE(
        :name IS NULL OR LOWER(
            CONCAT(
                s.profile.firstName, ' ',
                CASE WHEN s.profile.middleName IS NULL THEN ''
                ELSE CONCAT(s.profile.middleName, ' ') END,
                s.profile.lastName
            )
        ) LIKE LOWER(CONCAT('%', :name, '%'))
    )
    AND s.studentStatus = :studentStatus
    AND ai.section.id = :sectionId
    AND ai.enrolledCourses.schedule.courseAssignment.course.id = :courseId
    AND ai.enrolledCourses.schedule.courseAssignment.assignedFaculty.id = :facultyId
    AND ai.enrolledCourses.academicYear = :academicYear
    AND ai.enrolledCourses.courseAssignment.semester = :semester
    """)
    List<StudentEntity> getStudentsHandledByFaculty(
        @Param("name") String name,
        @Param("studentStatus") StudentStatus studentStatus,
        @Param("sectionId") Long sectionId,
        @Param("courseId") Long courseId,
        @Param("courseId") Long facultyId,
        @Param("academicYear") String academicYear,
        @Param("semester") int semester
    );
}   