package com.group7.sis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.dto.ScheduledCourseDTO.DayDTO;
import com.group7.sis.entity.academic.DayEntity;
import com.group7.sis.entity.academic.ScheduledCourseEntity;
import com.group7.sis.mapper.ScheduledCourseMapper;
import com.group7.sis.repository.ScheduledCourseRepository;
import com.group7.sis.service.queryservices.CourseQueryService;
import com.group7.sis.service.queryservices.ScheduleQueryService;
import com.group7.sis.service.queryservices.SectionQueryService;
import com.group7.sis.service.queryservices.FacultyQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleQueryService scheduleQueryService;
    private final ScheduledCourseRepository scheduledCourseRepository;
    private final ScheduledCourseMapper scheduledCourseMapper;

    private final CourseQueryService courseQueryService;
    private final FacultyQueryService facultyQueryService;
    private final SectionQueryService sectionQueryService;

    public ScheduledCourseEntity addSchedule(ScheduledCourseDTO scheduledCourseDTO){
        ScheduledCourseEntity schedule = scheduledCourseMapper.toEntity(scheduledCourseDTO);

        if(scheduledCourseDTO.getCourseAssignmentId() != null){
            schedule.setCourseAssignment(courseQueryService.getCourseAssignmentEntity(scheduledCourseDTO.getCourseAssignmentId()));
        }

        if(scheduledCourseDTO.getSectionId() != null){
            validateSection(scheduledCourseDTO.getSectionId(), scheduledCourseDTO.getCourseAssignmentId());
            schedule.setSection(sectionQueryService.getSectionEntity(scheduledCourseDTO.getSectionId()));
        }

        checkForScheduleDuplicates(scheduledCourseDTO.getCourseAssignmentId(), scheduledCourseDTO.getSectionId(), scheduledCourseDTO.getAcademicYear(), schedule);

        if(scheduledCourseDTO.getAssignedFacultyId() != null){
            validateFaculty(scheduledCourseDTO.getAssignedFacultyId(), courseQueryService.getCourseAssignmentEntity(scheduledCourseDTO.getCourseAssignmentId()).getCourse().getId());
            schedule.setAssignedFaculty(facultyQueryService.getFacultyEntity(scheduledCourseDTO.getAssignedFacultyId()));
        }
        
        schedule.setCode(scheduleCodeGenerator(scheduledCourseDTO.getAcademicYear()));
        
        checkExistingScheduleOverlap(scheduledCourseDTO, schedule);
        if(scheduledCourseDTO.getDays() != null){
            checkSubmittedDaysOverlap(scheduledCourseDTO);

            for (DayDTO dayDTO : scheduledCourseDTO.getDays()){
                DayEntity dayEntity = scheduledCourseMapper.toEntity(dayDTO);
                dayEntity.setScheduledCourse(schedule);
                schedule.getDays().add(dayEntity);
            }
        }
        
        return scheduledCourseRepository.save(schedule);
    }

    //im tired boss
    public ScheduledCourseDTO updateSchedule(Long id, ScheduledCourseDTO scheduledCourseDTO){
        ScheduledCourseEntity schedule = scheduleQueryService.getScheduledCourseEntity(id);
        scheduledCourseMapper.updateEntityFromDto(scheduledCourseDTO, schedule);
        if(scheduledCourseDTO.getCourseAssignmentId() != null){
            schedule.setCourseAssignment(courseQueryService.getCourseAssignmentEntity(scheduledCourseDTO.getCourseAssignmentId()));
        }

        if(scheduledCourseDTO.getSectionId() != null){
            validateSection(scheduledCourseDTO.getSectionId(), scheduledCourseDTO.getCourseAssignmentId());
            schedule.setSection(sectionQueryService.getSectionEntity(scheduledCourseDTO.getSectionId()));
        }

        checkForScheduleDuplicates(scheduledCourseDTO.getCourseAssignmentId(), scheduledCourseDTO.getSectionId(), scheduledCourseDTO.getAcademicYear(), schedule);

        if(scheduledCourseDTO.getAssignedFacultyId() != null){
            validateFaculty(scheduledCourseDTO.getAssignedFacultyId(), courseQueryService.getCourseAssignmentEntity(scheduledCourseDTO.getCourseAssignmentId()).getCourse().getId());
            schedule.setAssignedFaculty(facultyQueryService.getFacultyEntity(scheduledCourseDTO.getAssignedFacultyId()));
        }

        schedule.setCode(scheduleCodeGenerator(scheduledCourseDTO.getAcademicYear()));
        checkExistingScheduleOverlap(scheduledCourseDTO, schedule);
        if (scheduledCourseDTO.getDays() != null){
            Map<Long, DayEntity> oldDays = new HashMap<>();
            for (DayEntity day : schedule.getDays()){
                oldDays.put(day.getId(), day);
            }
            schedule.getDays().clear();
            for (DayDTO dayDTO : scheduledCourseDTO.getDays()){
                DayEntity dayEntity = oldDays.remove(dayDTO.getId());
                if (dayEntity == null){
                    dayEntity = new DayEntity();
                }
                scheduledCourseMapper.updateEntityFromDto(dayDTO, dayEntity);
                dayEntity.setScheduledCourse(schedule);
                schedule.getDays().add(dayEntity);
            }
        }
        return scheduledCourseMapper.toDto(scheduledCourseRepository.save(schedule));
    }

    public ScheduledCourseDTO getSchedule(Long id){
        ScheduledCourseEntity scheduleEntity = scheduleQueryService.getScheduledCourseEntity(id);
        ScheduledCourseDTO schedule = scheduledCourseMapper.toDto(scheduleEntity);

        return schedule;
    }

    public List<ScheduledCourseDTO> getAllSchedule(){
        List<ScheduledCourseDTO> schedules = new ArrayList<>();

        for (ScheduledCourseEntity schedule : scheduledCourseRepository.findAll()){
            schedules.add(scheduledCourseMapper.toDto(schedule));
        }
        return schedules;
    }

    private void validateFaculty(Long facultyId, Long courseId){
        if (!facultyQueryService.getFacultyEntity(facultyId)
            .getQualifiedCourses().contains(
            courseQueryService.getCourseEntity(courseId))){
            
            throw new RuntimeException("Faculty member isn't qualified to teach this course");
        }
    }

    private void validateSection(Long sectionId, Long courseAssignmentId){
        if (sectionQueryService.getSectionEntity(sectionId).getProgram().getId() != courseQueryService.getCourseAssignmentEntity(courseAssignmentId).getCurriculum().getProgram().getId()){
            throw new RuntimeException("Cannot assign a section with Program that doesn't own the Course Assignment");
        }
    }

    private String scheduleCodeGenerator(String academicYear){
        ScheduledCourseEntity latest = scheduledCourseRepository.findTopByAcademicYearOrderByCodeDesc(academicYear);
        int seq;
        if (latest == null){
            seq = 1;
        }else{
            String ref = latest.getCode().substring(latest.getCode().lastIndexOf("-")+1);
            seq = Integer.parseInt(ref) + 1;
        }
        String startYear = academicYear.substring(0, 4);

        String id = "SCH-" + startYear + "-" + String.format("%05d", seq);
        return id;
    }

    private void checkForScheduleDuplicates(Long courseAssignmentId, Long sectionId, String academicYear, ScheduledCourseEntity schedule){
        ScheduledCourseEntity existingSchedule = scheduledCourseRepository.findByCourseAssignmentIdAndSectionIdAndAcademicYear(courseAssignmentId, sectionId, academicYear);

        if(existingSchedule != null && existingSchedule.getId() != schedule.getId()){
            throw new RuntimeException("Schedule with course assignment '" + courseAssignmentId + "', section '" + sectionId + "', and year '" + academicYear + "' already exists");
        }        
    }

    private void checkExistingScheduleOverlap(ScheduledCourseDTO scheduledCourseDTO, ScheduledCourseEntity newSchedule){
        checkDayOverlapByFaculty(scheduledCourseDTO, newSchedule);
        checkDayOverlapByRoom(scheduledCourseDTO, newSchedule);
        checkDayOverlapBySection(scheduledCourseDTO, newSchedule);
    }

    private void checkDayOverlapByFaculty(ScheduledCourseDTO scheduledCourseDTO, ScheduledCourseEntity newSchedule){
        checkDayOverlap(scheduledCourseRepository.findByAssignedFacultyIdAndAcademicYearAndCourseAssignmentId(scheduledCourseDTO.getAssignedFacultyId(), scheduledCourseDTO.getAcademicYear(), scheduledCourseDTO.getCourseAssignmentId()), scheduledCourseDTO, newSchedule);
    }

    private void checkDayOverlapBySection(ScheduledCourseDTO scheduledCourseDTO, ScheduledCourseEntity newSchedule){
        checkDayOverlap(scheduledCourseRepository.findBySectionIdAndAcademicYearAndCourseAssignmentId(scheduledCourseDTO.getSectionId(), scheduledCourseDTO.getAcademicYear(), scheduledCourseDTO.getCourseAssignmentId()), scheduledCourseDTO, newSchedule);
    }

    private void checkDayOverlapByRoom(ScheduledCourseDTO scheduledCourseDTO, ScheduledCourseEntity newSchedule){
        checkDayOverlap(scheduledCourseRepository.findByRoomAndAcademicYearAndCourseAssignmentId(scheduledCourseDTO.getRoom(), scheduledCourseDTO.getAcademicYear(), scheduledCourseDTO.getCourseAssignmentId()), scheduledCourseDTO, newSchedule);
    }

    private void checkDayOverlap(List<ScheduledCourseEntity> existingSchedules, ScheduledCourseDTO scheduledCourseDTO, ScheduledCourseEntity newSchedule){
        
        for (ScheduledCourseEntity schedule : existingSchedules){
            if (schedule.getId() != newSchedule.getId()){
                validateDay(scheduledCourseDTO.getDays(), schedule);
            }
        }
    }

    private void validateDay(List<DayDTO> dayDtos,  ScheduledCourseEntity schedule ){
        for (DayDTO day : dayDtos){
            for (DayEntity day2: schedule.getDays()){
                if(
                    day.getName().equals(day2.getName()) &&
                    day.getStartTime().isBefore(day2.getEndTime()) &&
                    day.getEndTime().isAfter(day2.getStartTime())
                ){
                    throw new RuntimeException("""
                            Schedule conflict detected.

                            Conflicted with schedule:
                            Code: %s
                            AcademicYear: %s
                            AssignedFaculty: %s
                            Section: %s
                            Room: %s
                            Day: %s
                            Time: %s - %s
                            """.formatted(
                                schedule.getCode(),
                                schedule.getAcademicYear(),
                                schedule.getAssignedFaculty().getId(),
                                schedule.getSection().getId(),
                                schedule.getRoom(),
                                day2.getName(),
                                day2.getStartTime(), day2.getEndTime()
                            ));
                }
            }
        }
    }

    private void checkSubmittedDaysOverlap(ScheduledCourseDTO schedule){
        List<DayDTO> scheduleDays = schedule.getDays();

        if (scheduleDays != null){
            for (int i = 0; i < scheduleDays.size(); i++){
                DayDTO current = scheduleDays.get(i);

                for(int j = i + 1; j < scheduleDays.size(); j++){
                    DayDTO compare = scheduleDays.get(j);

                    if (current.getName().equals(compare.getName()) &&
                        current.getStartTime().isBefore(compare.getEndTime()) &&
                        current.getEndTime().isAfter(compare.getStartTime())
                    ){
                        throw new RuntimeException("Conflicting schedule being submitted on day '" + current.getName() + "'");
                    }
                }
            }
        }
    }
}
