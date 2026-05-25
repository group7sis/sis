package com.group7.sis.entity.users;

import com.group7.sis.entity.users.student.AcademicInfo;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Student {
    private String student_number;
    private Account account;
    private Profile profile;
    private List<AcademicInfo> acad_info;
    private int year_admitted;
}
