package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Program {
    private String title;
    private List<Curriculum> curriculum;

    public Program(String title, List<Curriculum> curriculum) {
        this.title = title;
        this.curriculum = curriculum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Curriculum> getCurriculum() {
        return curriculum;
    }

    // again, this could be addCurriculum instead of setCurriculum since a program can have multiple curriculum but for now i will just do setCurriculum
    public void setCurriculum(List<Curriculum> curriculum) {
        this.curriculum = curriculum;
    }

    
}
