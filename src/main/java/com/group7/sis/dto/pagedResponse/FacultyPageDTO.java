package com.group7.sis.dto.pagedResponse;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.FacultyDTO;
import com.group7.sis.dto.statsDtos.FacultyStatsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyPageDTO {
    List<FacultyDTO> faculties = new ArrayList<>();
    FacultyStatsDTO stats;
}
