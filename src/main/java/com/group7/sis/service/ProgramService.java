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
import com.group7.sis.entity.academic.CurriculumEntity.CurriculumStatus;
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
        return programRepository.save(program);
    }

    //ANGSEINGAWNGINAWIGNAWIdanf IAWINf iawF IWABFK A
    public ProgramDTO updateProgram(Long id, ProgramDTO programDTO){
        ProgramEntity program = programQueryService.getProgramEntity(id);

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

    
    public List<GetProgramDTO> getAllPrograms(){
        return programMapper.toGetDtoList(programRepository.findAll());
    }

    public ProgramDTO getProgram(Long id){
        return programMapper.toDto(programQueryService.getProgramEntity(id));
    }

    public String curriculumCodeGenerator(String programCode, String year){
        return programCode + "-" + year.substring(0, 4);
    }
}