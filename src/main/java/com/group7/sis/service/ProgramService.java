package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.GetProgramDTO;
import com.group7.sis.repository.ProgramRepository;
import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.YearEntity;
import com.group7.sis.mapper.ProgramMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    public ProgramEntity addProgram (ProgramDTO request){
        ProgramEntity program = programMapper.toEntity(request);
        
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
        ProgramEntity program = programRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Program not found."));

        programMapper.updateEntityFromDto(programDTO, program);

        Map<String, CurriculumEntity> oldCurriculums = new HashMap<>();
        for (CurriculumEntity curriculum : program.getCurriculums()){
            oldCurriculums.put(curriculum.getCode(), curriculum);
        }

        List<CurriculumEntity> updatedCurriculums = new ArrayList<>();
        if (programDTO.getCurriculums() != null) {
            for (ProgramDTO.CurriculumDTO cDto : programDTO.getCurriculums()){
                CurriculumEntity curriculumEntity = oldCurriculums.remove(cDto.getCode());
                if (curriculumEntity == null) {
                    curriculumEntity = new CurriculumEntity();
                }

                programMapper.updateEntityFromDto(cDto, curriculumEntity);
                curriculumEntity.setProgram(program);

                Map<Integer, YearEntity> oldYears = new HashMap<>();
                for (YearEntity year : curriculumEntity.getYears()){
                    oldYears.put(year.getLevel(), year);
                }

                curriculumEntity.getYears().clear();
                if (cDto.getYears() != null) {
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

                updatedCurriculums.add(curriculumEntity);
            }
        }

        program.getCurriculums().clear();
        program.getCurriculums().addAll(updatedCurriculums);

        return programMapper.toDto(programRepository.save(program));
    }

    
    public List<GetProgramDTO> getAllPrograms(){
        return programMapper.toGetDtoList(programRepository.findAll());
    }

    public ProgramDTO getProgram(Long id){
        return programMapper.toDto(programRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found.")));
    }
}