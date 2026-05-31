package com.group7.sis.entity.users.faculty;

import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.users.FacultyEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "qualified_courses")
public class QualifiedCourseEntity {
    
    @EmbeddedId
    private QualifiedCourseId qualifiedCourseId = new QualifiedCourseId();

    @ManyToOne
    @MapsId("facultyId")
    @JoinColumn(name = "faculty_id")
    private FacultyEntity faculty;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private CourseEntity course;
}
