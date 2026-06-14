package com.group7.sis.dto.pagedResponse;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.SectionDTO;
import com.group7.sis.dto.statsDtos.SectionStatsDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionPageDTO {
    List<SectionDTO> sections = new ArrayList<>();
    SectionStatsDto stats;
}
