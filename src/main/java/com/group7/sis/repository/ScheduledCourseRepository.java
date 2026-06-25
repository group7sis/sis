package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.academic.SectionEntity;
import com.group7.sis.entity.academic.DayEntity.DayName;
import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;

import java.util.List;


public interface ScheduledCourseRepository extends JpaRepository<ScheduledCourseEntity, Long>{
    ScheduledCourseEntity findTopByAcademicYearOrderByCodeDesc(String academicYear);

    ScheduledCourseEntity findByCourseAssignmentIdAndSectionIdAndAcademicYear
    (
        Long courseAssignmentId,
        Long sectionId,
        String academicYear
    );

    List<ScheduledCourseEntity> findByRoomAndAcademicYearAndCourseAssignmentId(String room, String academicYear, Long courseAssignmentId);

    List<ScheduledCourseEntity> findBySectionIdAndAcademicYearAndCourseAssignmentId(Long id, String academicYear, Long courseAssignmentId);

    List<ScheduledCourseEntity> findByAssignedFacultyIdAndAcademicYearAndCourseAssignmentId(Long id, String academicYear, Long courseAssignmentId);

    List<ScheduledCourseEntity> findByAssignedFacultyIdAndAcademicYearAndCourseAssignmentSemesterAndDaysNameAndStatus(Long facultyId, String academicYear, int semester, DayName dayName, ScheduleStatus status);

    @Query("""
    SELECT COUNT(DISTINCT s.section.id) FROM ScheduledCourseEntity s 
    WHERE s.assignedFaculty.id = :facultyId 
    AND s.academicYear = :academicYear 
    AND s.courseAssignment.semester = :semester
    AND s.status = :status
    """)
    long countSectionsHandledByFaculty(String academicYear, int semester, Long facultyId, ScheduleStatus status);

    long countByAssignedFacultyIdAndAcademicYearAndCourseAssignmentSemesterAndStatus(Long facultyId, String academicYear, int semester, ScheduleStatus status);

    long countByAssignedFacultyId(Long facultyId);

    @Query("""
    SELECT COUNT(DISTINCT s.courseAssignment.course.id) FROM ScheduledCourseEntity s 
    WHERE s.assignedFaculty.id = :facultyId 
    AND s.academicYear = :academicYear 
    AND s.courseAssignment.semester = :semester
    AND s.status = :status
    """)
    long countCoursesHandledByFaculty(
        @Param("academicYear") String academicYear, 
        @Param("semester") int semester, 
        @Param("facultyId") Long facultyId,
        @Param("status") ScheduleStatus status
    );
    
    @Query("""
    SELECT DISTINCT s FROM ScheduledCourseEntity s
    LEFT JOIN s.days d
    WHERE (:academicYear IS NULL OR s.academicYear = :academicYear)
    AND (:programId IS NULL OR s.courseAssignment.program.id = :programId)
    AND (:curriculumId IS NULL OR s.courseAssignment.curriculum.id = :curriculumId)
    AND (:yearLevel IS NULL OR s.courseAssignment.yearLevel = :yearLevel)
    AND (:semester IS NULL OR s.courseAssignment.semester = :semester)
    AND (:sectionId IS NULL OR s.section.id = :sectionId)
    AND (:status IS NULL OR s.status = :status)
    AND (:dayName IS NULL OR d.name = :dayName)
    """)
    List<ScheduledCourseEntity> search(
        @Param("academicYear") String academicYear,
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculumId,
        @Param("yearLevel") Integer yearLevel,
        @Param("semester") Integer semester,
        @Param("sectionId") Long sectionId,
        @Param("status") ScheduleStatus status,
        @Param("dayName") DayName dayName
    );

    @Query("""
    SELECT COUNT(DISTINCT s) FROM ScheduledCourseEntity s
    LEFT JOIN s.days d
    WHERE (:academicYear IS NULL OR s.academicYear = :academicYear)
    AND (:programId IS NULL OR s.courseAssignment.program.id = :programId)
    AND (:curriculumId IS NULL OR s.courseAssignment.curriculum.id = :curriculumId)
    AND (:yearLevel IS NULL OR s.courseAssignment.yearLevel = :yearLevel)
    AND (:semester IS NULL OR s.courseAssignment.semester = :semester)
    AND (:sectionId IS NULL OR s.section.id = :sectionId)
    AND (:status IS NULL OR s.status = :status)
    AND (:dayName IS NULL OR d.name = :dayName)
    """)
    long countSearch(
        @Param("academicYear") String academicYear,
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculumId,
        @Param("yearLevel") Integer yearLevel,
        @Param("semester") Integer semester,
        @Param("sectionId") Long sectionId,
        @Param("status") ScheduleStatus status,
        @Param("dayName") DayName dayName
    );


    @Query("""
    SELECT DISTINCT s.courseAssignment.course FROM ScheduledCourseEntity s 
    WHERE s.assignedFaculty.id = :facultyId 
    AND s.academicYear = :academicYear 
    AND s.courseAssignment.semester = :semester
    """)
    List<CourseEntity> getCoursesHandledOfFacultyByTerm(
        @Param("academicYear") String academicYear, 
        @Param("semester") int semester, 
        @Param("facultyId") Long facultyId
    );

    @Query("""
    SELECT DISTINCT s.section FROM ScheduledCourseEntity s 
    WHERE s.assignedFaculty.id = :facultyId 
    AND s.academicYear = :academicYear 
    AND s.courseAssignment.semester = :semester
    AND s.courseAssignment.course.id = :courseId
    """)
    List<SectionEntity> getSectionsFromCourses(
        @Param("academicYear") String academicYear, 
        @Param("semester") int semester, 
        @Param("facultyId") Long facultyId,
        @Param("courseId") Long courseId
    );


    @Query("""
    SELECT s from ScheduledCourseEntity s
    JOIN s.courseAssignment ca
    WHERE ca.program.id = :programId
    AND ca.curriculum.id = :curriculumId
    AND ca.yearLevel = :yearLevel
    AND ca.semester = :semester
    AND s.status = :status
    AND s.academicYear = :academicYear
    """)
    List<ScheduledCourseEntity> getSchedulesForProgramCurriculum(
        @Param("programId") Long programId,
        @Param("curriculumId") Long curriculumId,
        @Param("yearLevel") Integer yearLevel,
        @Param("semester") Integer semester,
        @Param("status") ScheduleStatus status,
        @Param("academicYear") String academicYear
    );   
}