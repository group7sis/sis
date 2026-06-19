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
import com.group7.sis.studentModule.dto.StudentProfileDTO;

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


    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "enrolledProgramId", source = "enrolledProgram.id")
    @Mapping(target = "programCurriculumId", source = "enrolledProgram.activeCurriculum.id")
    @Mapping(target = "sectionId", source = "section.id")
    AcademicInfoDTO toAcademicInfoDto(AcademicInfoEntity academicInfoEntity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "enrolledProgram", ignore = true)
    @Mapping(target = "programCurriculum", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "enrolledCourses", ignore = true)
    AcademicInfoEntity toAcademicInfoEntity(AcademicInfoDTO academicInfoDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "enrolledProgram", ignore = true)
    @Mapping(target = "programCurriculum", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "enrolledCourses", ignore = true)
    void updateAcademicInfoEntityFromDTO(AcademicInfoDTO dto, @MappingTarget AcademicInfoEntity entity);

    @Mapping(target = "academicInfoId", source = "academicInfo.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "courseId", source = "schedule.courseAssignment.course.id")
    @Mapping(target = "programId", source = "schedule.section.program.id")
    @Mapping(target = "courseCode", source = "schedule.courseAssignment.course.code")
    @Mapping(target = "courseTitle", source = "schedule.courseAssignment.course.title")
    @Mapping(target = "facultyName", ignore = true)
    @Mapping(target = "units", source = "schedule.courseAssignment.course.units")
    @Mapping(target = "sectionName", source = "schedule.section.name")
    EnrolledCourseDTO toEnrolledCourseDto(EnrolledCourseEntity enrolledCourseEntity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "academicInfo", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "status", ignore = true)
    EnrolledCourseEntity toEnrolledCourseEntity(EnrolledCourseDTO enrolledCourseDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "academicInfo", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    //TODO: ignore status at updates too?
    void updateEnrolledCourseEntityFromDTO(EnrolledCourseDTO dto, @MappingTarget EnrolledCourseEntity entity);

    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "middleName", source = "profile.middleName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "suffix", source = "profile.suffix")
    @Mapping(target = "mobileNumber", source = "profile.mobileNumber")
    @Mapping(target = "dateOfBirth", source = "profile.dateOfBirth")
    @Mapping(target = "enrolledProgram", ignore = true)
    @Mapping(target = "yearLevel", ignore = true)
    StudentProfileDTO toProfileDTO(StudentEntity studentEntity);
}