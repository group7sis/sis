package com.group7.sis.dto;

import java.time.LocalDate;

import com.group7.sis.entity.users.FacultyEntity.EmploymentType;
import com.group7.sis.entity.users.FacultyEntity.Status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyDTO {

    private Long id;
    
    private String facultyNumber;

    @Valid
    @NotNull(message = "An account is required")
    private AccountDTO account;

    @Valid
    @NotNull(message = "Profile details are required")
    private ProfileDTO profile;

    private Long departmentId;

    @NotNull(message = "Please specify employment type of faculty")
    private EmploymentType employmentType;

    @NotEmpty(message = "Faculty position is required")
    private String position;

    @NotNull(message = "Please specify date of hiring")
    @PastOrPresent(message = "Date of hiring cannot be set from the future")
    private LocalDate dateHired;

    private List<Long> qualifiedCoursesIds = new ArrayList<>();
    private List<Long> scheduledCoursesIds = new ArrayList<>();
    
    @NotNull(message = "Faculty member status must be defined")
    private Status status;
}
