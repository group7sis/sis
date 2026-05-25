package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Curriculum {
    private String code;
    private String effective_year;
    private List<Year> years;
}
