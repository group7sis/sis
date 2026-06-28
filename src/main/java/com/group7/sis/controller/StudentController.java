package com.group7.sis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.dto.StudentDTO.AcademicInfoDTO;
import com.group7.sis.studentModule.dto.SchedulesSubmissionDTO;
import com.group7.sis.studentModule.dto.StudentDashboardDataDTO;
import com.group7.sis.studentModule.dto.StudentProfileDTO;
import com.group7.sis.studentModule.dto.paged.SchedulesCategorizedDTO;
import com.group7.sis.studentModule.dto.paged.StudentGradesPageDTO;
import com.group7.sis.studentModule.service.StudentModuleService;
import com.group7.sis.studentModule.dto.StudentScheduleDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/student")
public class StudentController {

    private final StudentModuleService studentModuleService;


    @GetMapping("/{id}/dashboard")
    public StudentDashboardDataDTO getDashboardData(@PathVariable Long id) {
        return studentModuleService.getDashboardData(id);
    }
    
    //-------------------------------------PROFILE---------------------------------------
    @GetMapping("/{id}/profile")
    public StudentProfileDTO getProfile(@PathVariable Long id) {
        return studentModuleService.getProfile(id);
    }

    @PutMapping("/{id}/profile")
    public StudentProfileDTO getProfile(@PathVariable Long id, @RequestBody StudentProfileDTO studentProfileDTO) {
        return studentModuleService.updateProfile(id, studentProfileDTO);
    }

    //-------------------------------------GRADES---------------------------------------
    @GetMapping("/{id}/grades")
    public StudentGradesPageDTO getGrades(@PathVariable Long id) {
        return studentModuleService.getGrades(id);
    }

    //------------------------------------SCHEDULE---------------------------------------
    @GetMapping("/{id}/schedule")
    public List<StudentScheduleDTO> getSchedule(@PathVariable Long id) {
        return studentModuleService.getSchedule(id);
    }

    //-----------------------------------ENROLLMENT---------------------------------------
    @GetMapping("/{id}/enrollment")
    public SchedulesCategorizedDTO getAvailableSchedules(@PathVariable Long id){
        return studentModuleService.getAvailableSchedules(id);
    }

    @PutMapping("/{id}/enrollment")
    public SchedulesCategorizedDTO putMethodName(@PathVariable Long id, @RequestBody SchedulesSubmissionDTO schedulesSubmissionDTO){
        return studentModuleService.enrollInTerm(id, schedulesSubmissionDTO);
    }

    @GetMapping("/{id}/enrollment-history")
    public List<AcademicInfoDTO> getEnrollmentHistory(@PathVariable Long id){
        return studentModuleService.getEnrollmentHistory(id);
    }
}
