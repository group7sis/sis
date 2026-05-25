package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Course {
    private String code;
    private String title;
    private List<Course> prequisites;
    private int units;
    private String lecture_hours;
    private String lab_hours;
    private String notes;
    private enum status {
        ACTIVE, INACTIVE
    }
}
