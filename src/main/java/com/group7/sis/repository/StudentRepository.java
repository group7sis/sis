package com.group7.sis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.entity.users.StudentEntity.ScholasticStatus;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByAccountUsernameAndAccountPassword(String username, String password);

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
    LEFT JOIN ai.enrolledCourses ec
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
    AND ec.schedule.section.id = :sectionId
    AND ec.schedule.courseAssignment.course.id = :courseId
    AND ec.schedule.assignedFaculty.id = :facultyId
    AND ec.schedule.academicYear = :academicYear
    AND ec.schedule.courseAssignment.semester = :semester
    """)
    List<StudentEntity> getStudentsHandledByFaculty(
        @Param("name") String name,
        @Param("studentStatus") StudentStatus studentStatus,
        @Param("sectionId") Long sectionId,
        @Param("courseId") Long courseId,
        @Param("facultyId") Long facultyId,
        @Param("academicYear") String academicYear,
        @Param("semester") int semester
    );

    @Query("""
        SELECT DISTINCT ai FROM StudentEntity s
        LEFT JOIN s.academicInfo ai
        WHERE s.id = :studentId 
        AND ai.academicYear = :academicYear
        AND ai.semester = :semester
    """)
    AcademicInfoEntity getEnrollmentByYearAndSemester(
        @Param("studentId") Long studentId, 
        @Param("academicYear") String academicYear, 
        @Param("semester") Integer semester
    );

    @Query("""
        SELECT ai FROM StudentEntity s
        LEFT JOIN s.academicInfo ai
        WHERE s.id = :studentId
        AND ai.enrollmentStatus = :enrollmentStatus
        ORDER BY ai.enrollmentDate DESC
        LIMIT 1
    """)
    AcademicInfoEntity getLatestEnrollment(@Param("studentId") Long studentId, @Param("enrollmentStatus") EnrollmentStatus enrollmentStatus);
}   