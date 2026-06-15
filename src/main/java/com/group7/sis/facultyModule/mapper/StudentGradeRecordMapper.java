package com.group7.sis.facultyModule.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.facultyModule.dtos.StudentGradeRecordDTO;
import com.group7.sis.facultyModule.entity.StudentGradeRecordEntity;

@Mapper(componentModel = "spring")
public interface StudentGradeRecordMapper{

    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "courseName", source = "enrolledCourse.schedule.courseAssignment.course.title")
    StudentGradeRecordDTO toDto(StudentGradeRecordEntity studentGradeRecordEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrolledCourse", ignore = true)
    @Mapping(target = "calculatedGrade", ignore = true)
    @Mapping(target = "finalGrade", ignore = true)
    void updateEntityFromDto(StudentGradeRecordDTO studentGradeRecordDTO, @MappingTarget StudentGradeRecordEntity studentGradeRecordEntity);
}
