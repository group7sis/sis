package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.entity.users.StudentEntity;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, AccountMapper.class})
public interface StudentMapper {
    StudentDTO toDto(StudentEntity studentEntity);

    @Mapping(target = "id", ignore = true)
    StudentEntity toEntity(StudentDTO studentDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(StudentDTO dto, @MappingTarget StudentEntity entity);
}
