package com.group7.sis.mapper;

import org.mapstruct.*;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.dto.CourseDTO.CourseAssignmentDTO;
import com.group7.sis.entity.academic.CourseAssignmentEntity;
import com.group7.sis.entity.academic.CourseEntity;

@Mapper(componentModel = "spring")
public interface CourseMapper{

    @Mapping(target ="id", ignore = true)
    @Mapping(target = "prerequisites", ignore = true)
    @Mapping(target = "courseAssignments", ignore = true)
    CourseEntity toEntity(CourseDTO courseDTO);

    @Mapping(target ="course", ignore = true)
    @Mapping(target ="curriculum", ignore = true)
    CourseAssignmentEntity toEntity(CourseAssignmentDTO courseAssignmentDTO);

    @Mapping(target="prerequisiteIds", ignore = true)
    CourseDTO toDto(CourseEntity courseEntity);

    @Mapping(target = "curriculumId", source = "curriculum.id")
    @Mapping(target = "programId", source = "curriculum.program.id")
    CourseAssignmentDTO toDto(CourseAssignmentEntity courseAssignmentEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prerequisites", ignore = true)
    @Mapping(target = "courseAssignments", ignore = true)
    void updateEntityFromDTO(CourseDTO dto, @MappingTarget CourseEntity entity);

    @Mapping(target ="id", ignore = true)
    @Mapping(target ="course", ignore = true)
    @Mapping(target ="curriculum", ignore = true)
    void updateEntityFromDTO(CourseAssignmentDTO dto, @MappingTarget CourseAssignmentEntity entity);
}