package com.group7.sis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.dto.CreateAdmin;
import com.group7.sis.entity.Admin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController

@RequestMapping("/admin")
public class AdminController {
    @PostMapping("/register")
    public Admin createAdmin(@RequestBody CreateAdmin newAdmin) {
        Admin admin = new Admin(newAdmin.getUsername(), newAdmin.getPassword());       
        return admin;
    }

    
    
}
