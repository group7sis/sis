package com.group7.sis.dto.pagedResponse;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.dto.statsDtos.StudentsStatsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsPageDTO {
    List<StudentDTO> students = new ArrayList<>();
    StudentsStatsDTO stats;
}
