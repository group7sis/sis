package com.group7.sis.facultyModule.dtos.paged;

import com.group7.sis.facultyModule.dtos.GradeConfigurationDTO;
import com.group7.sis.facultyModule.dtos.StudentGradeRecordDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncodeGradePageDTO {
    private StudentGradeRecordDTO studentGradeRecord;
    private GradeConfigurationDTO gradeConfigurationDTO;
}
