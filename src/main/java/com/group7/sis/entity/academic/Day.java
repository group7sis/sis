package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;

@Entity
public class Day {
    private String name;
    private String start_time;
    private String end_time;
    private String break_time;
}
