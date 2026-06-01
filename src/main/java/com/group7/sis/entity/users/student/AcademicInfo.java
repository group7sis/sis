package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.Section;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.academic.ProgramEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.time.LocalDate;

@Getter
@Setter
//@Entity
public class AcademicInfo {
    public enum ScholasticStatus {
        REGULAR,
        IRREGULAR
    }

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;
    
    @OneToOne
    @JoinColumn(name="program_id")
    private ProgramEntity enrolledProgram;

    @OneToOne
    @JoinColumn(name="section_id")
    private Section section;

    @Column
    private int year_level; // this is already in section so i could remove this
    
    @Column
    private LocalDate enrollment_date;

    @Enumerated(EnumType.STRING)
    private ScholasticStatus scholastic_status;
    
    private List<EnrolledCourse> enrolled_courses;
}
