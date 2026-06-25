package com.group7.sis.service.queryservices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.academic.SectionEntity;
import com.group7.sis.entity.academic.DayEntity.DayName;
import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;
import com.group7.sis.repository.ScheduledCourseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleQueryService {
    private final ScheduledCourseRepository scheduledCourseRepository;

    public ScheduledCourseEntity getScheduledCourseEntity(Long id){
        return scheduledCourseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    public List<ScheduledCourseEntity> findTodaysSchedulesByFaculty(Long facultyId, DayName dayName, String academicYear, int semester, ScheduleStatus status){
        return scheduledCourseRepository.findByAssignedFacultyIdAndAcademicYearAndCourseAssignmentSemesterAndDaysNameAndStatus(facultyId, academicYear, semester, dayName, status);
    }

    public long getTotalSchedules(){
        return scheduledCourseRepository.count();
    }

    public long getTotalSchedulesByFaculty(Long facultyId){
        return scheduledCourseRepository.countByAssignedFacultyId(facultyId);
    }

    public long getTotalSchedulesByFacultyAndAcademicYearAndSemester(Long facultyId, String academicYear, int semester, ScheduleStatus status){
        return scheduledCourseRepository.countByAssignedFacultyIdAndAcademicYearAndCourseAssignmentSemesterAndStatus(facultyId, academicYear, semester, status);
    }

    public long getTotalSectionByAcademicYearAndSemesterAndFaculty(String academicYear, int semester, Long facultyId, ScheduleStatus status){
        return scheduledCourseRepository.countSectionsHandledByFaculty(academicYear, semester, facultyId, status);
    }

    public long getTotalCoursesHandledByAcademicYearAndSemesterAndFaculty(String academicYear, int semester, Long facultyId, ScheduleStatus status){
        return scheduledCourseRepository.countCoursesHandledByFaculty(academicYear, semester, facultyId, status);
    }

    public List<CourseEntity> getAllCoursesHandledByFacultyAndAcademicYearAndSemester(Long facultyId, String academicYear, int semester){
        return scheduledCourseRepository.getCoursesHandledOfFacultyByTerm(academicYear, semester, facultyId);
    }

    public List<SectionEntity> getAllSectionWithCourseByFacultyAndAcadYearAndSection(Long courseId, Long facultyId, String academicYear, int semester){
        return scheduledCourseRepository.getSectionsFromCourses(academicYear, semester, facultyId, courseId);
    }
}