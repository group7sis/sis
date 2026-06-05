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

        if(scheduledCourseDTO.getCourseId() != null){
            schedule.setCourse(courseQueryService.getCourseEntity(scheduledCourseDTO.getCourseId()));
        }

        if(scheduledCourseDTO.getSectionId() != null){
        schedule.setSection(sectionQueryService.getSectionEntity(scheduledCourseDTO.getSectionId()));
        }

        if(scheduledCourseDTO.getAssignedFacultyId() != null){
            schedule.setAssignedFaculty(facultyQueryService.getFacultyEntity(scheduledCourseDTO.getAssignedFacultyId()));
        }
        

        if(scheduledCourseDTO.getDays() != null && !scheduledCourseDTO.getDays().isEmpty()){
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

        if(scheduledCourseDTO.getCourseId() != null){
            schedule.setCourse(courseQueryService.getCourseEntity(scheduledCourseDTO.getCourseId()));
        }

        if(scheduledCourseDTO.getSectionId() != null){
        schedule.setSection(sectionQueryService.getSectionEntity(scheduledCourseDTO.getSectionId()));
        }

        if(scheduledCourseDTO.getAssignedFacultyId() != null){
            schedule.setAssignedFaculty(facultyQueryService.getFacultyEntity(scheduledCourseDTO.getAssignedFacultyId()));
        }

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
}
