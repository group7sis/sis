package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.GetProgramDTO;
import com.group7.sis.repository.ProgramRepository;
import com.group7.sis.service.queryservices.CurriculumQueryService;
import com.group7.sis.service.queryservices.ProgramQueryService;
import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.YearEntity;
import com.group7.sis.mapper.ProgramMapper;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProgramService {
    private final ProgramQueryService programQueryService;
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    private final CurriculumQueryService curriculumQueryService;

    public ProgramEntity addProgram (ProgramDTO request){
        ProgramEntity program = programMapper.toEntity(request);
        
        program.setActiveCurriculum(curriculumQueryService.getCurriculumEntity(request.getActiveCurriculumId()));

        for (CurriculumEntity curriculum : program.getCurriculums()){
            curriculum.setProgram(program);

            for (YearEntity year : curriculum.getYears()){
                year.setCurriculum(curriculum);
            }
        }
        return programRepository.save(program);
    }

    //ANGSEINGAWNGINAWIGNAWIdanf IAWINf iawF IWABFK A
    public ProgramDTO updateProgram(Long id, ProgramDTO programDTO){
        ProgramEntity program = programQueryService.getProgramEntity(id);

        program.setActiveCurriculum(curriculumQueryService.getCurriculumEntity(programDTO.getActiveCurriculumId()));
        programMapper.updateEntityFromDto(programDTO, program);

        if (programDTO.getCurriculums() != null) {

            Map<String, CurriculumEntity> oldCurriculums = new HashMap<>();
            for (CurriculumEntity curriculum : program.getCurriculums()){
                oldCurriculums.put(curriculum.getCode(), curriculum);
            }
            program.getCurriculums().clear();

            for (ProgramDTO.CurriculumDTO cDto : programDTO.getCurriculums()){
                CurriculumEntity curriculumEntity = oldCurriculums.remove(cDto.getCode());
                if (curriculumEntity == null) {
                    curriculumEntity = new CurriculumEntity();
                }

                programMapper.updateEntityFromDto(cDto, curriculumEntity);
                curriculumEntity.setProgram(program);

                if (cDto.getYears() != null) {

                    Map<Integer, YearEntity> oldYears = new HashMap<>();
                    for (YearEntity year : curriculumEntity.getYears()){
                        oldYears.put(year.getLevel(), year);
                    }
                    curriculumEntity.getYears().clear();

                    for (ProgramDTO.YearDTO yDto : cDto.getYears()){
                    YearEntity yearEntity = oldYears.remove(yDto.getLevel());
                    if (yearEntity == null) {
                            yearEntity = new YearEntity();
                        }

                        programMapper.updateEntityFromDto(yDto, yearEntity);
                        yearEntity.setCurriculum(curriculumEntity);
                        curriculumEntity.getYears().add(yearEntity);
                    }
                }

                program.getCurriculums().add(curriculumEntity);
            }
        }
        
        return programMapper.toDto(programRepository.save(program));
    }

    
    public List<GetProgramDTO> getAllPrograms(){
        return programMapper.toGetDtoList(programRepository.findAll());
    }

    public ProgramDTO getProgram(Long id){
        return programMapper.toDto(programQueryService.getProgramEntity(id));
    }
}