package com.group7.sis.entity.academic;

import com.group7.sis.entity.users.student.AcademicInfoEntity;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.util.List;                                

@Getter
@Setter
@Entity
public class SectionEntity {
    public enum Status {
        OPEN, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name="program_id")
    private ProgramEntity program;

    @Column
    private int yearLevel;

    @Column
    private String academicYear;

    @Column
    private int semester;

    @Column
    private int maxCapacity;

    @Column
    private String notes;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "section")
    private List<AcademicInfoEntity> studentAcademicInfos;
}