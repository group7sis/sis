package com.group7.sis.facultyModule.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.facultyModule.dtos.GradeConfigurationDTO;
import com.group7.sis.facultyModule.entity.GradeConfigurationEntity;

@Mapper(componentModel = "spring")
public interface GradeConfigurationMapper {

    @Mapping(target = "facultyId", source = "faculty.id")
    GradeConfigurationDTO toDto(GradeConfigurationEntity gradeConfigurationEntity);

    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(GradeConfigurationDTO gradeConfigurationDTO, @MappingTarget GradeConfigurationEntity gradeConfigurationEntity);
}
