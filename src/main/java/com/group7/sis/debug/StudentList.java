package com.group7.sis.debug;

import org.springframework.stereotype.Service;
import com.group7.sis.entity.users.Student;
import java.util.List;
import java.util.ArrayList;

@Service
public class StudentList {
    private static List<Student> students = new ArrayList<>();

    public static void addStudent(Student student) {
        students.add(student);
    }

    public static List<Student> getStudents() {
        return students;
    }
}
