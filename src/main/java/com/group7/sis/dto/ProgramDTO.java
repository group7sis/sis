package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.academic.ProgramEntity.ProgramStatus;
import com.group7.sis.entity.academic.CurriculumEntity.CurriculumStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProgramDTO {
    private String title;
    private String code;
    private ProgramStatus status;
    private Long activeCurriculumId;
    private List<CurriculumDTO> curriculums = new ArrayList<>();

    @Getter
    @Setter
    public static class CurriculumDTO{
        private String code;
        private String effectiveYear;
        private String durationYear;
        // private Long facultyHeadId;
        private CurriculumStatus status;
        private List<YearDTO> years = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class YearDTO {
        private int level;
        private int totalSemester;
    }


}
