package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.StudentDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO.EnrolledCourseDTO;
import com.group7.sis.dto.pagedResponse.StudentsPageDTO;
import com.group7.sis.dto.statsDtos.StudentsStatsDTO;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.entity.users.student.EnrolledCourseEntity;
import com.group7.sis.facultyModule.entity.StudentGradeRecordEntity;
import com.group7.sis.facultyModule.repository.StudentGradeRecordRepository;
import com.group7.sis.entity.users.StudentEntity.ScholasticStatus;
import com.group7.sis.mapper.StudentMapper;
import com.group7.sis.repository.StudentRepository;
import com.group7.sis.service.queryservices.CurriculumQueryService;
import com.group7.sis.service.queryservices.ProgramQueryService;
import com.group7.sis.service.queryservices.ScheduleQueryService;
import com.group7.sis.service.queryservices.SectionQueryService;
import com.group7.sis.service.queryservices.StudentQueryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentService {
    private final StudentQueryService studentQueryService;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    private final StudentGradeRecordRepository studentGradeRecordRepository;

    private final ProgramQueryService programQueryService;
    private final SectionQueryService sectionQueryService;
    private final ScheduleQueryService scheduleQueryService;
    private final CurriculumQueryService curriculumQueryService;

    public StudentEntity addStudent(StudentDTO studentDTO) {
        StudentEntity student = studentMapper.toEntity(studentDTO);

        student.setStudentNumber(studentNumberGenerator(student.getYearAdmitted()));

        if (studentDTO.getAcademicInfo() != null && !studentDTO.getAcademicInfo().isEmpty()) {

            checkForAcadInfoDuplicates(studentDTO.getAcademicInfo());

            for (AcademicInfoDTO academicInfoDTO : studentDTO.getAcademicInfo()) {
                AcademicInfoEntity academicInfoEntity = studentMapper.toAcademicInfoEntity(academicInfoDTO);
                academicInfoEntity.setStudent(student);
                
                if (academicInfoDTO.getEnrolledProgramId() != null) {
                    academicInfoEntity.setEnrolledProgram(
                            programQueryService.getProgramEntity(academicInfoDTO.getEnrolledProgramId()));
                }

                if (academicInfoDTO.getProgramCurriculumId() != null) {
                    validateCurriculum(academicInfoDTO.getEnrolledProgramId(),
                            academicInfoDTO.getProgramCurriculumId());
                    academicInfoEntity.setProgramCurriculum(
                            curriculumQueryService.getCurriculumEntity(academicInfoDTO.getProgramCurriculumId()));
                }

                if (academicInfoDTO.getSectionId() != null) {
                    validateSectionYearLevel(academicInfoDTO);
                    academicInfoEntity.setSection(sectionQueryService.getSectionEntity(academicInfoDTO.getSectionId()));
                }

                if (academicInfoDTO.getEnrolledCourses() != null && !academicInfoDTO.getEnrolledCourses().isEmpty()) {
                    checkForEnrolledCoursesDuplicates(academicInfoDTO);

                    for (EnrolledCourseDTO enrolledCourseDTO : academicInfoDTO.getEnrolledCourses()) {
                        EnrolledCourseEntity enrolledCourseEntity = studentMapper.toEnrolledCourseEntity(enrolledCourseDTO);
                        enrolledCourseEntity.setAcademicInfo(academicInfoEntity);

                        if (enrolledCourseDTO.getScheduleId() != null) {
                            validateAcademicYear(academicInfoDTO.getAcademicYear(), scheduleQueryService
                                    .getScheduledCourseEntity(enrolledCourseDTO.getScheduleId()).getAcademicYear());
                            enrolledCourseEntity.setSchedule(
                                    scheduleQueryService.getScheduledCourseEntity(enrolledCourseDTO.getScheduleId()));
                        }
                        academicInfoEntity.getEnrolledCourses().add(enrolledCourseEntity);
                        createStudentGradeRecord(enrolledCourseEntity);
                    }
                }
                student.getAcademicInfo().add(academicInfoEntity);
            }
        }
        return studentRepository.save(student);
    }

    private void createStudentGradeRecord(EnrolledCourseEntity enrolledCourseEntity){
        StudentGradeRecordEntity studentGradeRecord = new StudentGradeRecordEntity();
        studentGradeRecord.setEnrolledCourse(enrolledCourseEntity);
        studentGradeRecord.setFinalized(false);
        studentGradeRecordRepository.save(studentGradeRecord);
    }

    public StudentDTO getStudent(Long id) {
        return studentMapper.toDto(studentQueryService.getStudentEntity(id));
    }

    // god damn
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        StudentEntity student = studentQueryService.getStudentEntity(id);
        studentMapper.updateEntityFromDTO(studentDTO, student);

        if (studentDTO.getAcademicInfo() != null) {

            Map<Long, AcademicInfoEntity> oldAcademicInfoMap = new HashMap<>();
            for (AcademicInfoEntity oldAcademicInfo : student.getAcademicInfo()) {
                oldAcademicInfoMap.put(oldAcademicInfo.getId(), oldAcademicInfo);
            }

            List<AcademicInfoEntity> updatedAcademicInfoEntities = new ArrayList<>();

            checkForAcadInfoDuplicates(studentDTO.getAcademicInfo());

            for (AcademicInfoDTO academicInfoDTO : studentDTO.getAcademicInfo()) {
                AcademicInfoEntity academicInfoEntity = oldAcademicInfoMap.remove(academicInfoDTO.getId());

                if (academicInfoEntity == null) {
                    academicInfoEntity = new AcademicInfoEntity();
                }

                studentMapper.updateAcademicInfoEntityFromDTO(academicInfoDTO, academicInfoEntity);
                academicInfoEntity.setStudent(student);

                if (academicInfoDTO.getEnrolledProgramId() != null) {
                    academicInfoEntity.setEnrolledProgram(
                            programQueryService.getProgramEntity(academicInfoDTO.getEnrolledProgramId()));
                }

                if (academicInfoDTO.getProgramCurriculumId() != null) {
                    validateCurriculum(academicInfoDTO.getEnrolledProgramId(),
                            academicInfoDTO.getProgramCurriculumId());
                    academicInfoEntity.setProgramCurriculum(
                            curriculumQueryService.getCurriculumEntity(academicInfoDTO.getProgramCurriculumId()));
                }

                if (academicInfoDTO.getSectionId() != null) {
                    validateSectionYearLevel(academicInfoDTO);
                    academicInfoEntity.setSection(sectionQueryService.getSectionEntity(academicInfoDTO.getSectionId()));
                }

                if (academicInfoDTO.getEnrolledCourses() != null) {
                    Map<Long, EnrolledCourseEntity> oldEnrolledCourseMap = new HashMap<>();
                    for (EnrolledCourseEntity oldEnrolledCourse : academicInfoEntity.getEnrolledCourses()) {
                        oldEnrolledCourseMap.put(oldEnrolledCourse.getId(), oldEnrolledCourse);
                    }
                    List<EnrolledCourseEntity> updatedEnrolledCourseEntities = new ArrayList<>();

                    checkForEnrolledCoursesDuplicates(academicInfoDTO);

                    for (EnrolledCourseDTO enrolledCourseDTO : academicInfoDTO.getEnrolledCourses()) {
                        EnrolledCourseEntity enrolledCourseEntity = oldEnrolledCourseMap
                                .remove(enrolledCourseDTO.getId());

                        if (enrolledCourseEntity == null) {
                            enrolledCourseEntity = new EnrolledCourseEntity();
                        }

                        studentMapper.updateEnrolledCourseEntityFromDTO(enrolledCourseDTO, enrolledCourseEntity);
                        enrolledCourseEntity.setAcademicInfo(academicInfoEntity);
                        
                        if (enrolledCourseDTO.getScheduleId() != null) {
                            validateAcademicYear(academicInfoDTO.getAcademicYear(), scheduleQueryService
                                    .getScheduledCourseEntity(enrolledCourseDTO.getScheduleId()).getAcademicYear());
                            enrolledCourseEntity.setSchedule(
                                    scheduleQueryService.getScheduledCourseEntity(enrolledCourseDTO.getScheduleId()));
                        }
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

    public StudentsPageDTO getAllStudents(String name, StudentStatus studentStatus, ScholasticStatus scholasticStatus, Long programId, Long curriculumId, String academicYear, Integer yearLevel, Long sectionId) {
        StudentsPageDTO page = new StudentsPageDTO();
        StudentsStatsDTO stats = getStudentsStats(name, studentStatus, scholasticStatus, programId, curriculumId, academicYear, yearLevel, sectionId);

        List<StudentDTO> students = new ArrayList<>();
        for (StudentEntity student : studentRepository.search(name, studentStatus, scholasticStatus, programId, curriculumId, academicYear, yearLevel, sectionId)) {
            students.add(studentMapper.toDto(student));
        }

        page.setStudents(students);
        page.setStats(stats);
        
        return page;
    }

        private StudentsStatsDTO getStudentsStats(String name, StudentStatus studentStatus, ScholasticStatus scholasticStatus, Long programId, Long curriculumId, String academicYear, Integer yearLevel, Long sectionId){
        StudentsStatsDTO stats = new StudentsStatsDTO();

        stats.setTotalStudents(studentRepository.countSearch(name, studentStatus, scholasticStatus, programId, curriculumId, academicYear, yearLevel, sectionId));
        stats.setTotalActiveStudents(studentRepository.countSearch(name, StudentStatus.ACTIVE, scholasticStatus, programId, curriculumId, academicYear, yearLevel, sectionId));
        stats.setTotalIrregularStudents(studentRepository.countSearch(name, studentStatus, ScholasticStatus.IRREGULAR, programId, curriculumId, academicYear, yearLevel, sectionId));
        stats.setTotalGraduatedStudents(studentRepository.countSearch(name, StudentStatus.GRADUATED, scholasticStatus, programId, curriculumId, academicYear, yearLevel, sectionId));   
        
        return stats;
    }

    private String studentNumberGenerator(String yearAdmission) {
        StudentEntity latest = studentRepository.findTopByYearAdmittedOrderByStudentNumberDesc(yearAdmission);
        int seq;
        if (latest == null) {
            seq = 1;
        } else {
            String ref = latest.getStudentNumber().substring(latest.getStudentNumber().lastIndexOf("-") + 1);

            seq = Integer.parseInt(ref) + 1;
        }

        String startYear = yearAdmission.substring(0, 4);

        String id = "STU-" + startYear + "-" + String.format("%04d", seq);
        return id;
    }

    private void validateSectionYearLevel(AcademicInfoDTO academicInfoDTO) {
        if (academicInfoDTO.getYearLevel() != sectionQueryService.getSectionEntity(academicInfoDTO.getSectionId())
                .getYearLevel()) {
            throw new RuntimeException("Cannot enroll in section due to incompatible year level");
        }
    }

    private void validateAcademicYear(String year1, String year2) {
        if (!year1.equals(year2)) {
            throw new RuntimeException("Academic years must match");
        }
    }

    private void validateCurriculum(Long programId, Long curriculumId) {
        ProgramEntity program = programQueryService.getProgramEntity(programId);
        CurriculumEntity curriculum = curriculumQueryService.getCurriculumEntity(curriculumId);

        if (!program.getCurriculums().contains(curriculum)) {
            throw new RuntimeException("Curricilum must belong to enrolled program");
        }
    }

    private void checkForAcadInfoDuplicates(List<AcademicInfoDTO> academicInfoDTOs) {

        for (int i = 0; i < academicInfoDTOs.size(); i++) {
            AcademicInfoDTO current = academicInfoDTOs.get(i);

            for (int j = i + 1; j < academicInfoDTOs.size(); j++) {
                AcademicInfoDTO compare = academicInfoDTOs.get(j);

                if (current.getAcademicYear().equals(compare.getAcademicYear())
                        && current.getSemester() == compare.getSemester()) {
                    throw new RuntimeException("Enrollment for the year '" + current.getAcademicYear()
                            + "' and semester '" + current.getSemester() + "' already exists");
                }

            }
        }
    }

    private void checkForEnrolledCoursesDuplicates(AcademicInfoDTO academicInfoDTO) {
        List<EnrolledCourseDTO> enrolledCourseDTOs = academicInfoDTO.getEnrolledCourses();

        for (int i = 0; i < enrolledCourseDTOs.size(); i++) {
            EnrolledCourseDTO current = enrolledCourseDTOs.get(i);

            for (int j = i + 1; j < enrolledCourseDTOs.size(); j++) {
                EnrolledCourseDTO compare = enrolledCourseDTOs.get(j);

                if (current.getScheduleId() == compare.getScheduleId()) {
                    throw new RuntimeException("Enrollment for the Course schedule '" + current.getScheduleId()
                            + "' in year '" + academicInfoDTO.getAcademicYear() + "' and semester '"
                            + academicInfoDTO.getSemester() + "' already exists");
                }
            }
        }
    }
}