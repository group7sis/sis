package com.group7.sis.entity.users;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;

@Getter
@Entity
@Setter
@Table(name="faculty")
public class FacultyEntity {

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public enum EmploymentType{
        FULL_TIME,
        PART_TIME
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String facultyNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;

    @Column
    private String department;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;
    
    @Column
    private String position; //shound this be enum?

    @Column
    private LocalDate dateHired;

    //YOU DONT NEED EMBEDDABLE
    // @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<QualifiedCourseEntity> qualifiedCourses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "qualified_course",
        joinColumns = @JoinColumn(name ="faculty_id"),
        inverseJoinColumns = @JoinColumn(name="course_id")
    )
    private List<CourseEntity> qualifiedCourses = new ArrayList<>();

    @OneToMany(mappedBy = "assignedFaculty")
    private List<ScheduledCourseEntity> scheduledCourses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;
}
