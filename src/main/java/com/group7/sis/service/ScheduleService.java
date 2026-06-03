package com.group7.sis.service;

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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduledCourseRepository scheduledCourseRepository;
    private final ScheduledCourseMapper scheduledCourseMapper;

    private final ProgramService programService;
    private final CourseService courseService;
    private final FacultyService facultyService;

    public ScheduledCourseEntity addSchedule(ScheduledCourseDTO scheduledCourseDTO){
        ScheduledCourseEntity schedule = scheduledCourseMapper.toEntity(scheduledCourseDTO);

        schedule.setProgram(programService.getProgramEntity(scheduledCourseDTO.getProgramId()));
        schedule.setCourse(courseService.getCourseEntity(scheduledCourseDTO.getCourseId()));
        schedule.setAssignedFaculty(facultyService.getFacultyEntity(scheduledCourseDTO.getAssignedFacultyId()));

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
        ScheduledCourseEntity schedule = getScheduledCourseEntity(id);

        schedule.setProgram(programService.getProgramEntity(scheduledCourseDTO.getProgramId()));
        schedule.setCourse(courseService.getCourseEntity(scheduledCourseDTO.getCourseId()));
        schedule.setAssignedFaculty(facultyService.getFacultyEntity(scheduledCourseDTO.getAssignedFacultyId()));

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
        ScheduledCourseEntity scheduleEntity = getScheduledCourseEntity(id);
        ScheduledCourseDTO schedule = scheduledCourseMapper.toDto(scheduleEntity);

        schedule.setProgramId(scheduleEntity.getProgram().getId());
        schedule.setCourseId(scheduleEntity.getCourse().getId());
        schedule.setAssignedFacultyId(scheduleEntity.getAssignedFaculty().getId());

        return schedule;
    }

    public List<ScheduledCourseEntity> getAllSchedule(){
        return scheduledCourseRepository.findAll();
    }


    //Helper
    public ScheduledCourseEntity getScheduledCourseEntity(Long id){
        return scheduledCourseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }
}
