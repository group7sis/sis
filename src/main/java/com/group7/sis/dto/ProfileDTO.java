package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.users.ProfileEntity.Sex;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

import com.group7.sis.entity.users.ProfileEntity.CivilStatus;


@Getter
@Setter
public class ProfileDTO {

    @NotEmpty(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    private String suffix;
    
    @NotNull(message = "Birth sex is required")
    private Sex sex;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth cannot be today nor the future")
    private LocalDate dateOfBirth;

    @NotNull(message = "Civil status must be defined")
    private CivilStatus civilStatus;

    @NotEmpty(message = "Nationality required")
    private String nationality; 

    @NotEmpty(message = "Mobile number is required")
    private String mobileNumber;
}
