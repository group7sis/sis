package com.group7.sis.entity.academic;

import com.group7.sis.entity.users.Faculty;
import java.util.List;
import jakarta.persistence.Entity;

@Entity
public class ScheduledCourse {
    private Program program;
    private Course course;
    private Section section; // Or String section idk yet
    private String academic_year;
    private List<Day> days;
    private Faculty assigned_faculty;
    private int year_level; // this is already in section so i could remove this
    private String notes;
    private enum status {
        ACTIVE, INACTIVE
    }
}
