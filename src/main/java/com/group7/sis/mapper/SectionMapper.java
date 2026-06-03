package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.SectionDTO;
import com.group7.sis.entity.academic.SectionEntity;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    @Mapping(target = "programId", source = "program.id")
    SectionDTO toDto (SectionEntity sectionEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "studentAcademicInfos", ignore = true)
    SectionEntity toEntity (SectionDTO sectionDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "studentAcademicInfos", ignore = true)
    void updateEntityFromDto(SectionDTO sectionDTO, @MappingTarget SectionEntity sectionEntity);
}
