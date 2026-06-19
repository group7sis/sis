package com.group7.sis.studentModule.dto.paged;

import java.util.List;

import com.group7.sis.dto.StudentDTO.AcademicInfoDTO.EnrolledCourseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentGradesPageDTO {
    private Double gwa;
    private List<EnrolledCourseDTO> enrolledCourses;
}
