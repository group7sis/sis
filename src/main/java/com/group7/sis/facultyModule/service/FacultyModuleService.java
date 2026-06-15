package com.group7.sis.facultyModule.service;

import com.group7.sis.service.queryservices.StudentQueryService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.academic.TermEntity;
import com.group7.sis.entity.academic.DayEntity.DayName;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.ProfileEntity.Sex;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.facultyModule.dtos.paged.FacultyDashboardPageDTO;
import com.group7.sis.facultyModule.dtos.statsDtos.FacultyDashboardStatsDTO;
import com.group7.sis.mapper.ScheduledCourseMapper;
import com.group7.sis.service.queryservices.FacultyQueryService;
import com.group7.sis.service.queryservices.ScheduleQueryService;
import com.group7.sis.service.queryservices.TermQueryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacultyModuleService {
    private final StudentQueryService studentQueryService;
    private final FacultyQueryService facultyQueryService;
    private final ScheduleQueryService scheduleQueryService;
    private final TermQueryService termQueryService;

    private final ScheduledCourseMapper scheduledCourseMapper;

    public FacultyDashboardPageDTO getDashboardInfo(Long facultyId){
        TermEntity currentTerm = getTerm();
        FacultyDashboardPageDTO page = new FacultyDashboardPageDTO();
        List<ScheduledCourseDTO> schedules = new ArrayList<>();
        
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        DayName currentDayName = DayName.valueOf(currentDay.name());

        for (ScheduledCourseEntity schedule : scheduleQueryService.findTodaysSchedulesByFaculty(facultyId, currentDayName, currentTerm.getAcademicYear(), currentTerm.getSemester())){
            schedules.add(scheduledCourseMapper.toDto(schedule));
        }

        FacultyDashboardStatsDTO stats = getGlobalStats(facultyId);
        FacultyEntity faculty = facultyQueryService.getFacultyEntity(facultyId);

        String dayOfTheWeekFormatted = currentDay.name().charAt(0) + currentDay.name().substring(1).toLowerCase();

        String prefix = faculty.getProfile().getSex() == Sex.MALE ? "Mr. " : "Ms. ";
        String facultyName = prefix + faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName());

        page.setSchedules(schedules);
        page.setStats(stats);
        page.setCurrentMonth(LocalDate.now().getMonth().toString());
        page.setCurrentDay(String.valueOf(LocalDate.now().getDayOfMonth()));
        page.setCurrentYear(String.valueOf(LocalDate.now().getYear()));
        page.setCurrentDayOfTheWeek(dayOfTheWeekFormatted);
        page.setFacultyName(facultyName);

        return page;   
    }

    private FacultyDashboardStatsDTO getGlobalStats(Long id){
        TermEntity currentTerm = getTerm();
        FacultyEntity faculty = facultyQueryService.getFacultyEntity(id);
        FacultyDashboardStatsDTO stats = new FacultyDashboardStatsDTO();

        stats.setTotalSchedules(computeTotalSchedules(faculty, currentTerm.getAcademicYear(), currentTerm.getSemester()));
        stats.setTotalStudents(studentQueryService.getTotalStudentsOfFacultyByAcadmicYearAndSemesterAndByEnrollmentStatus(id, currentTerm.getAcademicYear(), currentTerm.getSemester(), EnrollmentStatus.ACTIVE));
        stats.setTotalSections(scheduleQueryService.getTotalSectionByAcademicYearAndSemesterAndFaculty(currentTerm.getAcademicYear(), currentTerm.getSemester(), id));
        stats.setTotalCoursesHandled(scheduleQueryService.getTotalCoursesHandledByAcademicYearAndSemesterAndFaculty(currentTerm.getAcademicYear(), currentTerm.getSemester(), id));
        
        return stats;
    }

    private long computeTotalSchedules(FacultyEntity faculty, String academicYear, int semester){
        long total = 0;
        total += scheduleQueryService.getTotalSchedulesByFacultyAndAcademicYearAndSemester(faculty.getId(), academicYear, semester);
        return total;
    }

    private TermEntity getTerm(){
        long id = 1;
        return termQueryService.getTermEntity(id);
    }
}
