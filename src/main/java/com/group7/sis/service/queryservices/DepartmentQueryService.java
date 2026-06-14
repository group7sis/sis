package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.DepartmentEntity;
import com.group7.sis.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentQueryService {
    private final DepartmentRepository departmentRepository;

    public DepartmentEntity getDepartmentEntity(Long id){
        return departmentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Department label not found"));
    }

    public long getTotalDepartments(){
        return departmentRepository.count();
    }
}
