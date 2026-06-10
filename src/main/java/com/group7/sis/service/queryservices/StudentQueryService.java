package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.mapper.StudentMapper;
import com.group7.sis.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentQueryService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentEntity getStudentEntity(Long id){
        return studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public List<StudentDTO> findStudentsFromSectionWithStatus(Long sectionId, EnrollmentStatus enrollmentStatus){
        List<StudentDTO> students = new ArrayList<>();

        for (StudentEntity student : studentRepository.findStudentsByStatusNSection(enrollmentStatus, sectionId)){
            students.add(studentMapper.toDto(student));
        }
        return students;
    }
}