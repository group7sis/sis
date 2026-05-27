package com.group7.sis.debug;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.*;
import com.group7.sis.entity.users.*;
import com.group7.sis.entity.users.student.*;
import java.util.List;
import java.util.ArrayList;

@Service
public class ListService {
    //STUDENTS
    private static List<Student> students = new ArrayList<>();

    public static void addStudent(Student student) {
        students.add(student);
    }

    public static List<Student> getStudents() {
        return students;
    }

    //PROGRAMS
    private static List<Program> programs = new ArrayList<>();

    public static void addProgram(Program program) {
        programs.add(program);
    }
    
    public static List<Program> getPrograms() {
        return programs;
    }

    //COURSES
    private static List<Course> courses = new ArrayList<>();

    public static void addCourse(Course course) {
        courses.add(course);
    }

    public static List<Course> getCourses() {
        return courses;
    }


    //FACULTY
    private static List<Faculty> faculty = new ArrayList<>();

    public static void addFaculty(Faculty facultyMember) {
        faculty.add(facultyMember);
    }

    public static List<Faculty> getFaculty() {
        return faculty;
    }
}
