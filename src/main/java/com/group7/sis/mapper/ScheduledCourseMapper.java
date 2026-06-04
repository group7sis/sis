package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.dto.ScheduledCourseDTO.DayDTO;
import com.group7.sis.entity.academic.DayEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;

@Mapper(componentModel = "spring")
public interface ScheduledCourseMapper {

    @Mapping(target = "programId", source = "program.id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "assignedFacultyId", source = "assignedFaculty.id")
    ScheduledCourseDTO toDto(ScheduledCourseEntity scheduledCourseEntity);

    DayDTO toDto(DayEntity dayEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "assignedFaculty", ignore = true)
    ScheduledCourseEntity toEntity(ScheduledCourseDTO scheduledCourseDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scheduledCourse", ignore = true)
    DayEntity toEntity(DayDTO dayDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scheduledCourse", ignore = true)
    void updateEntityFromDto(DayDTO dayDTO, @MappingTarget DayEntity dayEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "assignedFaculty", ignore = true)
    @Mapping(target = "days", ignore = true)
    void updateEntityFromDto(ScheduledCourseDTO scheduledCourseDTO, @MappingTarget ScheduledCourseEntity scheduledCourseEntity);
}
