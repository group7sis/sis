package com.group7.sis.entity.users;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="profiles")
public class ProfileEntity {

    public enum Sex{
        MALE,
        FEMALE
    }

    public enum CivilStatus{
        SINGLE,
        MARRIED,
        DIVORCED,
        WIDOWED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String middleName;

    @Column
    private String lastName;

    @Column
    private String suffix;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private CivilStatus civilStatus;

    @Column
    private String nationality;

    @Column
    private String mobileNumber;
}
