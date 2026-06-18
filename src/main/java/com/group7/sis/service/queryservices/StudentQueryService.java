package com.group7.sis.service.queryservices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentQueryService {
    private final StudentRepository studentRepository;

    public StudentEntity getStudentEntity(Long id){
        return studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public long getTotalStudentsFromSection(Long sectionId){
        return studentRepository.countDistinctByAcademicInfoSectionId(sectionId);
    }

    public long getTotalStudents(){
        return studentRepository.count();
    }

    public long getTotalStudentsOfFacultyByAcadmicYearAndSemesterAndByEnrollmentStatus(Long facultyId, String academicYear, int semester, EnrollmentStatus enrollmentStatus){
        return studentRepository.countDistinctByAcademicInfoEnrolledCoursesScheduleAssignedFacultyIdAndAcademicInfoAcademicYearAndAcademicInfoSemesterAndAcademicInfoEnrollmentStatus(facultyId, academicYear, semester, enrollmentStatus);
    }

    public List<StudentEntity> getStudentsHandedByFacultyByNameAndSectionAndCourseAndTerm(String name, StudentStatus studentStatus, Long sectionId, Long courseId, Long facultyId, String academicYear, int semester){   
        return studentRepository.getStudentsHandledByFaculty(name, studentStatus, sectionId, courseId, facultyId, academicYear, semester);
    }
}