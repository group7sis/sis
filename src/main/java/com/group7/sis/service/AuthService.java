package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.LoginRequestDTO;
import com.group7.sis.dto.LoginResponseDTO;
import com.group7.sis.entity.AdminEntity;
import com.group7.sis.entity.users.AccountEntity.Role;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.repository.AdminRepository;
import com.group7.sis.repository.FacultyRepository;
import com.group7.sis.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public LoginResponseDTO adminLogin(LoginRequestDTO request){
        LoginResponseDTO response = new LoginResponseDTO();

        AdminEntity admin = adminRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword()).orElse(null);

        if (admin == null){
            response.setSuccess(false);
            response.setMessage("Invalid admin username or password");
            return response;
        }

        response.setSuccess(true);
        response.setId(admin.getId());
        response.setRole("ADMIN");
        response.setName(admin.getUsername());
        response.setMessage("Login successful");
        return response;
    }

    public LoginResponseDTO portalLogin(LoginRequestDTO request){
        LoginResponseDTO response = new LoginResponseDTO();

        if (request.getRole() == null){
            response.setSuccess(false);
            response.setMessage("Please select Student or Faculty");
            return response;
        }

        if ("STUDENT".equalsIgnoreCase(request.getRole())){
            StudentEntity student = studentRepository.findByAccountUsernameAndAccountPassword(request.getUsername(), request.getPassword()).orElse(null);

            if (student == null){
                response.setSuccess(false);
                response.setMessage("Invalid student credentials");
                return response;
            }

            response.setSuccess(true);
            response.setId(student.getId());
            response.setRole(Role.STUDENT.name());
            response.setName(formatName(student.getProfile().getFirstName(), student.getProfile().getMiddleName(), student.getProfile().getLastName(), student.getProfile().getSuffix()));
            response.setMessage("Login successful");
            return response;
        }

        if ("FACULTY".equalsIgnoreCase(request.getRole())){
            FacultyEntity faculty = facultyRepository.findByAccountUsernameAndAccountPassword(request.getUsername(), request.getPassword()).orElse(null);

            if (faculty == null){
                response.setSuccess(false);
                response.setMessage("Invalid faculty credentials");
                return response;
            }

            response.setSuccess(true);
            response.setId(faculty.getId());
            response.setRole(Role.FACULTY.name());
            response.setName(formatName(faculty.getProfile().getFirstName(), faculty.getProfile().getMiddleName(), faculty.getProfile().getLastName(), faculty.getProfile().getSuffix()));
            response.setMessage("Login successful");
            return response;
        }

        response.setSuccess(false);
        response.setMessage("Invalid role");
        return response;
    }

    private String formatName(String firstName, String middleName, String lastName, String suffix){
        return firstName
            + (middleName == null ? "" : (" " + middleName))
            + (" " + lastName)
            + (suffix == null ? "" : (" " + suffix));
    }
}
