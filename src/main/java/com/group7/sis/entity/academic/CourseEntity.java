package com.group7.sis.entity.academic;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
//import com.group7.sis.entity.academic.Program;

@Getter
@Setter
@Entity
@Table(name = "courses")
public class CourseEntity {

    public enum Status {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String code;

    @Column
    private String title;
    
    @Column
    private int units;

    @Column
    private String lectureHours;

    @Column
    private String labHours;

    @Column
    private String notes;

    @Enumerated(EnumType.STRING)
    private Status status;
    
    @ManyToMany
    private List<CourseEntity> prerequisites = new ArrayList<>();
}
