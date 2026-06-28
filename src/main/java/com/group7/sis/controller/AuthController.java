package com.group7.sis.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.sis.dto.LoginRequestDTO;
import com.group7.sis.dto.LoginResponseDTO;
import com.group7.sis.dto.TermDTO;
import com.group7.sis.service.AuthService;
import com.group7.sis.service.TermService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TermService termService;

    @GetMapping("/term/current")
    public TermDTO getCurrentTerm(){
        return termService.getTerm(1L);
    }

    @PostMapping("/admin/login")
    public LoginResponseDTO adminLogin(@RequestBody LoginRequestDTO request){
        return authService.adminLogin(request);
    }

    @PostMapping("/portal/login")
    public LoginResponseDTO portalLogin(@RequestBody LoginRequestDTO request){
        return authService.portalLogin(request);
    }
}
