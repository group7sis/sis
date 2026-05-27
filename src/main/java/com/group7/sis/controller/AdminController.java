package com.group7.sis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.entity.*;
import com.group7.sis.entity.users.*;
import com.group7.sis.entity.academic.*;
import com.group7.sis.debug.*;
import com.group7.sis.dto.*;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController

@RequestMapping("/admin")
public class AdminController {
    @PostMapping("/register")
    public Admin createAdmin(@RequestBody CreateAdmin newAdmin) {
        //should be added in a list of admins
        Admin admin = new Admin(newAdmin.getUsername(), newAdmin.getPassword());       
        return admin;
    }
    @PostMapping("/login")
    public String adminLogin(@RequestBody Admin login) {
        //add a list of admins so i can add logic here to check if the username and password are correct, for now it just returns a success message
        return "Admin logged in successfully";
    }

    //-----------------------------PROGRAMS-----------------------------------
    @GetMapping("/programs")
    public List<Program> getAllPrograms() {
        //add logic of what to show here in the list, for now it passes all information of programs
        //could also use a DTO class to only show the appopriate information in the list
        return ListService.getPrograms();
    }

    @PostMapping("/programs/add")
    public Program createProgram(@RequestBody Program newProgram) {
        //add checks here for the program code, etc. before adding to the list
        ListService.addProgram(newProgram);
        return newProgram;
    }

    //-----------------------------COURSES-----------------------------------
    @GetMapping("/courses")
    public List<CourseInfo> getAllCourses() {
        //add logic
        return ListService.getCourses();
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestBody CourseInfo addCourse) {
        ListService.addCourse(addCourse);
        return ListService.addCourseToProgram(addCourse.getProgram(), addCourse.getCurriculum_code(), addCourse.getYear_level(), addCourse.getSemester(), addCourse.getCourse());
    }
    
    //-----------------------------------STUDENTS-----------------------------------
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        //add logic of what to show here in the list, for now it just shows all students
        return ListService.getStudents();
    }

    @PostMapping("/students/add")
    public Student createStudent(@RequestBody Student newStudent) {
        //add checks here for the student number, account, etc. before adding to the list
        //academicinfo.enrollment_date should be automatically generated, but for now it is being passed in the request body
        ListService.addStudent(newStudent);
        return newStudent;
    }

    @PostMapping("/students/{studentnum}/edit")
    public Student EditStudent(@PathVariable String studentnum, @RequestBody Student editedStudent) {
        //temporary iteration, will be changed once the database is implemented
        //add checks here too before editing the student information
        for (Student student : ListService.getStudents()) {
            if (student.getStudent_number().equals(studentnum)) {
                student.setAccount(editedStudent.getAccount());
                student.setProfile(editedStudent.getProfile());
                student.setAcad_info(editedStudent.getAcad_info());
                student.setYear_admitted(editedStudent.getYear_admitted());
                break;
            }
        }
        return editedStudent;
    }

    //-----------------------------------FACULTY-----------------------------------
    @GetMapping("/faculty")
    public List<Faculty> getAllFaculty() {
        //add logic of what to show here in the list, for now it just shows all faculty
        return ListService.getFaculty();
    }

    @PostMapping("/faculty/add")
    public Faculty createFaculty(@RequestBody Faculty newFaculty) {
        //add checks here before adding to the list
        //date_hired should be automatically generated, but for now it is being passed in the request body 
        ListService.addFaculty(newFaculty);
        return newFaculty;
    }

    @PostMapping("/faculty/edit")
    public Faculty editFaculty(@RequestBody Faculty editedFaculty) {
        //add checks here before editing the faculty information
        for (Faculty faculty : ListService.getFaculty()) {
            if (faculty.getFaculty_number().equals(editedFaculty.getFaculty_number())) {
                faculty.setAccount(editedFaculty.getAccount());
                faculty.setProfile(editedFaculty.getProfile());
                faculty.setDepartment(editedFaculty.getDepartment());
                faculty.setEmployment_type(editedFaculty.getEmployment_type());
                faculty.setPosition(editedFaculty.getPosition());
                faculty.setQualified_courses(editedFaculty.getQualified_courses());
                faculty.setStatus(editedFaculty.getStatus());
                break;
            }
        }
        return editedFaculty;
    }
    
}
