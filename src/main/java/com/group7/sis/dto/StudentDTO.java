package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;

import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity.ScholasticStatus;
import com.group7.sis.entity.users.student.EnrolledCourseEntity.Status;
import com.group7.sis.validation.ValidateYear;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class StudentDTO {
    private Long id;
    private String studentNumber;

    @NotNull(message = "Student status must be defined")
    private StudentStatus studentStatus;

    @Valid
    @NotNull(message = "An account is required")
    private AccountDTO account;

    @Valid
    @NotNull(message = "Profile details are required")
    private ProfileDTO profile;

    @NotBlank(message = "Please specify year admission")
    @Pattern(
        regexp = "^\\d{4}-\\d{4}$",
        message = "Format must be YYYY-YYYY"
    )
    @ValidateYear
    private String yearAdmitted;

    @Valid
    private List<AcademicInfoDTO> academicInfo;

    @Getter
    @Setter
    public static class AcademicInfoDTO {
        private Long id;

        @NotNull(message = "Program must be assigned")
        private Long enrolledProgramId;

        @NotNull(message = "Curriculum must be assigned")
        private Long programCurriculumId;

        @NotNull(message = "Section must be assigned")
        private Long sectionId;

        @NotBlank(message = "Academic year is required")
        @Pattern(
            regexp = "^\\d{4}-\\d{4}$",
            message = "Format must be YYYY-YYYY"
        )
        @ValidateYear
        private String academicYear;

        @NotNull(message = "Semester is required")
        @Positive(message = "Semester must be positive")
        private int semester;


        @NotNull(message = "Please specify year level")
        @Positive(message = "Year level must be positive")
        private int yearLevel;

        @PastOrPresent(message = "Enrollment date can't be from the future")
        @NotNull(message = "Enrollment date is required")
        private LocalDate enrollmentDate;

        @NotNull(message = "Scholastic status must be defined")
        private ScholasticStatus scholasticStatus;

        @NotNull(message = "Enrollment status must be defined")
        private EnrollmentStatus enrollmentStatus;

        @Valid
        @NotEmpty(message = "Must be enrolled in at least one course")
        private List<EnrolledCourseDTO> enrolledCourses;

        @Getter
        @Setter
        public static class EnrolledCourseDTO {
            private Long id;

            @NotNull(message = "Must assign a schedule")
            private Long scheduleId;

            private Long programId;

            private Long courseId;

            private double grade;

            @NotNull(message = "Course enrollment status must be defined")
            private Status status;
        }
    }   
}
