package com.group7.sis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.facultyModule.dtos.FacultyProfileDTO;
import com.group7.sis.facultyModule.dtos.paged.FacultyDashboardPageDTO;
import com.group7.sis.facultyModule.dtos.paged.SchedulesByDayPageDTO;
import com.group7.sis.facultyModule.service.FacultyModuleService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyModuleService facultyModuleService;

    @GetMapping("/{id}/dashboard")
    public FacultyDashboardPageDTO getDashboardInfo(@PathVariable Long id) {
        return facultyModuleService.getDashboardInfo(id);
    }

    @GetMapping("/{id}/profile")
    public FacultyProfileDTO getProfile(@PathVariable Long id) {
        return facultyModuleService.getProfile(id);
    }

    @PutMapping("/{id}/profile")
    public FacultyProfileDTO updateProfile(@PathVariable Long id, @RequestBody FacultyProfileDTO facultyProfileDTO) {
        return facultyModuleService.updateProfile(id, facultyProfileDTO);
    }

    @GetMapping("/{id}/schedules")
    public SchedulesByDayPageDTO getSchedules(@PathVariable Long id) {
        return facultyModuleService.getAllSchedulesByDay(id);
    }
    
    
}
