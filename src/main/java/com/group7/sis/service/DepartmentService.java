package com.group7.sis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.DepartmentDTO;
import com.group7.sis.entity.academic.DepartmentEntity;
import com.group7.sis.mapper.DepartmentMapper;
import com.group7.sis.repository.DepartmentRepository;
import com.group7.sis.service.queryservices.DepartmentQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DepartmentRepository departmentRepository;
    private final DepartmentQueryService departmentQueryService;

    public DepartmentEntity addDepartment(DepartmentDTO departmentDTO){
        DepartmentEntity departmentEntity = departmentMapper.toEntity(departmentDTO);
        return departmentRepository.save(departmentEntity);
    }

    public DepartmentEntity updateDepartment(Long id, DepartmentDTO departmentDTO){
        DepartmentEntity departmentEntity = departmentQueryService.getDepartmentEntity(id);

        departmentMapper.updateEntityFromDto(departmentDTO, departmentEntity);

        return departmentRepository.save(departmentEntity);
    }

    public DepartmentDTO getDepartment(Long id){
        return departmentMapper.toDto(departmentQueryService.getDepartmentEntity(id));
    }

    public List<DepartmentDTO> getAllDepartment(String name){
        List<DepartmentDTO> departments = new ArrayList<>();

        for(DepartmentEntity department : departmentRepository.search(name)){
            departments.add(departmentMapper.toDto(department));
        }
        return departments;
    }
}
