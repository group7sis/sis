package com.group7.sis.dto.pagedResponse;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.CourseDTO;
import com.group7.sis.dto.statsDtos.CourseStatsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoursePageDTO {
    List<CourseDTO> courses = new ArrayList<>();
    CourseStatsDTO stats;
}
