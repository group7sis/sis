package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.TermDTO;
import com.group7.sis.entity.academic.TermEntity;

@Mapper(componentModel = "spring")
public interface TermMapper {

    TermDTO toDto(TermEntity termEntity);

    @Mapping(target = "id", ignore = true)
    TermEntity toEntity(TermDTO termDTO);

    @Mapping(target = "id", ignore = true)
    void upateEntityFromDTO(TermDTO termDTO, @MappingTarget TermEntity termEntity);
}
