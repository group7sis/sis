package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.ProgramDTO.CurriculumDTO;
import com.group7.sis.dto.ProgramDTO.YearDTO;
import com.group7.sis.dto.CourseDTO;
import com.group7.sis.dto.CourseDTO.CourseAssignmentDTO;
import com.group7.sis.dto.GetProgramDTO;
import com.group7.sis.repository.ProgramRepository;
import com.group7.sis.service.queryservices.CourseQueryService;
import com.group7.sis.service.queryservices.CurriculumQueryService;
import com.group7.sis.service.queryservices.DepartmentQueryService;
import com.group7.sis.service.queryservices.ProgramQueryService;
import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.YearEntity;
import com.group7.sis.entity.academic.CourseAssignmentEntity;
import com.group7.sis.entity.academic.CourseEntity;
import com.group7.sis.entity.academic.CourseEntity.Status;
import com.group7.sis.entity.academic.CurriculumEntity.CurriculumStatus;
import com.group7.sis.entity.academic.ProgramEntity.ProgramStatus;
import com.group7.sis.mapper.CourseMapper;
import com.group7.sis.mapper.ProgramMapper;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProgramService {
    private final ProgramQueryService programQueryService;
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    private final CurriculumQueryService curriculumQueryService;
    private final DepartmentQueryService departmentQueryService;
    private final CourseQueryService courseQueryService;
    private final CourseMapper courseMapper;

    public List<CourseDTO> getCoursesFromProgramCurriculum(Long programId, Long curriculumId, Integer yearLevel, Integer semester){
        List<CourseDTO> courses = new ArrayList<>();
        
        for (CourseEntity course : courseQueryService.getCoursesAssignedFromProgramCurriculum(programId, curriculumId, yearLevel, semester, Status.ACTIVE)){
            List <Long> prereqIds = new ArrayList<>();
            if (course.getPrerequisites() != null){
                for (CourseEntity prereq : course.getPrerequisites()){
                    prereqIds.add(prereq.getId());
                }
            }

            List<CourseAssignmentDTO> courseAssignmentDTOs = new ArrayList<>();
            if(course.getCourseAssignments() != null){
                for(CourseAssignmentEntity courseAssignmentEntity : course.getCourseAssignments()){
                    courseAssignmentDTOs.add(courseMapper.toDto(courseAssignmentEntity));
                }
            }
            CourseDTO courseDto = courseMapper.toDto(course);
            courseDto.setPrerequisiteIds(prereqIds);
            courseDto.setCourseAssignments(courseAssignmentDTOs);
            courses.add(courseDto);
        }

        return courses;
    }

    public ProgramDTO addProgram (ProgramDTO request){
        ProgramEntity program = programMapper.toEntity(request);
        
        program.setDepartment(departmentQueryService.getDepartmentEntity(request.getDepartmentId()));
        
        validateCurriculums(request.getCurriculums());

        if(request.getCurriculums() != null){
            for(CurriculumDTO curriculum : request.getCurriculums()){
                validateCurriculumYears(curriculum);
            }
        }
        

        for (CurriculumEntity curriculum : program.getCurriculums()){
            curriculum.setProgram(program);

            for (YearEntity year : curriculum.getYears()){
                year.setCurriculum(curriculum);
            }

            curriculum.setDurationYear(curriculum.getYears().size());
            curriculum.setStatus(CurriculumStatus.INACTIVE);
            String curCode = curriculumCodeGenerator(program.getCode(), curriculum.getEffectiveYear());
            curriculumQueryService.checkExistenceByCode(curCode, curriculum, program.getCode());
            curriculum.setCode(curCode);
        }

        programRepository.save(program);
        return getProgram(program.getId());
    }

    public ProgramDTO updateProgram(Long id, ProgramDTO programDTO){
        ProgramEntity program = programQueryService.getProgramEntity(id);

        program.setDepartment(departmentQueryService.getDepartmentEntity(programDTO.getDepartmentId()));

        if(programDTO.getActiveCurriculumId() == null){

            if(program.getActiveCurriculum() != null){
                program.getActiveCurriculum().setStatus(CurriculumStatus.INACTIVE);
            }

            program.setActiveCurriculum(null);
        }
        else{
            if(program.getActiveCurriculum() != null){
                program.getActiveCurriculum().setStatus(CurriculumStatus.INACTIVE);
            }

            program.setActiveCurriculum(curriculumQueryService.getCurriculumEntity(programDTO.getActiveCurriculumId()));
            program.getActiveCurriculum().setStatus(CurriculumStatus.ACTIVE);
        }
        
        
        programMapper.updateEntityFromDto(programDTO, program);

        if (programDTO.getCurriculums() != null) {
            validateCurriculums(programDTO.getCurriculums());

            Map<Long, CurriculumEntity> oldCurriculums = new HashMap<>();
            for (CurriculumEntity curriculum : program.getCurriculums()){
                oldCurriculums.put(curriculum.getId(), curriculum);
            }
            program.getCurriculums().clear();

            for (ProgramDTO.CurriculumDTO cDto : programDTO.getCurriculums()){
                CurriculumEntity curriculumEntity = oldCurriculums.remove(cDto.getId());
                if (curriculumEntity == null) {
                    curriculumEntity = new CurriculumEntity();
                    curriculumEntity.setStatus(CurriculumStatus.INACTIVE);
                }

                programMapper.updateEntityFromDto(cDto, curriculumEntity);
                curriculumEntity.setProgram(program);
                curriculumEntity.setCode(curriculumCodeGenerator(program.getCode(), curriculumEntity.getEffectiveYear()));
                
                if (cDto.getYears() != null) {

                    validateCurriculumYears(cDto);

                    Map<Long, YearEntity> oldYears = new HashMap<>();
                    for (YearEntity year : curriculumEntity.getYears()){
                        oldYears.put(year.getId(), year);
                    }
                    curriculumEntity.getYears().clear();

                    for (ProgramDTO.YearDTO yDto : cDto.getYears()){
                    YearEntity yearEntity = oldYears.remove(yDto.getId());
                    if (yearEntity == null) {
                            yearEntity = new YearEntity();
                        }
                        programMapper.updateEntityFromDto(yDto, yearEntity);
                        yearEntity.setCurriculum(curriculumEntity);
                        curriculumEntity.getYears().add(yearEntity);
                    }
                }
                curriculumEntity.setDurationYear(curriculumEntity.getYears().size());
                program.getCurriculums().add(curriculumEntity);
            }
        }
        
        return programMapper.toDto(programRepository.save(program));
    }

    
    public List<GetProgramDTO> getAllPrograms(String title, ProgramStatus status){
        return programMapper.toGetDtoList(programRepository.search(title, status));
    }

    public ProgramDTO getProgram(Long id){
        return programMapper.toDto(programQueryService.getProgramEntity(id));
    }

    public String curriculumCodeGenerator(String programCode, String year){
        return programCode + "-" + year.substring(0, 4);
    }

    private void validateCurriculumYears(CurriculumDTO curriculumDTO){
        if(curriculumDTO.getYears() != null){
            for (int i = 0; i < curriculumDTO.getYears().size(); i++){
                YearDTO current = curriculumDTO.getYears().get(i);

                for(int j = i + 1; j < curriculumDTO.getYears().size(); j++){
                    YearDTO compare = curriculumDTO.getYears().get(j);

                    if(current.getLevel() == compare.getLevel()){
                        throw new RuntimeException("Duplicated year level found");
                    }
                }
            }
        }
    }

    private void validateCurriculums(List<CurriculumDTO> curriculums){
        if (curriculums != null){
            for (int i = 0; i < curriculums.size(); i++){
                CurriculumDTO current = curriculums.get(i);

                for(int j = i + 1; j < curriculums.size(); j++){
                    CurriculumDTO compare = curriculums.get(j);

                    if(current.getEffectiveYear().equals(compare.getEffectiveYear())){
                        throw new RuntimeException("A curriculum for the year '" + current.getEffectiveYear() +"' is already being submitted");
                    }
                }
            }
        }
    }
}