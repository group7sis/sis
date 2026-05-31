package com.group7.sis.entity.academic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="curriculums")
public class CurriculumEntity {
    public enum CurriculumStatus {
        ACTIVE,
        INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private ProgramEntity program;

    @Column
    private String code;

    @Column
    private String effectiveYear;

    @Column
    private String durationYear;

    //add faculty head later

    @Enumerated(EnumType.STRING)
    private CurriculumStatus status;

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YearEntity> years = new ArrayList<>();
}