package com.group7.sis.dto;

import java.util.List;
import com.group7.sis.entity.academic.ProgramEntity.ProgramStatus;
import com.group7.sis.entity.academic.CurriculumEntity.CurriculumStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProgramDTO {
    private long id;
    private String title;
    private String code;
    private ProgramStatus status;

    List<CurriculumDTO> curriculums;

    @Getter
    @Setter
    public static class CurriculumDTO {
        private long id;
        private long programId;
        private String code;
        private String effectiveYear;
        private String durationYear;
        private CurriculumStatus status;

        private List<YearDTO> years;
    }

    @Getter
    @Setter
    public static class YearDTO {
        private long id;
        private long curriculumId;
        private int level;
        private int totalSemester;
    }
}
