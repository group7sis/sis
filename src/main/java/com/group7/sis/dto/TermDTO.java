package com.group7.sis.dto;

import com.group7.sis.entity.academic.TermEntity.TermStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TermDTO {
    private String academicYear;
    private int semester;
    private TermStatus status;
}
