package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.Section;
import com.group7.sis.entity.academic.Program;
import jakarta.persistence.Entity;
import java.util.List;
import java.time.LocalDate;

@Entity
public class AcademicInfo {
    private Program enrolled_program;
    private List<EnrolledCourse> enrolled_courses;
    private int year_level;
    private LocalDate enrollment_date;
    private Section section;
    private enum scholarship_status {
        REGULAR,
        IRREGULAR
    }
}

