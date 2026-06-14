package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.statsDtos.DashboardStatsDTO;
import com.group7.sis.service.queryservices.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final ProgramQueryService programQueryService;
    private final CurriculumQueryService curriculumQueryService;
    private final CourseQueryService courseQueryService;
    private final StudentQueryService studentQueryService;
    private final SectionQueryService sectionQueryService;
    private final FacultyQueryService facultyQueryService;
    private final ScheduleQueryService scheduleQueryService;
    private final DepartmentQueryService departmentQueryService;
    private final AdminQueryService adminQueryService;

    public DashboardStatsDTO getGlobalStats(){
        DashboardStatsDTO stats = new DashboardStatsDTO();

        stats.setTotalPrograms(programQueryService.getTotalPrograms());
        stats.setTotalCurriculums(curriculumQueryService.getTotalCurriculums());
        stats.setTotalCourses(courseQueryService.getTotalCourses());
        stats.setTotalSections(sectionQueryService.getTotalSections());
        stats.setTotalStudents(studentQueryService.getTotalStudents());
        stats.setTotalFaculty(facultyQueryService.getTotalFaculty());
        stats.setTotalSchedules(scheduleQueryService.getTotalSchedules());
        stats.setTotalDepartments(departmentQueryService.getTotalDepartments());
        stats.setTotalAdminAccounts(adminQueryService.getTotalAdmins());

        return stats;
    }
}
