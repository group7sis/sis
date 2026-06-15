package com.group7.sis.facultyModule.service;

import com.group7.sis.facultyModule.repository.GradeConfigurationRepository;
import com.group7.sis.facultyModule.repository.StudentGradeRecordRepository;
import com.group7.sis.service.queryservices.StudentQueryService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.entity.academic.TermEntity;
import com.group7.sis.entity.academic.DayEntity.DayName;
import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.entity.users.ProfileEntity.Sex;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.facultyModule.dtos.FacultyProfileDTO;
import com.group7.sis.facultyModule.dtos.GradeConfigurationDTO;
import com.group7.sis.facultyModule.dtos.StudentGradeRecordDTO;
import com.group7.sis.facultyModule.dtos.paged.EncodeGradePageDTO;
import com.group7.sis.facultyModule.dtos.paged.FacultyDashboardPageDTO;
import com.group7.sis.facultyModule.dtos.paged.SchedulesByDayPageDTO;
import com.group7.sis.facultyModule.dtos.statsDtos.FacultyDashboardStatsDTO;
import com.group7.sis.facultyModule.entity.GradeConfigurationEntity;
import com.group7.sis.facultyModule.entity.StudentGradeRecordEntity;
import com.group7.sis.facultyModule.mapper.GradeConfigurationMapper;
import com.group7.sis.facultyModule.mapper.StudentGradeRecordMapper;
import com.group7.sis.mapper.FacultyMapper;
import com.group7.sis.mapper.ScheduledCourseMapper;
import com.group7.sis.repository.FacultyRepository;
import com.group7.sis.service.queryservices.FacultyQueryService;
import com.group7.sis.service.queryservices.ScheduleQueryService;
import com.group7.sis.service.queryservices.TermQueryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacultyModuleService {

    private final StudentGradeRecordRepository studentGradeRecordRepository;
    private final StudentGradeRecordMapper studentGradeRecordMapper;

    private final FacultyMapper facultyMapper;
    private final FacultyRepository facultyRepository;
    private final FacultyQueryService facultyQueryService;

    private final GradeConfigurationRepository gradeConfigurationRepository;
    private final GradeConfigurationMapper gradeConfigurationMapper;

    private final StudentQueryService studentQueryService;
    private final ScheduleQueryService scheduleQueryService;
    private final TermQueryService termQueryService;
    private final ScheduledCourseMapper scheduledCourseMapper;

    private StudentGradeRecordEntity getStudentGradeRecordEntity(Long id){
        return studentGradeRecordRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student grade record does not exist"));
    }

    private GradeConfigurationEntity getGradeConfigEntity(Long id){
        return gradeConfigurationRepository.findByFacultyId(id)
        .orElseThrow(() -> new RuntimeException("Grade Configuration not found "));
    }

    public EncodeGradePageDTO getEncodeInfo(Long id, Long facultyId){
        EncodeGradePageDTO page = new EncodeGradePageDTO();

        page.setStudentGradeRecord(getStudentRecord(id));
        page.setGradeConfigurationDTO(getGradeConfig(facultyId));

        return page;
    }
    
    public StudentGradeRecordDTO getStudentRecord(Long id){
        StudentGradeRecordEntity recordEntity = getStudentGradeRecordEntity(id); 
        
        StudentGradeRecordDTO recordDTO = studentGradeRecordMapper.toDto(recordEntity);

        String studentName =  recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getFirstName() + (recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getMiddleName() == null ? "" : (" " + recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getMiddleName())) + (" " + recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getLastName());
        recordDTO.setStudentName(studentName);

        return recordDTO;
    }

    public StudentGradeRecordDTO updateStudentRecord(Long id, StudentGradeRecordDTO studentGradeRecordDTO){
        StudentGradeRecordEntity recordEntity = getStudentGradeRecordEntity(id);
        studentGradeRecordMapper.updateEntityFromDto(studentGradeRecordDTO, recordEntity);
        studentGradeRecordRepository.save(recordEntity);
        return getStudentRecord(id);
    }

    public GradeConfigurationDTO getGradeConfig(Long id){
        return gradeConfigurationMapper.toDto(getGradeConfigEntity(id));
    }

    public GradeConfigurationDTO updateGradeConfig(Long id, GradeConfigurationDTO gradeConfigurationDto){
        GradeConfigurationEntity config = getGradeConfigEntity(id);
        gradeConfigurationMapper.updateEntityFromDto(gradeConfigurationDto, config);
        int total = gradeConfigurationDto.getActivityWeight() + gradeConfigurationDto.getAssignmentWeight() + gradeConfigurationDto.getQuizWeight() + gradeConfigurationDto.getFinalExamWeight();
        if(total != 100){
            throw new RuntimeException("Weights must equal to 100");
        }
        gradeConfigurationRepository.save(config);
        return getGradeConfig(id);
    }

    public StudentGradeRecordDTO computeGrade(Long id, Long facultyId){
        StudentGradeRecordEntity recordEntity = getStudentGradeRecordEntity(id);

        if(
            recordEntity.getAssignments() != null && recordEntity.getQuizzes() != null
            && recordEntity.getActivities() != null && recordEntity.getFinalExam() != null
        ) {
            GradeConfigurationEntity config = getGradeConfigEntity(facultyId);
            double calculatedGrade = 
                (recordEntity.getAssignments() * config.getAssignmentWeight() / 100.0) +
                (recordEntity.getQuizzes() * config.getQuizWeight() / 100.0) +
                (recordEntity.getActivities() * config.getActivityWeight() / 100.0) +
                (recordEntity.getFinalExam() * config.getFinalExamWeight() / 100.0);

            recordEntity.setCalculatedGrade(Math.round(calculatedGrade * 10.0) / 10.0);
            recordEntity.setFinalGrade(getFinalGrade(recordEntity.getCalculatedGrade()));
        }
        else{
            throw new RuntimeException("Cannot compute as fields aren't complete");
        }
        return getStudentRecord(id);
    }

    private double getFinalGrade(double calculatedGrade){
        int raw = (int) Math.round(calculatedGrade);

        if (raw >= 97) return 1.00;
        if (raw >= 94) return 1.25;
        if (raw >= 91) return 1.50;
        if (raw >= 88) return 1.75;

        if (raw >= 85) return 2.00;
        if (raw >= 82) return 2.25;
        if (raw >= 79) return 2.50;
        if (raw >= 76) return 2.75;

        if (raw >= 75) return 3.00;

        return 5.00;
    }

    public FacultyProfileDTO getProfile(Long facultyId){
        FacultyProfileDTO facultyProfileDTO = facultyMapper.toProfileDTO(facultyQueryService.getFacultyEntity(facultyId));
        return facultyProfileDTO;
    }

    public FacultyProfileDTO updateProfile(Long facultyId, FacultyProfileDTO facultyProfileDTO){
        FacultyEntity facultyEntity = facultyQueryService.getFacultyEntity(facultyId);

        facultyEntity.getProfile().setFirstName(facultyProfileDTO.getFirstName());
        facultyEntity.getProfile().setMiddleName(facultyProfileDTO.getMiddleName());
        facultyEntity.getProfile().setLastName(facultyProfileDTO.getLastName());
        facultyEntity.getProfile().setMobileNumber(facultyProfileDTO.getMobileNumber());

        facultyRepository.save(facultyEntity);
        return getProfile(facultyId);
    }

        public List<StudentGradeRecordDTO> getStudentRecords(String name, Long programId, Integer yearLevel, Long courseId, Long sectionId){
        TermEntity term = getTerm();
            List<StudentGradeRecordDTO> records = new ArrayList<>();

        for(StudentGradeRecordEntity recordEntity : studentGradeRecordRepository.searchGradeRecords(name, programId, yearLevel, courseId, sectionId, term.getAcademicYear(), term.getSemester())){
            StudentGradeRecordDTO recordDTO = studentGradeRecordMapper.toDto(recordEntity);
            String studentName =  recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getFirstName() + (recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getMiddleName() == null ? "" : (" " + recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getMiddleName())) + (" " + recordEntity.getEnrolledCourse().getAcademicInfo().getStudent().getProfile().getLastName());
            recordDTO.setStudentName(studentName);
            records.add(recordDTO);
        }
        return records;
    }

    public SchedulesByDayPageDTO getAllSchedulesByDay(Long facultyId){
        SchedulesByDayPageDTO page = new SchedulesByDayPageDTO();

        page.setMonday(getSchedulesWithDay(facultyId, DayName.MONDAY));
        page.setTuesday(getSchedulesWithDay(facultyId, DayName.TUESDAY));
        page.setWednesday(getSchedulesWithDay(facultyId, DayName.WEDNESDAY));
        page.setThursday(getSchedulesWithDay(facultyId, DayName.THURSDAY));
        page.setFriday(getSchedulesWithDay(facultyId, DayName.FRIDAY));
        page.setSaturday(getSchedulesWithDay(facultyId, DayName.SATURDAY));
        page.setSunday(getSchedulesWithDay(facultyId, DayName.SUNDAY));

        return page;
    }

    private List<ScheduledCourseDTO> getSchedulesWithDay(Long facultyId, DayName dayName){
        List<ScheduledCourseDTO> schedules = new ArrayList<>();
        TermEntity term = getTerm();

        for(ScheduledCourseEntity schedule : scheduleQueryService.findTodaysSchedulesByFaculty(facultyId, dayName, term.getAcademicYear(), term.getSemester())){
            schedules.add(scheduledCourseMapper.toDto(schedule));
        }
        return schedules;
    }

    public FacultyDashboardPageDTO getDashboardInfo(Long facultyId){
        TermEntity currentTerm = getTerm();
        FacultyDashboardPageDTO page = new FacultyDashboardPageDTO();
        List<ScheduledCourseDTO> schedules = new ArrayList<>();
        
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        DayName currentDayName = DayName.valueOf(currentDay.name());

        for (ScheduledCourseEntity schedule : scheduleQueryService.findTodaysSchedulesByFaculty(facultyId, currentDayName, currentTerm.getAcademicYear(), currentTerm.getSemester())){
            schedules.add(scheduledCourseMapper.toDto(schedule));
        }

        FacultyDashboardStatsDTO stats = getGlobalStats(facultyId);
        FacultyEntity faculty = facultyQueryService.getFacultyEntity(facultyId);

        String dayOfTheWeekFormatted = currentDay.name().charAt(0) + currentDay.name().substring(1).toLowerCase();

        String prefix = faculty.getProfile().getSex() == Sex.MALE ? "Mr. " : "Ms. ";
        String facultyName = prefix + faculty.getProfile().getFirstName() + (faculty.getProfile().getMiddleName() == null ? "" : (" " + faculty.getProfile().getMiddleName())) + (" " + faculty.getProfile().getLastName());

        page.setSchedules(schedules);
        page.setStats(stats);
        page.setCurrentMonth(LocalDate.now().getMonth().toString());
        page.setCurrentDay(String.valueOf(LocalDate.now().getDayOfMonth()));
        page.setCurrentYear(String.valueOf(LocalDate.now().getYear()));
        page.setCurrentDayOfTheWeek(dayOfTheWeekFormatted);
        page.setFacultyName(facultyName);

        return page;   
    }

    private FacultyDashboardStatsDTO getGlobalStats(Long facultyId){
        TermEntity currentTerm = getTerm();
        FacultyEntity faculty = facultyQueryService.getFacultyEntity(facultyId);
        FacultyDashboardStatsDTO stats = new FacultyDashboardStatsDTO();

        stats.setTotalSchedules(computeTotalSchedules(faculty, currentTerm.getAcademicYear(), currentTerm.getSemester()));
        stats.setTotalStudents(studentQueryService.getTotalStudentsOfFacultyByAcadmicYearAndSemesterAndByEnrollmentStatus(facultyId, currentTerm.getAcademicYear(), currentTerm.getSemester(), EnrollmentStatus.ACTIVE));
        stats.setTotalSections(scheduleQueryService.getTotalSectionByAcademicYearAndSemesterAndFaculty(currentTerm.getAcademicYear(), currentTerm.getSemester(), facultyId));
        stats.setTotalCoursesHandled(scheduleQueryService.getTotalCoursesHandledByAcademicYearAndSemesterAndFaculty(currentTerm.getAcademicYear(), currentTerm.getSemester(), facultyId));
        
        return stats;
    }

    private long computeTotalSchedules(FacultyEntity faculty, String academicYear, int semester){
        long total = 0;
        total += scheduleQueryService.getTotalSchedulesByFacultyAndAcademicYearAndSemester(faculty.getId(), academicYear, semester);
        return total;
    }

    private TermEntity getTerm(){
        long id = 1;
        return termQueryService.getTermEntity(id);
    }
}
