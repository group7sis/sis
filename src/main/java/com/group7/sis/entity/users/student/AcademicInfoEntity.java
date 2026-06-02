package com.group7.sis.entity.users.student;

// import com.group7.sis.entity.academic.Section;
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
    public enum ScholasticStatus {
        REGULAR,
        IRREGULAR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name="program_id")
    private ProgramEntity enrolledProgram;

    // @OneToOne
    // @JoinColumn(name="section_id")
    // private Section section;

    @Column
    private int yearLevel;
    
    @Column
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    private ScholasticStatus scholasticStatus;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "acad_info_id")
    private List<EnrolledCourseEntity> enrolledCourses = new ArrayList<>();
}
