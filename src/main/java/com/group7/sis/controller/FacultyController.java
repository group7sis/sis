package com.group7.sis.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.dto.CourseDTO;
import com.group7.sis.dto.StudentDTO;
import com.group7.sis.dto.pagedResponse.SectionAndTotalStudentNumPageDTO;
import com.group7.sis.facultyModule.dtos.FacultyProfileDTO;
import com.group7.sis.facultyModule.dtos.GradeConfigurationDTO;
import com.group7.sis.facultyModule.dtos.StudentGradeRecordDTO;
import com.group7.sis.facultyModule.dtos.paged.EncodeGradePageDTO;
import com.group7.sis.facultyModule.dtos.paged.FacultyDashboardPageDTO;
import com.group7.sis.facultyModule.dtos.paged.SchedulesByDayPageDTO;
import com.group7.sis.facultyModule.dtos.statsDtos.FacultyDashboardStatsDTO;
import com.group7.sis.facultyModule.service.FacultyModuleService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
                      
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyModuleService facultyModuleService;

    @GetMapping("/{id}/dashboard")
    public FacultyDashboardPageDTO getDashboardInfo(@PathVariable Long id) {
        return facultyModuleService.getDashboardInfo(id);
    }


    //sidebar stats
    @GetMapping("/{id}/faculty-stats")
    public FacultyDashboardStatsDTO getFacultyStats(@PathVariable Long id) {
        return facultyModuleService.getGlobalStats(id);
    }


    
    //--------------------------------------------PROFILE--------------------------------------------------
    @GetMapping("/{id}/profile")
    public FacultyProfileDTO getProfile(@PathVariable Long id) {
        return facultyModuleService.getProfile(id);
    }

    @PutMapping("/{id}/profile")
    public FacultyProfileDTO updateProfile(@PathVariable Long id, @RequestBody FacultyProfileDTO facultyProfileDTO) {
        return facultyModuleService.updateProfile(id, facultyProfileDTO);
    }



    //------------------------------------------SCHEDULES--------------------------------------------------
    @GetMapping("/{id}/schedules")
    public SchedulesByDayPageDTO getSchedules(@PathVariable Long id) {
        return facultyModuleService.getAllSchedulesByDay(id);
    }

    //------------------------------------------CLASS LIST----------------------------------------------------
    //reuse getCoursesHandled and getSectionAndStat
    @GetMapping("/{id}/classes/courses/{courseId}/sections/{sectionId}/students")
    public List<StudentDTO> getStudentsHandled(@PathVariable Long id, @PathVariable Long courseId, @PathVariable Long sectionId, @RequestParam (required = false) String name) {
        return facultyModuleService.getStudentsHandledByNameAndFacultyAndSectionAndCourse(name, sectionId, courseId, id);
    }
    

    //----------------------------------------ENCODE GRADES--------------------------------------------------
    
    @GetMapping("/{id}/records/courses")
    public List<CourseDTO> getCoursesHandled(@PathVariable Long id) {
        return facultyModuleService.getCoursesHandled(id);
    }

    @GetMapping("/{id}/records/courses/{courseId}/sections")
    public List<SectionAndTotalStudentNumPageDTO> getSections(@PathVariable Long id, @PathVariable Long courseId) {
        return facultyModuleService.getSectionAndStat(id, courseId);
    }

    @GetMapping("/{id}/records/courses/{courseId}/sections/{sectionId}/students")
    public List<StudentGradeRecordDTO> getStudentRecordsFromSection(@PathVariable Long id, @PathVariable Long courseId, @PathVariable Long sectionId, @RequestParam (required = false) String name) {
        return facultyModuleService.getStudentRecordsFromSectionWithCourse(id, sectionId, courseId, name);
    }

    @GetMapping("/{id}/records/encode/{recordId}")
    public EncodeGradePageDTO getGradeRecord(@PathVariable Long id, @PathVariable Long recordId){
        return facultyModuleService.getEncodeInfo(recordId, id);
    }

    @PutMapping("/{id}/records/encode/{recordId}")
    public StudentGradeRecordDTO updateGradeRecord(@PathVariable Long id, @PathVariable Long recordId, @RequestBody StudentGradeRecordDTO studentGradeRecordDTO){
        return facultyModuleService.updateStudentRecord(id, recordId, studentGradeRecordDTO);
    }

    @GetMapping("/{id}/records/encode/{recordId}/compute")
    public StudentGradeRecordDTO computeGradeRecord(@PathVariable Long id, @PathVariable Long recordId){
        return facultyModuleService.computeGrade(recordId, id);
    }

    @PutMapping("/{id}/records/encode/{recordId}/finalize")
    public StudentGradeRecordDTO finalizeGradeRecord(@PathVariable Long id, @PathVariable Long recordId){
        return facultyModuleService.finalizeGrade(recordId, id);
    }



    //------------------------------------------GRADE CONFIG--------------------------------------------------
    @GetMapping("/{id}/grade-formula")
    public GradeConfigurationDTO getGradeConfig(@PathVariable Long id) {
        return facultyModuleService.getGradeConfig(id);
    }
    
    @PostMapping("/{id}/grade-formula")
    public GradeConfigurationDTO updateGradeConfig(@PathVariable Long id, @RequestBody GradeConfigurationDTO gradeConfigurationDTO) {
        return facultyModuleService.updateGradeConfig(id, gradeConfigurationDTO);
    }
        

}