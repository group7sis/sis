package com.group7.sis.mapper;

import org.mapstruct.*;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.entity.academic.CourseEntity;

@Mapper(componentModel = "spring")
public interface CourseMapper{

    @Mapping(target ="id", ignore = true)
    @Mapping(target = "prerequisites", ignore = true)
    CourseEntity toEntity(CourseDTO courseDTO);

    @Mapping(target="prerequisiteIds", ignore = true)
    CourseDTO toDto(CourseEntity courseEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prerequisites", ignore = true)
    void updateEntityFromDTO(CourseDTO dto, @MappingTarget CourseEntity entity);
}