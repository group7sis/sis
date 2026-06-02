package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;

import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity.ScholasticStatus;
import com.group7.sis.entity.users.student.EnrolledCourseEntity.Status;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class StudentDTO {
    private String studentNumber;
    private StudentStatus studentStatus;

    @NotNull
    private AccountDTO account;

    @NotNull
    private ProfileDTO profile;

    private String yearAdmitted;
    private List<AcademicInfoDTO> academicInfo;

    @Getter
    @Setter
    public static class AcademicInfoDTO {
        private Long id;
        private Long enrolledProgramId;
        // private Long sectionId;
        private int yearLevel;
        private LocalDate enrollmentDate;
        private ScholasticStatus scholasticStatus;
        private List<EnrolledCourseDTO> enrolledCourses;

        @Getter
        @Setter
        public static class EnrolledCourseDTO {
            private Long id;
            private Long scheduleId;
            private double grade;
            private Status status;
        }
    }   
}
