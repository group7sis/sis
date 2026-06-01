package com.group7.sis.service;

import com.group7.sis.repository.CourseRepository;
import com.group7.sis.repository.FacultyRepository;
import com.group7.sis.repository.ProgramRepository;

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
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;
    private final ScheduledCourseMapper scheduledCourseMapper;

    public ScheduledCourseEntity addSchedule(ScheduledCourseDTO scheduledCourseDTO){
        ScheduledCourseEntity schedule = scheduledCourseMapper.toEntity(scheduledCourseDTO);

        schedule.setProgram(programRepository.findById(scheduledCourseDTO.getProgramId()).orElseThrow(() -> new RuntimeException("Program not found")));
        schedule.setCourse(courseRepository.findById(scheduledCourseDTO.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found")));
        schedule.setAssignedFaculty(facultyRepository.findById(scheduledCourseDTO.getAssignedFacultyId()).orElseThrow(() -> new RuntimeException("Faculty not found")));

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
        ScheduledCourseEntity schedule = scheduledCourseRepository.findById(id).orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        schedule.setProgram(programRepository.findById(scheduledCourseDTO.getProgramId()).orElseThrow(() -> new RuntimeException("Program not found")));
        schedule.setCourse(courseRepository.findById(scheduledCourseDTO.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found")));
        schedule.setAssignedFaculty(facultyRepository.findById(scheduledCourseDTO.getAssignedFacultyId()).orElseThrow(() -> new RuntimeException("Faculty not found")));

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
        ScheduledCourseEntity scheduleEntity = scheduledCourseRepository.findById(id).orElseThrow(() -> new RuntimeException("Schedule not found"));
        ScheduledCourseDTO schedule = scheduledCourseMapper.toDto(scheduleEntity);

        schedule.setProgramId(scheduleEntity.getProgram().getId());
        schedule.setCourseId(scheduleEntity.getCourse().getId());
        schedule.setAssignedFacultyId(scheduleEntity.getAssignedFaculty().getId());

        return schedule;
    }

    public List<ScheduledCourseEntity> getAllSchedule(){
        return scheduledCourseRepository.findAll();
    }
}
