package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.academic.SectionEntity.Status;

@Getter
@Setter
public class SectionDTO {
    private String code;
    private String name;
    private Long programId;
    private int yearLevel;
    private String academicYear;
    private int semester;
    private int maxCapacity;
    private String notes;
    private Status status;
}
