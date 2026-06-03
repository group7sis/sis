package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.repository.StudentRepository;
import com.group7.sis.mapper.StudentMapper; 

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

    public StudentDTO getStudentByAcademicInfoId(Long academicInfoId){
        return studentMapper.toDto(studentRepository.findByAcademicInfoId(academicInfoId)
        .orElseThrow(() -> new RuntimeException("Student not found.")));
    }


}
