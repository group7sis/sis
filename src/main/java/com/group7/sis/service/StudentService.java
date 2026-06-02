package com.group7.sis.service;

import com.group7.sis.repository.ProgramRepository;
import org.springframework.stereotype.Service;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO.EnrolledCourseDTO;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.entity.users.student.EnrolledCourseEntity;
import com.group7.sis.mapper.StudentMapper;
import com.group7.sis.repository.ScheduledCourseRepository;
import com.group7.sis.repository.StudentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentService {
    private final ProgramRepository programRepository;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ScheduledCourseRepository scheduledCourseRepository;

    //cant add scheduleId to enrolledCourse: "Detached entity passed to persist: com.group7.sis.entity.users.student.AcademicInfoEntity"
    public StudentEntity addStudent(StudentDTO studentDTO){
        StudentEntity student = studentMapper.toEntity(studentDTO);


        if (studentDTO.getAcademicInfo() != null && !studentDTO.getAcademicInfo().isEmpty()) {
            for (AcademicInfoDTO academicInfoDTO : studentDTO.getAcademicInfo()) {
                AcademicInfoEntity academicInfoEntity = studentMapper.toAcademicInfoEntity(academicInfoDTO);

                academicInfoEntity.setEnrolledProgram(programRepository.findById(academicInfoDTO.getEnrolledProgramId()).orElseThrow(() -> new RuntimeException("Program not found.")));
                // academicInfoEntity.setSection(null); ignore for now

                if (academicInfoDTO.getEnrolledCourses() != null && !academicInfoDTO.getEnrolledCourses().isEmpty()) {
                    for (EnrolledCourseDTO enrolledCourseDTO : academicInfoDTO.getEnrolledCourses()) {
                        EnrolledCourseEntity enrolledCourseEntity = studentMapper.toEnrolledCourseEntity(enrolledCourseDTO);
                        enrolledCourseEntity.setSchedule(scheduledCourseRepository.findById(enrolledCourseDTO.getScheduleId()).orElseThrow(() -> new RuntimeException("Scheduled course not found.")));
                        academicInfoEntity.getEnrolledCourses().add(enrolledCourseEntity);
                    }
                }
                student.getAcademicInfo().add(academicInfoEntity);
            }
        }
        return studentRepository.save(student);
    }

    public StudentDTO getStudent(Long id){
        return studentMapper.toDto(studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student not found,")));
    }

    //god damn
    //cant add academicinfo, enrolledcourse: The given id must not be null
    //when emptying a list: A collection with orphan deletion was no longer referenced by the owning entity
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO){
        StudentEntity student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found."));
        studentMapper.updateEntityFromDTO(studentDTO, student);

        if (studentDTO.getAcademicInfo() != null){

            Map<Long, AcademicInfoEntity> oldAcademicInfoMap = new HashMap<>();
            for (AcademicInfoEntity oldAcademicInfo : student.getAcademicInfo()) {
                oldAcademicInfoMap.put(oldAcademicInfo.getId(), oldAcademicInfo);
            }
            
            List<AcademicInfoEntity> updatedAcademicInfoEntities = new ArrayList<>();

            for (AcademicInfoDTO academicInfoDTO : studentDTO.getAcademicInfo()) {
                AcademicInfoEntity academicInfoEntity = oldAcademicInfoMap.remove(academicInfoDTO.getId());
                
                if (academicInfoEntity == null) {
                    academicInfoEntity = new AcademicInfoEntity();
                }

                studentMapper.updateAcademicInfoEntityFromDTO(academicInfoDTO, academicInfoEntity);
                academicInfoEntity.setEnrolledProgram(programRepository.findById(academicInfoDTO.getEnrolledProgramId()).orElseThrow(() -> new RuntimeException("Program not found.")));
                // academicInfoEntity.setSection(null); ignore for now

                if (academicInfoDTO.getEnrolledCourses() != null) {
                    Map<Long, EnrolledCourseEntity> oldEnrolledCourseMap = new HashMap<>();
                    for (EnrolledCourseEntity oldEnrolledCourse : academicInfoEntity.getEnrolledCourses()) {
                        oldEnrolledCourseMap.put(oldEnrolledCourse.getId(), oldEnrolledCourse);
                    }
                    List<EnrolledCourseEntity> updatedEnrolledCourseEntities = new ArrayList<>();
                    
                    for (EnrolledCourseDTO enrolledCourseDTO : academicInfoDTO.getEnrolledCourses()) {
                        EnrolledCourseEntity enrolledCourseEntity = oldEnrolledCourseMap.remove(enrolledCourseDTO.getId());
                        
                        if (enrolledCourseEntity == null) {
                            enrolledCourseEntity = new EnrolledCourseEntity();
                        }

                        studentMapper.updateEnrolledCourseEntityFromDTO(enrolledCourseDTO, enrolledCourseEntity);
                        enrolledCourseEntity.setSchedule(scheduledCourseRepository.findById(enrolledCourseDTO.getScheduleId()).orElseThrow(() -> new RuntimeException("Scheduled course not found.")));
                        updatedEnrolledCourseEntities.add(enrolledCourseEntity);
                    }
                    academicInfoEntity.getEnrolledCourses().clear();
                    academicInfoEntity.getEnrolledCourses().addAll(updatedEnrolledCourseEntities);
                } 
                updatedAcademicInfoEntities.add(academicInfoEntity);
            }
            student.getAcademicInfo().clear();
            student.getAcademicInfo().addAll(updatedAcademicInfoEntities);
        }
        return studentMapper.toDto(studentRepository.save(student));
    }

    public List<StudentEntity> getAllStudents(){
        return studentRepository.findAll();
    }
}