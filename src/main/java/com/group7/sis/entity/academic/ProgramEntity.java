package com.group7.sis.entity.academic;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "programs")
public class ProgramEntity {
    public enum ProgramStatus {
        ACTIVE,
        INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String code;

    @Enumerated(EnumType.STRING)
    private ProgramStatus status;

    @OneToOne
    @JoinColumn(name = "active_curriculum_id")
    private CurriculumEntity activeCurriculum;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CurriculumEntity> curriculums = new ArrayList <>();
}
