package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.SectionEntity;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.StudentEntity.ScholasticStatus;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.ProgramEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class AcademicInfoEntity {

    public enum EnrollmentStatus{
        INACTIVE,
        ACTIVE,
        COMPLETED,
        DROPPED
    }

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="program_id")
    private ProgramEntity enrolledProgram;
    
    @ManyToOne
    @JoinColumn(name="program_curriculum_id")
    private CurriculumEntity programCurriculum;

    @ManyToOne
    @JoinColumn(name="section_id")
    private SectionEntity section;

    @Column
    private String academicYear;

    @Column
    private int semester;
    
    @Column
    private int yearLevel;

    @Column
    private int units;
    
    @Column
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    private ScholasticStatus scholasticStatus;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "academicInfo")
    private List<EnrolledCourseEntity> enrolledCourses = new ArrayList<>();
}
