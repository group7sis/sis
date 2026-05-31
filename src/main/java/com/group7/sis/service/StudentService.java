package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.mapper.StudentMapper;
import com.group7.sis.repository.StudentRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentEntity addStudent(StudentDTO studentDTO){
        return studentRepository.save(studentMapper.toEntity(studentDTO));
    }

    public StudentDTO getStudent(Long id){
        return studentMapper.toDto(studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student not found,")));
    }

    public StudentDTO updateStudent(Long id, StudentDTO studentDTO){
        StudentEntity student = studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student does not exist."));

        studentMapper.updateEntityFromDTO(studentDTO, student);
        return studentMapper.toDto(studentRepository.save(student));
    }

    public List<StudentEntity> getAllStudents(){
        return studentRepository.findAll();
    }
}