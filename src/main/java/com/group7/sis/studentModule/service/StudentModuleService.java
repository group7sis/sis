package com.group7.sis.studentModule.service;

import com.group7.sis.repository.AcademicInfoRepository;
import com.group7.sis.repository.CourseAssignmentRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO.EnrolledCourseDTO;
import com.group7.sis.entity.academic.DayEntity.DayName;
import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;
import com.group7.sis.entity.academic.TermEntity.TermStatus;
import com.group7.sis.entity.academic.CourseAssignmentEntity;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.DayEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.academic.TermEntity;
import com.group7.sis.entity.academic.YearEntity;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.StudentEntity.ScholasticStatus;
import com.group7.sis.entity.users.StudentEntity.StudentStatus;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.entity.users.student.EnrolledCourseEntity;
import com.group7.sis.facultyModule.entity.StudentGradeRecordEntity;
import com.group7.sis.facultyModule.repository.StudentGradeRecordRepository;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.entity.users.student.EnrolledCourseEntity.Status;
import com.group7.sis.mapper.ScheduledCourseMapper;
import com.group7.sis.mapper.StudentMapper;
import com.group7.sis.repository.ScheduledCourseRepository;
import com.group7.sis.repository.StudentRepository;
import com.group7.sis.service.queryservices.CourseQueryService;
import com.group7.sis.service.queryservices.StudentQueryService;
import com.group7.sis.service.queryservices.TermQueryService;
import com.group7.sis.studentModule.dto.SchedulesSubmissionDTO;
import com.group7.sis.studentModule.dto.StudentDashboardDataDTO;
import com.group7.sis.studentModule.dto.StudentProfileDTO;
import com.group7.sis.studentModule.dto.StudentScheduleDTO;
import com.group7.sis.studentModule.dto.paged.SchedulesCategorizedDTO;
import com.group7.sis.studentModule.dto.paged.StudentGradesPageDTO;
import com.group7.sis.dto.ScheduledCourseDTO.DayDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentModuleService {

    private final AcademicInfoRepository academicInfoRepository;
    private final StudentQueryService studentQueryService;
    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;

    private final StudentGradeRecordRepository studentGradeRecordRepository;

    private final TermQueryService termQueryService;

    private final ScheduledCourseMapper scheduledCourseMapper;
    private final ScheduledCourseRepository scheduledCourseRepository;

    private final CourseQueryService courseQueryService;
    private final CourseAssignmentRepository courseAssignmentRepository;


    public SchedulesCategorizedDTO getAvailableSchedules(Long studentId){

        StudentEntity studentEntity = studentQueryService.getStudentEntity(studentId);
        SchedulesCategorizedDTO page = new SchedulesCategorizedDTO();
        TermEntity currentTerm = getTerm();

        if (currentTerm.getStatus()!= TermStatus.ENROLLMENT){
            page.setMessage("Enrollment is closed");
            return page;
        }
        AcademicInfoEntity previousEnrollment = studentRepository.getLatestEnrollment(studentId, EnrollmentStatus.ACTIVE);

        List<ScheduledCourseEntity> availableSchedules = scheduledCourseRepository.getSchedulesForProgramCurriculum(previousEnrollment.getEnrolledProgram().getId(), previousEnrollment.getProgramCurriculum().getId(), previousEnrollment.getYearLevel(), currentTerm.getSemester(), ScheduleStatus.ACTIVE, currentTerm.getAcademicYear());

        for (ScheduledCourseEntity schedule : availableSchedules){
            StudentScheduleDTO studentScheduleDTO = scheduledCourseMapper.toStudentScheduleDTO(schedule); 

            FacultyEntity faculty = schedule.getAssignedFaculty();
            String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));

            studentScheduleDTO.setFacultyName(facultyName);

            for (DayEntity day : schedule.getDays()){
                studentScheduleDTO.getDays().add(scheduledCourseMapper.toDto(day));
            }

            page.getCurrentSchedules().add(studentScheduleDTO);
        }

        //------------------------------FAILED COURSES----------------------------------
        List<CourseAssignmentEntity> failedCourses = getFailedCourses(studentEntity);
        List<ScheduledCourseEntity> availableFailedCourseSchedules = new ArrayList<>();
        
        if(!failedCourses.isEmpty()){
            for (CourseAssignmentEntity failedCourse : failedCourses){
                for (ScheduledCourseEntity schedule : scheduledCourseRepository.getSchedulesForProgramCurriculum(failedCourse.getProgram().getId(), failedCourse.getCurriculum().getId(), failedCourse.getYearLevel(), failedCourse.getSemester(), ScheduleStatus.ACTIVE, currentTerm.getAcademicYear())){
                    availableFailedCourseSchedules.add(schedule);
                }
            }
        }

        if (!availableFailedCourseSchedules.isEmpty()) {
            for (ScheduledCourseEntity schedule : availableFailedCourseSchedules){
                StudentScheduleDTO studentScheduleDTO = scheduledCourseMapper.toStudentScheduleDTO(schedule); 

                FacultyEntity faculty = schedule.getAssignedFaculty();
                String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));

                studentScheduleDTO.setFacultyName(facultyName);

                for (DayEntity day : schedule.getDays()){
                    studentScheduleDTO.getDays().add(scheduledCourseMapper.toDto(day));
                }

                page.getFailedCourseSchedules().add(studentScheduleDTO);
            }
        }
        
        //------------------------------COURSES NOT TAKEN----------------------------------
        List<CourseAssignmentEntity> requiredCourses = new ArrayList<>();

        for (YearEntity year: previousEnrollment.getProgramCurriculum().getYears()){
            if (year.getLevel() > previousEnrollment.getYearLevel()){
                break;
            }

            for (int sem = 1; sem <= year.getTotalSemester(); sem++){
                if (year.getLevel() == previousEnrollment.getYearLevel() && sem >= currentTerm.getSemester()){
                    break;
                }

                requiredCourses.addAll(courseAssignmentRepository.getCourseAssignment(previousEnrollment.getEnrolledProgram().getId(), previousEnrollment.getProgramCurriculum().getId(), year.getLevel(), sem, CourseEntity.Status.ACTIVE));
            }    
        }

        Set<Long> takenCourseIds = new HashSet<>();

        for(AcademicInfoEntity enrollment : studentEntity.getAcademicInfo()){
            for (EnrolledCourseEntity enrolledCourse : enrollment.getEnrolledCourses()){
                takenCourseIds.add(enrolledCourse.getSchedule().getCourseAssignment().getCourse().getId());
            }
        }

        List<CourseAssignmentEntity> missingCourses = new ArrayList<>();

        for (CourseAssignmentEntity requiredCourse: requiredCourses){
            if (!takenCourseIds.contains(requiredCourse.getCourse().getId())){
                missingCourses.add(requiredCourse);
            }
        }

        if(!missingCourses.isEmpty()){
            List<ScheduledCourseEntity> availableNotTakenCourses = new ArrayList<>();

            for (CourseAssignmentEntity missing : missingCourses){
                availableNotTakenCourses.addAll(scheduledCourseRepository.getSchedulesForProgramCurriculum(missing.getProgram().getId(), missing.getCurriculum().getId(), missing.getYearLevel(), missing.getSemester(), ScheduleStatus.ACTIVE, currentTerm.getAcademicYear()));
            }

        
            for (ScheduledCourseEntity schedule : availableNotTakenCourses){
                StudentScheduleDTO studentScheduleDTO = scheduledCourseMapper.toStudentScheduleDTO(schedule); 

                FacultyEntity faculty = schedule.getAssignedFaculty();
                String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));

                studentScheduleDTO.setFacultyName(facultyName);

                for (DayEntity day : schedule.getDays()){
                    studentScheduleDTO.getDays().add(scheduledCourseMapper.toDto(day));
                }

                page.getMissedCourseSchedules().add(studentScheduleDTO);
            }
        }

        page.setMessage("Get schedules successful");
        return page;
    }

    public boolean checkGraduating(AcademicInfoEntity previousEnrollment){
        StudentEntity student = previousEnrollment.getStudent();

        List<EnrolledCourseEntity> studentPassedCourses = new ArrayList<>();

        for (AcademicInfoEntity enrollment : student.getAcademicInfo()){
            for (EnrolledCourseEntity enrolledCourse : enrollment.getEnrolledCourses()){
                if (enrolledCourse.getStatus() == Status.PASSED){
                    studentPassedCourses.add(enrolledCourse);
                }
            }
        }

        for (YearEntity year : previousEnrollment.getProgramCurriculum().getYears()){
            for (int sem = 1; sem <= year.getTotalSemester(); sem++){
                List<CourseEntity> requiredCourses = courseQueryService.getCoursesAssignedFromProgramCurriculum(previousEnrollment.getEnrolledProgram().getId(), previousEnrollment.getProgramCurriculum().getId(), year.getLevel(), sem, CourseEntity.Status.ACTIVE);
                
                for(CourseEntity requiredCourse: requiredCourses){
                    boolean found = false;

                    for (EnrolledCourseEntity enrolledCourse : studentPassedCourses){
                        CourseEntity passedCourse = enrolledCourse.getSchedule().getCourseAssignment().getCourse();

                        if(passedCourse.getId().equals(requiredCourse.getId())){
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int updateYearLevel(AcademicInfoEntity previousEnrollment){
        int yearLevel = previousEnrollment.getYearLevel();
        for (YearEntity year : previousEnrollment.getProgramCurriculum().getYears()){
            if (year.getLevel() == previousEnrollment.getYearLevel()){
                if(previousEnrollment.getSemester() == year.getTotalSemester()){
                    yearLevel+=1;
                }
            }
        }
        return yearLevel;
    }

    public SchedulesCategorizedDTO enrollInTerm(Long studentId, SchedulesSubmissionDTO schedulesSubmissionDTO){
        StudentEntity studentEntity = studentQueryService.getStudentEntity(studentId);
        SchedulesCategorizedDTO page = new SchedulesCategorizedDTO();

        if (studentEntity.getStudentStatus() == StudentStatus.DROPPED){
            page.setMessage("A DROPPED student may not enroll anymore");
            return page;
        }

        TermEntity currentTerm = getTerm();

        //check if enrollment exists in that term
        AcademicInfoEntity currentEnrollment = studentQueryService.getEnrollmentOfStudentByYearAndSem(studentId, currentTerm.getAcademicYear(), currentTerm.getSemester());

        if (currentEnrollment == null && currentTerm.getStatus() == TermStatus.ENROLLMENT){
            //create new enrollment
            AcademicInfoEntity previousEnrollment = studentRepository.getLatestEnrollment(studentId, EnrollmentStatus.ACTIVE);

            if (checkGraduating(previousEnrollment)){
                page.setMessage("Congratulations! You have completed every course.");
                studentEntity.setStudentStatus(StudentStatus.GRADUATED);
                studentRepository.save(studentEntity);
                return page;
            }

            AcademicInfoEntity newEnrollment = new AcademicInfoEntity();
            
            newEnrollment.setStudent(studentEntity);
            newEnrollment.setEnrolledProgram(previousEnrollment.getEnrolledProgram());
            newEnrollment.setProgramCurriculum(previousEnrollment.getProgramCurriculum());
            newEnrollment.setSection(previousEnrollment.getSection());

            newEnrollment.setAcademicYear(currentTerm.getAcademicYear());
            newEnrollment.setSemester(currentTerm.getSemester());
            newEnrollment.setYearLevel(updateYearLevel(previousEnrollment));

            newEnrollment.setEnrollmentDate(LocalDate.now());
            newEnrollment.setEnrollmentStatus(EnrollmentStatus.ACTIVE);

            List<ScheduledCourseEntity> availableSchedules = scheduledCourseRepository.getSchedulesForProgramCurriculum(newEnrollment.getEnrolledProgram().getId(), newEnrollment.getProgramCurriculum().getId(), newEnrollment.getYearLevel(), currentTerm.getSemester(), ScheduleStatus.ACTIVE, currentTerm.getAcademicYear());
            int allowedUnits = calculateUnits(availableSchedules);

            List<CourseAssignmentEntity> failedCourses = getFailedCourses(studentEntity);
            boolean hasFailedCourse =  !failedCourses.isEmpty();

            if (failedCourses.size() == 2){
                allowedUnits -= 3;
            }

            if(failedCourses.size() >= 3){
                studentEntity.setStudentStatus(StudentStatus.DROPPED);
                page.setMessage("Student status has been changed to DROPPED due to having at least 3 failed courses");
                studentRepository.save(studentEntity);
                return page;
            }

            newEnrollment.setUnits(allowedUnits);

            Map<Long, ScheduledCourseEntity> schedulesMap = new HashMap<>();

            for(ScheduledCourseEntity schedule : availableSchedules){
                schedulesMap.put(schedule.getId(), schedule);
            }

            for(Long scheduleId : schedulesSubmissionDTO.getCurrentSchedulesIds()){
                ScheduledCourseEntity check = schedulesMap.remove(scheduleId);
                
                if(check == null){
                    throw new RuntimeException("Could not enroll schedule id '" + scheduleId + "'. Either it does not exist in the current program term, or this has already been selected to be enrolled.");
                }

                EnrolledCourseEntity newCourseEnrollment = new EnrolledCourseEntity();
                newCourseEnrollment.setAcademicInfo(newEnrollment);
                newCourseEnrollment.setStatus(Status.ONGOING);
                newCourseEnrollment.setSchedule(check);

                StudentScheduleDTO studentScheduleDTO = scheduledCourseMapper.toStudentScheduleDTO(check); 

                FacultyEntity faculty = check.getAssignedFaculty();
                String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));

                studentScheduleDTO.setFacultyName(facultyName);

                for (DayEntity day : check.getDays()){
                    studentScheduleDTO.getDays().add(scheduledCourseMapper.toDto(day));
                }
            
                page.getCurrentSchedules().add(studentScheduleDTO);
                newEnrollment.getEnrolledCourses().add(newCourseEnrollment);
            }


            //------------------------------COURSES NOT TAKEN--------------------------------
            Map<Long, ScheduledCourseEntity> missedCourseSchedulesMap = new HashMap<>();
            List<CourseAssignmentEntity> requiredCourses = new ArrayList<>();

            for (YearEntity year: previousEnrollment.getProgramCurriculum().getYears()){
            if (year.getLevel() > previousEnrollment.getYearLevel()){
                break;
            }

            for (int sem = 1; sem <= year.getTotalSemester(); sem++){
                if (year.getLevel() == previousEnrollment.getYearLevel() && sem >= currentTerm.getSemester()){
                    break;
                }

                requiredCourses.addAll(courseAssignmentRepository.getCourseAssignment(previousEnrollment.getEnrolledProgram().getId(), previousEnrollment.getProgramCurriculum().getId(), year.getLevel(), sem, CourseEntity.Status.ACTIVE));
                }    
            }

            Set<Long> takenCourseIds = new HashSet<>();

            for(AcademicInfoEntity enrollment : studentEntity.getAcademicInfo()){
                for (EnrolledCourseEntity enrolledCourse : enrollment.getEnrolledCourses()){
                    takenCourseIds.add(enrolledCourse.getSchedule().getCourseAssignment().getCourse().getId());
                }
            }

            List<CourseAssignmentEntity> missingCourses = new ArrayList<>();

            for (CourseAssignmentEntity requiredCourse: requiredCourses){
                if (!takenCourseIds.contains(requiredCourse.getCourse().getId())){
                    missingCourses.add(requiredCourse);
                }
            }

            List<ScheduledCourseEntity> availableNotTakenCourses = new ArrayList<>();
            if(!missingCourses.isEmpty()){
                for (CourseAssignmentEntity missing : missingCourses){
                    availableNotTakenCourses.addAll(scheduledCourseRepository.getSchedulesForProgramCurriculum(missing.getProgram().getId(), missing.getCurriculum().getId(), missing.getYearLevel(), missing.getSemester(), ScheduleStatus.ACTIVE, currentTerm.getAcademicYear()));
                }

            
                for(ScheduledCourseEntity schedule : availableNotTakenCourses){
                    missedCourseSchedulesMap.put(schedule.getId(), schedule);
                }

                for (Long scheduleId : schedulesSubmissionDTO.getMissedCourseSchedulesIds()){
                    ScheduledCourseEntity check = missedCourseSchedulesMap.remove(scheduleId);

                    if(check == null){
                        throw new RuntimeException("[NOT TAKEN SCHEDULES] Could not enroll schedule id '" + scheduleId + "'. Either it does not exist in the current program term, or this has already been selected to be enrolled.");
                    }

                    EnrolledCourseEntity newNotTakenCourseEnrollment = new EnrolledCourseEntity();
                    newNotTakenCourseEnrollment.setAcademicInfo(newEnrollment);
                    newNotTakenCourseEnrollment.setStatus(Status.ONGOING);
                    newNotTakenCourseEnrollment.setSchedule(check);
                    

                    StudentScheduleDTO studentScheduleDTO = scheduledCourseMapper.toStudentScheduleDTO(check); 

                    FacultyEntity faculty = check.getAssignedFaculty();
                    String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));

                    studentScheduleDTO.setFacultyName(facultyName);

                    for (DayEntity day : check.getDays()){
                        studentScheduleDTO.getDays().add(scheduledCourseMapper.toDto(day));
                    }
                
                    page.getMissedCourseSchedules().add(studentScheduleDTO);
                    newEnrollment.getEnrolledCourses().add(newNotTakenCourseEnrollment);
                }
            }

            //------------------------------FAILED COURSES--------------------------------
            Map<Long, ScheduledCourseEntity> failedCourseSchedulesMap = new HashMap<>();
            List<ScheduledCourseEntity> availableFailedCourseSchedules = new ArrayList<>();

            if (!failedCourses.isEmpty()){    
                for (CourseAssignmentEntity failedCourse : failedCourses){
                    for (ScheduledCourseEntity schedule : scheduledCourseRepository.getSchedulesForProgramCurriculum(failedCourse.getProgram().getId(), failedCourse.getCurriculum().getId(), failedCourse.getYearLevel(), failedCourse.getSemester(), ScheduleStatus.ACTIVE, currentTerm.getAcademicYear())){
                        availableFailedCourseSchedules.add(schedule);
                    }
                }

        
                for(ScheduledCourseEntity schedule : availableFailedCourseSchedules){
                    failedCourseSchedulesMap.put(schedule.getId(), schedule);
                }

                for(Long scheduleId : schedulesSubmissionDTO.getFailedCourseSchedulesIds()){
                    ScheduledCourseEntity check = failedCourseSchedulesMap.remove(scheduleId);
                    
                    if(check == null){
                        throw new RuntimeException("[FAILED SCHEDULES] Could not enroll schedule id '" + scheduleId + "'. Either it does not exist in the current program term, or this has already been selected to be enrolled.");
                    }

                    EnrolledCourseEntity newFailedCourseEnrollment = new EnrolledCourseEntity();
                    newFailedCourseEnrollment.setAcademicInfo(newEnrollment);
                    newFailedCourseEnrollment.setStatus(Status.ONGOING);
                    newFailedCourseEnrollment.setSchedule(check);
                    

                    StudentScheduleDTO studentScheduleDTO = scheduledCourseMapper.toStudentScheduleDTO(check); 

                    FacultyEntity faculty = check.getAssignedFaculty();
                    String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));

                    studentScheduleDTO.setFacultyName(facultyName);

                    for (DayEntity day : check.getDays()){
                        studentScheduleDTO.getDays().add(scheduledCourseMapper.toDto(day));
                    }
                
                    page.getFailedCourseSchedules().add(studentScheduleDTO);
                    newEnrollment.getEnrolledCourses().add(newFailedCourseEnrollment);
                }
            }
            
            //-------------------------------------END---------------------------------------

            int enrolledUnits = calculateTotalUnitsEnrolled(newEnrollment.getEnrolledCourses());
            
            if (enrolledUnits > allowedUnits){
                throw new RuntimeException("Units exceeded. Allowed: " + newEnrollment.getUnits() +" ; Enrolled: " + enrolledUnits);
            }

            if (!page.getFailedCourseSchedules().isEmpty() || !page.getMissedCourseSchedules().isEmpty()){
                newEnrollment.setScholasticStatus(ScholasticStatus.IRREGULAR);
                studentEntity.setScholasticStatus(ScholasticStatus.IRREGULAR);
            } 
            else if(hasFailedCourse && page.getFailedCourseSchedules().isEmpty() && page.getMissedCourseSchedules().isEmpty()){
                newEnrollment.setScholasticStatus(ScholasticStatus.REGULAR_WARNING);
                studentEntity.setScholasticStatus(ScholasticStatus.REGULAR_WARNING);
            }
            else {
                newEnrollment.setScholasticStatus(ScholasticStatus.REGULAR);
                studentEntity.setScholasticStatus(ScholasticStatus.REGULAR);
            }

            academicInfoRepository.save(newEnrollment);
            studentRepository.save(studentEntity);
            createStudentGradeRecordPerEnrollment(academicInfoRepository.findEntityById(newEnrollment.getId()));
            page.setMessage("Enrollment succesful");
        }
        else{
            page.setMessage("Enrollment unavailable");
        }
        return page;
    }

    private List<CourseAssignmentEntity> getFailedCourses(StudentEntity student){
        List<CourseAssignmentEntity> failedCourses = new ArrayList<>();

        for(AcademicInfoEntity academicInfoEntity : student.getAcademicInfo()){
            for(EnrolledCourseEntity enrolledCourseEntity : academicInfoEntity.getEnrolledCourses()){
                if (enrolledCourseEntity.getStatus() == Status.FAILED){
                    boolean alreadyAdded = false;

                    for (CourseAssignmentEntity courseAssignment : failedCourses){
                        if (courseAssignment.getId().equals(enrolledCourseEntity.getSchedule().getCourseAssignment().getId())){
                            alreadyAdded = true;
                            break;
                        }
                    }

                    if(!alreadyAdded){
                        failedCourses.add(enrolledCourseEntity.getSchedule().getCourseAssignment());
                    }
                }
            }
        }
        return failedCourses;
    }

    private int calculateTotalUnitsEnrolled(List<EnrolledCourseEntity> enrolledCourses){
        int enrolledUnits = 0;
        for (EnrolledCourseEntity enrolledCourse : enrolledCourses){
            enrolledUnits += enrolledCourse.getSchedule().getCourseAssignment().getCourse().getUnits();
        }
        return enrolledUnits;
    }

    private int calculateUnits(List<ScheduledCourseEntity> schedules){
        int totalUnits = 0;
        for (ScheduledCourseEntity schedule : schedules){
            totalUnits += schedule.getCourseAssignment().getCourse().getUnits();

        }
        return totalUnits;
    }

    private void createStudentGradeRecordPerEnrollment(AcademicInfoEntity currentEnrollment){
        if(currentEnrollment.getEnrolledCourses() != null){
            for (EnrolledCourseEntity enrolledCourse : currentEnrollment.getEnrolledCourses()){
                createStudentGradeRecord(enrolledCourse);
            }
        }
    }

    private void createStudentGradeRecord(EnrolledCourseEntity enrolledCourseEntity){
        StudentGradeRecordEntity studentGradeRecord = new StudentGradeRecordEntity();
        studentGradeRecord.setEnrolledCourse(enrolledCourseEntity);
        studentGradeRecord.setFinalized(false);
        studentGradeRecordRepository.save(studentGradeRecord);
    }

    public List<StudentScheduleDTO> getSchedule(Long studentId){
        List<StudentScheduleDTO> schedules = new ArrayList<>();

        TermEntity currentTerm = getTerm();
        AcademicInfoEntity currentEnrollment = studentQueryService.getEnrollmentOfStudentByYearAndSem(studentId, currentTerm.getAcademicYear(), currentTerm.getSemester());

        if (currentEnrollment == null){
            throw new RuntimeException("No enrollment found in current term");
        }

        for (EnrolledCourseEntity enrolledCourse : currentEnrollment.getEnrolledCourses()){
            ScheduledCourseEntity scheduledCourseEntity = enrolledCourse.getSchedule();
            StudentScheduleDTO scheduleDTO = scheduledCourseMapper.toStudentScheduleDTO(scheduledCourseEntity);
            
            List<DayDTO> dayDtos = new ArrayList<>();
            for (DayEntity day : scheduledCourseEntity.getDays()){
                dayDtos.add(scheduledCourseMapper.toDto(day));
            }

            FacultyEntity faculty = enrolledCourse.getSchedule().getAssignedFaculty();
            String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));
            
            scheduleDTO.setFacultyName(facultyName);
            scheduleDTO.setDays(dayDtos);
            schedules.add(scheduleDTO);
        }
        return schedules;
    }

    public StudentGradesPageDTO getGrades(Long studentId){
        StudentGradesPageDTO page = new StudentGradesPageDTO();

        TermEntity currentTerm = getTerm();
        AcademicInfoEntity currentEnrollment = studentQueryService.getEnrollmentOfStudentByYearAndSem(studentId, currentTerm.getAcademicYear(), currentTerm.getSemester());

        if (currentEnrollment == null){
            throw new RuntimeException("No enrollment for the current term");
        }

        List<EnrolledCourseDTO> grades = new ArrayList<>();

        for (EnrolledCourseEntity enrolledCourse : currentEnrollment.getEnrolledCourses()){
            EnrolledCourseDTO dto = studentMapper.toEnrolledCourseDto(enrolledCourse);

            FacultyEntity faculty = enrolledCourse.getSchedule().getAssignedFaculty();
            String facultyName = faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName()) + (faculty.getProfile().getSuffix() == null ? "" : (" " + faculty.getProfile().getSuffix()));
            dto.setFacultyName(facultyName);

            grades.add(dto);
        }

        page.setGwa(calculateGwa(currentEnrollment));
        page.setEnrolledCourses(grades);
        return page;
    }

    public StudentProfileDTO getProfile(Long studentId){
        TermEntity currentTerm = getTerm();
        
        StudentEntity studentEntity = studentQueryService.getStudentEntity(studentId);
        StudentProfileDTO profile = studentMapper.toProfileDTO(studentEntity);

        AcademicInfoEntity currentEnrollment = studentQueryService.getEnrollmentOfStudentByYearAndSem(studentId, currentTerm.getAcademicYear(), currentTerm.getSemester());

        if (currentEnrollment == null){
            currentEnrollment = studentRepository.getLatestEnrollment(studentId, EnrollmentStatus.ACTIVE);
        }

        profile.setEnrolledProgram(currentEnrollment.getEnrolledProgram().getTitle());
        profile.setYearLevel(currentEnrollment.getYearLevel());

        return profile;
    }

    public StudentProfileDTO updateProfile(Long studentId, StudentProfileDTO studentProfileDTO){
        StudentEntity studentEntity = studentQueryService.getStudentEntity(studentId);

        studentEntity.getProfile().setFirstName(studentProfileDTO.getFirstName());
        studentEntity.getProfile().setMiddleName(studentProfileDTO.getMiddleName());
        studentEntity.getProfile().setLastName(studentProfileDTO.getLastName());
        studentEntity.getProfile().setSuffix(studentProfileDTO.getSuffix());
        studentEntity.getProfile().setMobileNumber(studentProfileDTO.getMobileNumber());

        studentRepository.save(studentEntity);        

        return getProfile(studentId);
        
    }
    
    public StudentDashboardDataDTO getDashboardData(Long studentId){
        TermEntity currentTerm = getTerm();
        StudentDashboardDataDTO data = new StudentDashboardDataDTO();

        StudentEntity studentEntity = studentQueryService.getStudentEntity(studentId);

        String studentName = studentEntity.getProfile().getFirstName() + (studentEntity.getProfile().getMiddleName() == null ? "" : (" " + studentEntity.getProfile().getMiddleName())) + (" " + studentEntity.getProfile().getLastName()) + (studentEntity.getProfile().getSuffix() == null ? "" : (" " + studentEntity.getProfile().getSuffix()));

        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        Month month = LocalDate.now().getMonth();

        String dayOfTheWeekFormatted = currentDay.name().charAt(0) + currentDay.name().substring(1).toLowerCase();
        String monthFormatted = month.name().charAt(0) + month.name().substring(1).toLowerCase();


        AcademicInfoEntity currentEnrollment = studentQueryService.getEnrollmentOfStudentByYearAndSem(studentId, currentTerm.getAcademicYear(), currentTerm.getSemester());

        data.setCurrentMonth(monthFormatted);
        data.setCurrentDay(String.valueOf(LocalDate.now().getDayOfMonth()));
        data.setCurrentYear(String.valueOf(LocalDate.now().getYear()));
        data.setCurrentDayOfTheWeek(dayOfTheWeekFormatted);
        data.setName(studentName);

        if (currentEnrollment != null){
            data.setGwa(calculateGwa(currentEnrollment));
            data.setGradeCompletionStatus(checkGradeCompletionStatus(currentEnrollment));
            data.setProgramCode(currentEnrollment.getEnrolledProgram().getCode());
            data.setSectionName(currentEnrollment.getSection().getName());
            data.setTotalEnrolledCourses(currentEnrollment.getEnrolledCourses().size());
            data.setTotalUnits(currentEnrollment.getUnits());
            data.setSchedulesToday(getTodaySchedules(currentEnrollment, currentDay));
        }
        return data;
    }

    private double calculateGwa(AcademicInfoEntity currentEnrollment){
        double total = 0;
        double totalUnits = 0;
        for (EnrolledCourseEntity enrolledCourse : currentEnrollment.getEnrolledCourses()){
            if (enrolledCourse.getGrade() != null){
                total += (enrolledCourse.getGrade() *  enrolledCourse.getSchedule().getCourseAssignment().getCourse().getUnits());
                totalUnits += enrolledCourse.getSchedule().getCourseAssignment().getCourse().getUnits();
            }
        }
        
        if (totalUnits == 0){
            return 0;
        }

        double gwa = Math.round((total / totalUnits) * 100.0) / 100.0;
        return gwa;
    }

    private String checkGradeCompletionStatus(AcademicInfoEntity currentEnrollment){
        for (EnrolledCourseEntity enrolledCourse : currentEnrollment.getEnrolledCourses()){
            if (enrolledCourse.getGrade() == null){
                return "PENDING";
            }
        }
        return "FINALIZED";
    }

    private TermEntity getTerm(){
        long id = 1;
        return termQueryService.getTermEntity(id);
    }

    private List<ScheduledCourseDTO> getTodaySchedules(AcademicInfoEntity currentEnrollment, DayOfWeek currentDay){
        List<ScheduledCourseDTO> todaySchedules = new ArrayList<>();
        DayName currentDayName = DayName.valueOf(currentDay.name());

        for (EnrolledCourseEntity enrolledCourse : currentEnrollment.getEnrolledCourses()){
            for (DayEntity day : enrolledCourse.getSchedule().getDays()){
                if (day.getName() == currentDayName){
                    todaySchedules.add(scheduledCourseMapper.toDto(enrolledCourse.getSchedule()));
                    break;
                }
            }
        }

        return todaySchedules;
    }
}
