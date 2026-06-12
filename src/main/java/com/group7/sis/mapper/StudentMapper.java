package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO.EnrolledCourseDTO;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.entity.users.student.EnrolledCourseEntity;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, AccountMapper.class})
public interface StudentMapper {
    
    StudentDTO toDto(StudentEntity studentEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "academicInfo", ignore = true)
    @Mapping(target = "studentNumber", ignore = true)
    StudentEntity toEntity(StudentDTO studentDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "academicInfo", ignore = true)
    @Mapping(target = "studentNumber", ignore = true)
    void updateEntityFromDTO(StudentDTO dto, @MappingTarget StudentEntity entity);


    @Mapping(target = "enrolledProgramId", source = "enrolledProgram.id")
    @Mapping(target = "programCurriculumId", source = "enrolledProgram.activeCurriculum.id")
    @Mapping(target = "sectionId", source = "section.id")
    AcademicInfoDTO toAcademicInfoDto(AcademicInfoEntity academicInfoEntity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrolledProgram", ignore = true)
    @Mapping(target = "programCurriculum", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "enrolledCourses", ignore = true)
    AcademicInfoEntity toAcademicInfoEntity(AcademicInfoDTO academicInfoDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrolledProgram", ignore = true)
    @Mapping(target = "programCurriculum", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "enrolledCourses", ignore = true)
    void updateAcademicInfoEntityFromDTO(AcademicInfoDTO dto, @MappingTarget AcademicInfoEntity entity);

    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "courseId", source = "schedule.courseAssignment.course.id")
    @Mapping(target = "programId", source = "schedule.section.program.id")
    EnrolledCourseDTO toEnrolledCourseDto(EnrolledCourseEntity enrolledCourseEntity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    EnrolledCourseEntity toEnrolledCourseEntity(EnrolledCourseDTO enrolledCourseDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    void updateEnrolledCourseEntityFromDTO(EnrolledCourseDTO dto, @MappingTarget EnrolledCourseEntity entity);
}