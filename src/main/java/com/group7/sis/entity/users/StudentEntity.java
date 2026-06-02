package com.group7.sis.entity.users;

//import com.group7.sis.entity.users.student.AcademicInfo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.group7.sis.entity.users.student.AcademicInfoEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="students")
public class StudentEntity {

    public enum StudentStatus{
        ACTIVE,
        INACTIVE,
        GRADUATED,
        DROPPED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String studentNumber;

    @Enumerated(EnumType.STRING)
    private StudentStatus studentStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;

    @Column
    private String yearAdmitted; 

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private List<AcademicInfoEntity> academicInfo = new ArrayList<>(); //enrollment history, current enrollment, etc.   
}
