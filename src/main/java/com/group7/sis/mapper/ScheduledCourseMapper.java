package com.group7.sis.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.dto.ScheduledCourseDTO.DayDTO;
import com.group7.sis.entity.academic.DayEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.studentModule.dto.StudentScheduleDTO;

@Mapper(componentModel = "spring")
public interface ScheduledCourseMapper {

    @Mapping(target = "courseAssignmentId", source = "courseAssignment.id")
    @Mapping(target = "courseTitle", source = "courseAssignment.course.title")
    @Mapping(target = "courseId", source = "courseAssignment.course.id")
    @Mapping(target = "programId", source = "courseAssignment.curriculum.program.id")
    @Mapping(target = "curriculumId", source = "courseAssignment.curriculum.id")
    @Mapping(target = "yearLevel", source = "courseAssignment.yearLevel")
    @Mapping(target = "semester", source = "courseAssignment.semester")
    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "assignedFacultyId", source = "assignedFaculty.id")
    @Mapping(target = "sectionName", source = "section.name")
    @Mapping(target = "facultyName", ignore = true)
    ScheduledCourseDTO toDto(ScheduledCourseEntity scheduledCourseEntity);

    @AfterMapping
    default void mapFacultyName(ScheduledCourseEntity entity, @MappingTarget ScheduledCourseDTO dto) {
        if (entity.getAssignedFaculty() == null || entity.getAssignedFaculty().getProfile() == null) {
            return;
        }
        var profile = entity.getAssignedFaculty().getProfile();
        String name = profile.getFirstName()
            + (profile.getMiddleName() == null ? "" : (" " + profile.getMiddleName()))
            + (" " + profile.getLastName())
            + (profile.getSuffix() == null ? "" : (" " + profile.getSuffix()));
        dto.setFacultyName(name.trim());
    }

    DayDTO toDto(DayEntity dayEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "courseAssignment", ignore = true)
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
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "courseAssignment", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "assignedFaculty", ignore = true)
    @Mapping(target = "days", ignore = true)
    void updateEntityFromDto(ScheduledCourseDTO scheduledCourseDTO, @MappingTarget ScheduledCourseEntity scheduledCourseEntity);

    @Mapping(target = "courseCode", source = "courseAssignment.course.code")
    @Mapping(target = "courseTitle", source = "courseAssignment.course.title")
    @Mapping(target = "facultyName", ignore = true)
    @Mapping(target = "sectionName", source = "section.name")
    @Mapping(target = "lectureHours", source = "courseAssignment.course.lectureHours")
    @Mapping(target = "labHours", source = "courseAssignment.course.labHours")
    @Mapping(target = "units", source = "courseAssignment.course.units")
    @Mapping(target = "days", ignore = true)
    StudentScheduleDTO toStudentScheduleDTO(ScheduledCourseEntity scheduledCourseEntity);

}
