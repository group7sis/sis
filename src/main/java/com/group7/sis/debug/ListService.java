package com.group7.sis.debug;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.*;
import com.group7.sis.entity.users.*;
import com.group7.sis.entity.users.student.*;
import com.group7.sis.dto.*;
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

    public static String addCourseToProgram(String program, String curriculum_code, int year_level, int semester, Course course) {
        for (Program p : programs) {
            if (p.getTitle().equals(program)) {
                for (Curriculum c : p.getCurriculum()) {
                    if (c.getCode().equals(curriculum_code)) {
                        for (Year y : c.getYears()) {
                            if (y.getLevel() == year_level) {
                                if (semester > y.sems_n_course.size()) {
                                    break;
                                }
                                y.sems_n_course.get(semester).add(course);
                                return "Course added successfully";
                            }
                        }
                    }
                }
            }
        }
        return "Failed to add course";
    }
    
    public static List<Program> getPrograms() {
        return programs;
    }

    //COURSES
    private static List<CourseInfo> courses = new ArrayList<>();

    public static void addCourse(CourseInfo courseInfo) {
        courses.add(courseInfo);
    }

    public static List<CourseInfo> getCourses() {
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
