package com.group7.sis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.entity.*;
import com.group7.sis.entity.users.*;
import com.group7.sis.entity.users.FacultyEntity.EmploymentType;
import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity.ScholasticStatus;
import com.group7.sis.entity.academic.*;
import com.group7.sis.entity.academic.CourseEntity.Status;
import com.group7.sis.entity.academic.ProgramEntity.ProgramStatus;
import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;
import com.group7.sis.dto.*;
import com.group7.sis.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor

@RequestMapping("/admin")
public class AdminController {
    // TODO: Delete API for all entities (tho kahit ihuli na. not really prioritized
    // atm)

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

    // -----------------------------TERM-----------------------------------
    // Not sure if ill need ALL of term crud but it is what it is atm
    private final TermService termService;

    @GetMapping("/terms")
    public List<TermEntity> getAllTerms() {
        return termService.getAllTerms();
    }

    @GetMapping("/terms/{id}")
    public TermDTO getTerm(@PathVariable Long id) {
        return termService.getTerm(id);
    }

    @PutMapping("/terms/{id}")
    public TermEntity updateTerm(@PathVariable Long id, @RequestBody TermDTO termDTO) {
        return termService.updateTerm(id, termDTO);
    }

    @PostMapping("/terms/add")
    public TermEntity createTerm(@RequestBody TermDTO termDTO) {
        return termService.addTerm(termDTO);
    }

    // -----------------------------PROGRAMS-----------------------------------
    private final ProgramService programService;

    @GetMapping("/programs")
    public List<GetProgramDTO> getAllPrograms(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ProgramStatus status) {
        return programService.getAllPrograms(title, status);
    }

    @GetMapping("/programs/{id}")
    public ProgramDTO getProgram(@PathVariable Long id) {
        return programService.getProgram(id);
    }

    @PutMapping("/programs/{id}")
    public ProgramDTO updateProgram(@PathVariable Long id, @Valid @RequestBody ProgramDTO programDTO) {
        return programService.updateProgram(id, programDTO);
    }

    @PostMapping("/programs/add")
    public ProgramEntity createProgram(@Valid @RequestBody ProgramDTO addProgram) {
        return programService.addProgram(addProgram);
    }

    // -----------------------------COURSES-----------------------------------
    private final CourseService courseService;

