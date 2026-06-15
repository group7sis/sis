package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.ScheduledCourseEntity;
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

    List<ScheduledCourseEntity> findByAssignedFacultyIdAndAcademicYearAndCourseAssignmentSemesterAndDaysName(Long facultyId, String academicYear, int semester, DayName dayName);

    @Query("""
    SELECT COUNT(DISTINCT s.section.id) FROM ScheduledCourseEntity s 
    WHERE s.assignedFaculty.id = :facultyId 
    AND s.academicYear = :academicYear 
    AND s.courseAssignment.semester = :semester
    """)
    long countSectionsHandledByFaculty(String academicYear, int semester, Long facultyId);

    long countByAssignedFacultyIdAndAcademicYearAndCourseAssignmentSemester(Long facultyId, String academicYear, int semester);

    long countByAssignedFacultyId(Long facultyId);

    @Query("""
    SELECT COUNT(DISTINCT s.courseAssignment.course.id) FROM ScheduledCourseEntity s 
    WHERE s.assignedFaculty.id = :facultyId 
    AND s.academicYear = :academicYear 
    AND s.courseAssignment.semester = :semester
    """)
    long countCoursesHandledByFaculty(String academicYear, int semester, Long facultyId);
    
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
}
