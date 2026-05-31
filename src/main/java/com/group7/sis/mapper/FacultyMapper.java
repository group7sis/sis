package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.GetFacultyDTO;

import com.group7.sis.dto.FacultyDTO;
import com.group7.sis.entity.users.FacultyEntity;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, AccountMapper.class})
public interface FacultyMapper {

    @Mapping(target = "qualifiedCourses", ignore = true)
    GetFacultyDTO toDto(FacultyEntity facultyEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "qualifiedCourses", ignore = true)
    FacultyEntity toEntity(FacultyDTO facultyDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "qualifiedCourses", ignore = true)
    void updateEntityFromDto(FacultyDTO facultyDTO, @MappingTarget FacultyEntity facultyEntity);
}
