package com.group7.sis.facultyModule.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyProfileDTO {

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    private String middleName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    private String mobileNumber;
    
    private String departmentName;
    private String position;
    private LocalDate dateHired;
}
