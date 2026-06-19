package com.group7.sis.studentModule.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.dto.StudentDTO.AcademicInfoDTO.EnrolledCourseDTO;
import com.group7.sis.entity.academic.DayEntity.DayName;
import com.group7.sis.entity.academic.DayEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.academic.TermEntity;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.StudentEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.entity.users.student.EnrolledCourseEntity;
import com.group7.sis.mapper.ScheduledCourseMapper;
import com.group7.sis.mapper.StudentMapper;
import com.group7.sis.repository.StudentRepository;
import com.group7.sis.service.queryservices.StudentQueryService;
import com.group7.sis.service.queryservices.TermQueryService;
import com.group7.sis.studentModule.dto.StudentDashboardDataDTO;
import com.group7.sis.studentModule.dto.StudentProfileDTO;
import com.group7.sis.studentModule.dto.StudentScheduleDTO;
import com.group7.sis.studentModule.dto.paged.StudentGradesPageDTO;
import com.group7.sis.dto.ScheduledCourseDTO.DayDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentModuleService {

    private final StudentQueryService studentQueryService;
    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;

    private final TermQueryService termQueryService;

    private final ScheduledCourseMapper scheduledCourseMapper;



    public List<StudentScheduleDTO> getSchedule(Long studentId){
        List<StudentScheduleDTO> schedules = new ArrayList<>();

        TermEntity currentTerm = getTerm();
        AcademicInfoEntity currentEnrollment = studentQueryService.getEnrollmentOfStudentByYearAndSem(studentId, currentTerm.getAcademicYear(), currentTerm.getSemester());

        
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