    @GetMapping("/courses")
    public List<CourseDTO> getAllCourses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long department,
            @RequestParam(required = false) Long program,
            @RequestParam(required = false) Long curriculum,
            @RequestParam(required = false) Integer yearLevel,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Status status) {
        return courseService.getAllCourses(title, department, program, curriculum, yearLevel, semester, status);
    }

    @GetMapping("/courses/{id}")
    public CourseDTO getCourseByCode(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @PutMapping("/courses/{id}")
    public CourseDTO modifyCourseByCode(@PathVariable Long id, @Valid @RequestBody CourseDTO modifyCourse) {
        return courseService.updateCourse(id, modifyCourse);
    }

    @PostMapping("/courses/add")
    public CourseDTO createCourse(@Valid @RequestBody CourseDTO addCourse) {
        return courseService.addCourse(addCourse);
    }

    // -----------------------------------STUDENTS-----------------------------------
    private final StudentService studentService;

    @GetMapping("/students")
    public List<StudentDTO> getAllStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) StudentStatus studentStatus,
            @RequestParam(required = false) EnrollmentStatus enrollmentStatus,
            @RequestParam(required = false) ScholasticStatus scholasticStatus,
            @RequestParam(required = false) Long program,
            @RequestParam(required = false) Long curriculum,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Integer yearLevel,
            @RequestParam(required = false) Long section) {
        return studentService.getAllStudents(name, studentStatus, enrollmentStatus, scholasticStatus, program,
                curriculum, academicYear, yearLevel, section);
    }

    @GetMapping("/students/{id}")
    public StudentDTO getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @PutMapping("/students/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        return studentService.updateStudent(id, studentDTO);
    }

    @PostMapping("/students/add")
    public StudentEntity createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        return studentService.addStudent(studentDTO);
    }

    // -----------------------------------FACULTY-----------------------------------
    private final FacultyService facultyService;

    @GetMapping("/faculty")
    public List<FacultyDTO> getAllFacultyByDTO(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) FacultyEntity.Status status,
            @RequestParam(required = false) Long department,
            @RequestParam(required = false) EmploymentType employmentType) {
        return facultyService.getAllFacultyDTO(name, status, department, employmentType);
    }

    // debugging
    @GetMapping("/facultyentity")
    public List<FacultyEntity> getAllFaculty() {
        return facultyService.getAllFaculty();
    }

    @GetMapping("/faculty/{id}")
    public FacultyDTO getFaculty(@PathVariable Long id) {
        return facultyService.getFaculty(id);
    }

    @PutMapping("/faculty/{id}")
    public FacultyDTO updateFaculty(@PathVariable Long id, @Valid @RequestBody FacultyDTO facultyDTO) {
        return facultyService.updateFaculty(id, facultyDTO);
    }

    @PostMapping("/faculty/add")
    public FacultyEntity createFaculty(@Valid @RequestBody FacultyDTO facultyDTO) {
        return facultyService.addFaculty(facultyDTO);
    }

    // -----------------------------------SCHEDULE-----------------------------------
    private final ScheduleService scheduleService;

    @GetMapping("/schedules")
    public List<ScheduledCourseDTO> getAllSchedules(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Long program,
            @RequestParam(required = false) Long curriculum,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) ScheduleStatus status) {
        return scheduleService.getAllSchedule(academicYear, program, curriculum, semester, sectionId, status);
    }

    @GetMapping("/schedules/{id}")
    public ScheduledCourseDTO getSchedule(@PathVariable Long id) {
        return scheduleService.getSchedule(id);
    }

    @PutMapping("/schedules/{id}")
    public ScheduledCourseDTO updateSchedule(@PathVariable Long id,
            @Valid @RequestBody ScheduledCourseDTO scheduledCourseDTO) {
        return scheduleService.updateSchedule(id, scheduledCourseDTO);
    }

    @PostMapping("/schedules/add")
    public ScheduledCourseEntity createSchedule(@Valid @RequestBody ScheduledCourseDTO scheduledCourseDTO) {
        return scheduleService.addSchedule(scheduledCourseDTO);
    }

    // -----------------------------------SECTION-----------------------------------
    private final SectionService sectionService;

    @GetMapping("/sections")
    public List<SectionDTO> getAllSections(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long program,
            @RequestParam(required = false) Integer yearLevel,
            @RequestParam(required = false) SectionEntity.Status status) {
        return sectionService.getAllSections(name, program, yearLevel, status);
    }

    @GetMapping("/sections/{id}")
    public SectionDTO getSection(@PathVariable Long id) {
        return sectionService.getSection(id);
    }

    @PutMapping("/sections/{id}")
    public SectionDTO updateSection(@PathVariable Long id, @Valid @RequestBody SectionDTO sectionDTO) {
        return sectionService.updateSection(id, sectionDTO);
    }

    @PostMapping("/sections/{id}/students")
    public List<StudentDTO> getSectionStudents(@PathVariable Long id,
            @Valid @RequestBody SearchForSectionDTO searchForSectionDTO) {
        return sectionService.getStudentsInSection(id, searchForSectionDTO);
    }

    @PostMapping("/sections/add")
    public SectionDTO createSection(@Valid @RequestBody SectionDTO sectionDTO) {
        return sectionService.addSection(sectionDTO);
    }

    // -----------------------------------SETTINGS-----------------------------------

    // DEPARTMENTS
    private final DepartmentService departmentService;

    @GetMapping("/departments")
    public List<DepartmentDTO> getAllDepartments(@RequestParam(required = false) String name) {
        return departmentService.getAllDepartment(name);
    }

    @GetMapping("/departments/{id}")
    public DepartmentDTO getDepartment(@PathVariable Long id) {
        return departmentService.getDepartment(id);
    }

    @PutMapping("/departments/{id}")
    public DepartmentEntity updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDTO departmentDTO) {
        return departmentService.updateDepartment(id, departmentDTO);
    }

    @PostMapping("/departments/add")
    public DepartmentEntity createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        return departmentService.addDepartment(departmentDTO);
    }

}