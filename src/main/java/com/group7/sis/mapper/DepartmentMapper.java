package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.DepartmentDTO;
import com.group7.sis.entity.academic.DepartmentEntity;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    DepartmentEntity toEntity (DepartmentDTO departmentDTO);

    DepartmentDTO toDto (DepartmentEntity departmentEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(DepartmentDTO departmentDTO, @MappingTarget DepartmentEntity departmentEntity);
}
