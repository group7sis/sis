package com.group7.sis.dto.pagedResponse;

import com.group7.sis.dto.SectionDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionAndTotalStudentNumPageDTO {
    SectionDTO section;
    Long totalStudents;
}
