package com.group7.sis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.dto.CreateAdmin;
import com.group7.sis.entity.*;
import com.group7.sis.entity.users.*;
import com.group7.sis.entity.users.student.AcademicInfo;
import com.group7.sis.debug.*;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController

@RequestMapping("/admin")
public class AdminController {
    @PostMapping("/register")
    public Admin createAdmin(@RequestBody CreateAdmin newAdmin) {
        Admin admin = new Admin(newAdmin.getUsername(), newAdmin.getPassword());       
        return admin;
    }

    @PostMapping("/addstudent")
    public Student createStudent(@RequestBody Student newStudent) {
        Student student = new Student(newStudent.getStudent_number(), newStudent.getAccount(), newStudent.getProfile(), newStudent.getAcad_info(), newStudent.getYear_admitted());
        StudentList.addStudent(student);
        return student;
    }

    @GetMapping("/getallstudents")
    public List<Student> getAllStudents() {
        return StudentList.getStudents();
    }

    @PostMapping("/addacademicinfo")
    public AcademicInfo addAcadInfo(@RequestParam String studentnum, @RequestBody AcademicInfo newAcadInfo) {
        AcademicInfo academicInfo = new AcademicInfo(newAcadInfo.getEnrolled_program(), newAcadInfo.getEnrolled_courses(), newAcadInfo.getYear_level(), newAcadInfo.getEnrollment_date(), newAcadInfo.getSection(), newAcadInfo.getScholastic_status(), newAcadInfo.getStudent_status());
            for (Student student : StudentList.getStudents()) {
                if (student.getStudent_number().equals(studentnum)) {
                    student.addAcad_info(academicInfo);
                    break;
                }
            }
        return academicInfo;
    }
    
}
