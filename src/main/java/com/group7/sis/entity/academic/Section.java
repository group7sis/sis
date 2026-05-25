package com.group7.sis.entity.academic;

import com.group7.sis.entity.users.Student;
import jakarta.persistence.Entity;                    
import java.util.List;                                

@Entity
public class Section {
    private String code;
    private String name;
    private Program program;
    private int year_level;
    private String academic_year;
    private int semester;
    private int max_capacity;
    private String notes;
    private enum status {
        OPEN, CLOSED, WAITLIST
    }
    private List<Student> students;
    private List<ScheduledCourse> sched_courses;
}
