package com.group7.sis.dto;

import java.time.LocalDate;

import com.group7.sis.entity.users.FacultyEntity.EmploymentType;
import com.group7.sis.entity.users.FacultyEntity.Status;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyDTO {
    private String facultyNumber;

    @NotNull
    private AccountDTO account;

    @NotNull
    private ProfileDTO profile;

    private String department;
    private EmploymentType employmentType;
    private String position;
    private LocalDate dateHired;
    private List<Long> qualifiedCoursesIds = new ArrayList<>();
    private Status status;
}
