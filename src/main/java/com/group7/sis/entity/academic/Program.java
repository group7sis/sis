package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Program {
    private String title;
    private List<Curriculum> curriculum;
}
