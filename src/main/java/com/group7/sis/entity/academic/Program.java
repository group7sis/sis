package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Program {
    public enum Status {
        ACTIVE,
        INACTIVE
    }

    private String title;
    private List<Curriculum> curriculum;
    private Status status;

    public Program(String title, List<Curriculum> curriculum, Status status) {
        this.title = title;
        this.curriculum = curriculum;
        this.status = status;
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

    public void setCurriculum(List<Curriculum> curriculum) {
        this.curriculum = curriculum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
