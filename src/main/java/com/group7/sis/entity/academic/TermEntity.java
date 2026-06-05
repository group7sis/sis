package com.group7.sis.entity.academic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class TermEntity {
    public enum TermStatus {
        ENROLLMENT,
        ONGOING,
        CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    private String acadamicYear;

    @Column
    private int semester;

    @Enumerated(EnumType.STRING)
    private TermStatus status;
}
