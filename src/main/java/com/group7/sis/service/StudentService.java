package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO.EnrolledCourseDTO;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.entity.users.student.EnrolledCourseEntity;
import com.group7.sis.mapper.StudentMapper;
import com.group7.sis.repository.StudentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    private final ProgramService programService;
    private final SectionService sectionService;
    private final ScheduleService scheduleService;

    //cant add scheduleId to enrolledCourse: "Detached entity passed to persist: com.group7.sis.entity.users.student.AcademicInfoEntity"
    public StudentEntity addStudent(StudentDTO studentDTO){
        StudentEntity student = studentMapper.toEntity(studentDTO);


        if (studentDTO.getAcademicInfo() != null && !studentDTO.getAcademicInfo().isEmpty()) {
            for (AcademicInfoDTO academicInfoDTO : studentDTO.getAcademicInfo()) {
                AcademicInfoEntity academicInfoEntity = studentMapper.toAcademicInfoEntity(academicInfoDTO);

                academicInfoEntity.setEnrolledProgram(programService.getProgramEntity(academicInfoDTO.getEnrolledProgramId()));
                academicInfoEntity.setSection(sectionService.getSectionEntity(academicInfoDTO.getSectionId()));

                if (academicInfoDTO.getEnrolledCourses() != null && !academicInfoDTO.getEnrolledCourses().isEmpty()) {
                    for (EnrolledCourseDTO enrolledCourseDTO : academicInfoDTO.getEnrolledCourses()) {
                        EnrolledCourseEntity enrolledCourseEntity = studentMapper.toEnrolledCourseEntity(enrolledCourseDTO);
                        enrolledCourseEntity.setSchedule(scheduleService.getScheduledCourseEntity(enrolledCourseDTO.getScheduleId()));
                        academicInfoEntity.getEnrolledCourses().add(enrolledCourseEntity);
                    }
                }
                student.getAcademicInfo().add(academicInfoEntity);
            }
        }
        return studentRepository.save(student);
    }

    public StudentDTO getStudent(Long id){
        return studentMapper.toDto(getStudentEntity(id));
    }

    //god damn
    //cant add academicinfo, enrolledcourse: The given id must not be null
    //when emptying a list: A collection with orphan deletion was no longer referenced by the owning entity
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO){
        StudentEntity student = getStudentEntity(id);
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
                academicInfoEntity.setEnrolledProgram(programService.getProgramEntity(academicInfoDTO.getEnrolledProgramId()));
                academicInfoEntity.setSection(sectionService.getSectionEntity(academicInfoDTO.getSectionId()));

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
                        enrolledCourseEntity.setSchedule(scheduleService.getScheduledCourseEntity(enrolledCourseDTO.getScheduleId()));
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


    //Helpers
    public StudentEntity getStudentEntity(Long id){
        return studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student not found."));
    }
    
    public StudentDTO getStudentByAcademicInfoId(Long academicInfoId){
        return studentMapper.toDto(studentRepository.findByAcademicInfoId(academicInfoId)
        .orElseThrow(() -> new RuntimeException("Student not found.")));
    }
}