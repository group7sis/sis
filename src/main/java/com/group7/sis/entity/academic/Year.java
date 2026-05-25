package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;
import java.util.Map;

@Entity
public class Year {
    private int level;
    private Map<Integer, List<Course>> sems_n_course;
}
