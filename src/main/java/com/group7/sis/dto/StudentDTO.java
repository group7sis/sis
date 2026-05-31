package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;

import com.group7.sis.entity.users.StudentEntity.StudentStatus;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class StudentDTO {
    private String studentNumber;
    private StudentStatus studentStatus;

    @NotNull
    private AccountDTO account;

    @NotNull
    private ProfileDTO profile;

    private String yearAdmitted;
}
