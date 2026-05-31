package com.group7.sis.entity.users.faculty;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class QualifiedCourseId implements Serializable {
    private Long facultyId;
    private Long courseId;
}
