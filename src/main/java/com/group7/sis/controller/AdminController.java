package com.group7.sis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.dto.CreateAdmin;
import com.group7.sis.entity.*;
import com.group7.sis.entity.users.*;
import com.group7.sis.debug.*;

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
        Admin admin = new Admin(newAdmin.getUsername(), newAdmin.getPassword());       
        return admin;
    }
    @PostMapping("/login")
    public String adminLogin(@RequestBody Admin login) {
        //add a list of admins so i can add logic here to check if the username and password are correct, for now it just returns a success message
        return "Admin logged in successfully";
    }
    
    //-----------------------------------STUDENTS-----------------------------------
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        //add logic of what to show here in the list, for now it just shows all students
        return StudentList.getStudents();
    }

    @PostMapping("/students/add")
    public Student createStudent(@RequestBody Student newStudent) {
        Student student = new Student(newStudent.getStudent_number(), newStudent.getAccount(), newStudent.getProfile(), newStudent.getAcad_info(), newStudent.getYear_admitted());
        //add checks here for the student number, account, etc. before adding to the list
        StudentList.addStudent(student);
        return student;
    }

    //small issue where u can't edit the student number, but idk might be fine for now since the student number is unique and should not be changed
    @PostMapping("/students/{studentnum}/edit")
    public Student EditStudent(@PathVariable String studentnum, @RequestBody Student editedStudent) {
        //temporary iteration, will be changed once the database is implemented
        //add checks here too before editing the student information
        for (Student student : StudentList.getStudents()) {
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
}
