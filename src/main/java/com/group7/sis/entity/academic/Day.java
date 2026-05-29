package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;

public class Day {
    private String name;
    private String start_time;
    private String end_time;
    private String break_time;

    public Day(String name, String start_time, String end_time, String break_time) {
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.break_time = break_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getBreak_time() {
        return break_time;
    }

    public void setBreak_time(String break_time) {
        this.break_time = break_time;
    }

    
}
