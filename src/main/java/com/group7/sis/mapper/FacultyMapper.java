package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.FacultyDTO;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.facultyModule.dtos.FacultyProfileDTO;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, AccountMapper.class})
public interface FacultyMapper {

    @Mapping(target = "qualifiedCoursesIds", ignore = true)
    @Mapping(target = "scheduledCoursesIds", ignore = true)
    @Mapping(target = "departmentId", ignore = true)
    FacultyDTO toDto(FacultyEntity facultyEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultyNumber", ignore = true)
    @Mapping(target = "qualifiedCourses", ignore = true)
    @Mapping(target = "scheduledCourses", ignore = true)
    @Mapping(target = "department", ignore = true)
    FacultyEntity toEntity(FacultyDTO facultyDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultyNumber", ignore = true)
    @Mapping(target = "qualifiedCourses", ignore = true)
    @Mapping(target = "scheduledCourses", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntityFromDto(FacultyDTO facultyDTO, @MappingTarget FacultyEntity facultyEntity);

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "middleName", source = "profile.middleName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "mobileNumber", source = "profile.mobileNumber")
    FacultyProfileDTO toProfileDTO(FacultyEntity facultyEntity);
}
