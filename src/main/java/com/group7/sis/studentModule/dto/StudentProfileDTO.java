package com.group7.sis.studentModule.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StudentProfileDTO {
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    private String middleName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    
    private String suffix;
    private String mobileNumber;

    private LocalDate dateOfBirth;

    private String enrolledProgram;
    private int yearLevel;
}
