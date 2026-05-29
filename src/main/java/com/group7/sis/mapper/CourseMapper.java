package com.group7.sis.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.repository.CourseRepository;

@Mapper(componentModel = "spring")
public abstract class CourseMapper{
    @Autowired
    protected CourseRepository courseRepository;

    @Mapping(target ="id", ignore = true)
    @Mapping(target = "prerequisites", source = "prerequisiteIds")
    public abstract CourseEntity toEntity(CourseDTO courseDTO);

    protected CourseEntity map(Long id){
        return courseRepository.findById(id).orElseThrow();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prerequisites", source = "prerequisiteIds")
    public abstract void updateEntityFromDTO(CourseDTO dto, @MappingTarget CourseEntity entity);
}
