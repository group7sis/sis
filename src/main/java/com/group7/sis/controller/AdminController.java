package com.group7.sis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.entity.*;
import com.group7.sis.entity.users.*;
import com.group7.sis.entity.academic.*;
import com.group7.sis.dto.*;
import com.group7.sis.service.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequiredArgsConstructor

@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/accounts")
    public List<AdminDTO> getAllAdmins() {
        return adminService.getAllAdmin();
    }

    @GetMapping("/accounts/{id}")
    public AdminDTO getAdmin(@PathVariable Long id) {
        return adminService.getAdmin(id);
    }  

    @PutMapping("/accounts/{id}")
    public AdminDTO updateAdmin(@PathVariable Long id, @RequestBody AdminDTO adminDto) {
        return adminService.updateAdmin(id, adminDto);
    }

    @PostMapping("/accounts/add")
    public AdminEntity addAdmin(@RequestBody AdminDTO adminDTO) {
        return adminService.addAdmin(adminDTO);
    }    
    

    //-----------------------------PROGRAMS-----------------------------------
    private final ProgramService programService;

    @GetMapping("/programs")
    public List<GetProgramDTO> getAllPrograms() {
        return programService.getAllPrograms();
    }

    @GetMapping("/programs/{id}")
    public ProgramDTO getProgram(@PathVariable Long id) {
        return programService.getProgram(id);
    }

    @PutMapping("/programs/{id}")
    public ProgramDTO updateProgram(@PathVariable Long id, @RequestBody ProgramDTO programDTO) {
        return programService.updateProgram(id, programDTO);
    }

    @PostMapping("/programs/add")
    public ProgramEntity createProgram(@RequestBody ProgramDTO addProgram) {
        return programService.addProgram(addProgram);
    }

    //-----------------------------COURSES-----------------------------------
    private final CourseService courseService;

    @GetMapping("/courses")
    public List<CourseEntity> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/courses/{id}")
    public CourseDTO getCourseByCode(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @PutMapping("/courses/{id}")
    public CourseDTO modifyCourseByCode(@PathVariable Long id, @RequestBody CourseDTO modifyCourse) {
        return courseService.updateCourse(id, modifyCourse);
    }

    @PostMapping("/courses/add")
    public CourseEntity createCourse(@RequestBody CourseDTO addCourse) {
        return courseService.addCourse(addCourse);
    }

    //TODO: Delete (tho kahit ihuli na. not really prioritized atm)
    
    //-----------------------------------STUDENTS-----------------------------------
    private final StudentService studentService;

    @GetMapping("/students")
    public List<StudentEntity> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/students/{id}")
    public StudentDTO getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @PutMapping("/students/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        return studentService.updateStudent(id, studentDTO);
    }

    @PostMapping("/students/add")
    public StudentEntity createStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.addStudent(studentDTO);
    }
    
    
    //-----------------------------------FACULTY-----------------------------------
    private final FacultyService facultyService;
    
    @GetMapping("/facultydto")
    public List<FacultyDTO> getAllFacultyByDTO() {
        return facultyService.getAllFacultyDTO();
    }

    @GetMapping("/faculty")
    public List<FacultyEntity> getAllFaculty() {
        return facultyService.getAllFaculty();
    }

    @GetMapping("/faculty/{id}")
    public FacultyDTO getFaculty(@PathVariable Long id) {
        return facultyService.getFaculty(id);
    }

    @PutMapping("/faculty/{id}")
    public FacultyDTO updateFaculty(@PathVariable Long id, @RequestBody FacultyDTO facultyDTO) {
        return facultyService.updateFaculty(id, facultyDTO);
    }
    
    @PostMapping("/faculty/add")
    public FacultyEntity createFaculty(@RequestBody FacultyDTO facultyDTO) {
        return facultyService.addFaculty(facultyDTO);
    }


    //-----------------------------------SCHEDULE-----------------------------------
    private final ScheduleService scheduleService;

    @GetMapping("/schedules")
    public List<ScheduledCourseDTO> getAllSchedules() {
        return scheduleService.getAllSchedule();
    }

    @GetMapping("/schedules/{id}")
    public ScheduledCourseDTO getSchedule(@PathVariable Long id) {
        return scheduleService.getSchedule(id);
    }

    @PutMapping("/schedules/{id}")
    public ScheduledCourseDTO updateSchedule(@PathVariable Long id, @RequestBody ScheduledCourseDTO scheduledCourseDTO) {
        return scheduleService.updateSchedule(id, scheduledCourseDTO);
    }
    @PostMapping("/schedules/add")
    public ScheduledCourseEntity createSchedule(@RequestBody ScheduledCourseDTO scheduledCourseDTO) {
        return scheduleService.addSchedule(scheduledCourseDTO);
    }


    //-----------------------------------SECTION-----------------------------------
    private final SectionService sectionService;

    @GetMapping("/sections")
    public List<SectionDTO> getAllSections() {
        return sectionService.getAllSections();
    }

    @GetMapping("/sections/{id}")
    public SectionDTO getSection(@PathVariable Long id) {
        return sectionService.getSection(id);
    }
    
    @PutMapping("/sections/{id}")
    public SectionDTO updateSection(@PathVariable Long id, @RequestBody SectionDTO sectionDTO) {        
        return sectionService.updateSection(id, sectionDTO);
    }

    @GetMapping("/sections/{id}/students")
    public List<StudentDTO> getSectionStudents(@PathVariable Long id) {
        return sectionService.getStudentsInSection(id);
    }

    @PostMapping("/sections/add")
    public SectionDTO createSection(@RequestBody SectionDTO sectionDTO) {
        return sectionService.addSection(sectionDTO);
    }
    
    
    

}
