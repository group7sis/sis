package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;
import java.util.Map;

@Entity
public class Year {
    private int level;
    private Map<Integer, List<Course>> sems_n_course;

    public Year(int level, Map<Integer, List<Course>> sems_n_course) {
        this.level = level;
        this.sems_n_course = sems_n_course;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map<Integer, List<Course>> getSems_n_course() {
        return sems_n_course;
    }

    // not sure about this yet. i dont like that it overwrites the whole map but for now i will just do setSems_n_course
    public void setSems_n_course(Map<Integer, List<Course>> sems_n_course) {
        this.sems_n_course = sems_n_course;
    }

    
}
