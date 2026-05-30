package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.GetProgramDTO;
import com.group7.sis.repository.CurriculumRepository;
import com.group7.sis.repository.ProgramRepository;
import com.group7.sis.repository.YearRepository;
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
    private final CurriculumRepository curriculumRepository;
    private final YearRepository yearRepository;
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

        Map<Long, CurriculumEntity> oldCurriculums = new HashMap<>();

        for (CurriculumEntity curriculum : program.getCurriculums()){
            oldCurriculums.put(curriculum.getId(), curriculum);
        }

        List<CurriculumEntity> newCurriculums = new ArrayList<>();
        for (ProgramDTO.CurriculumDTO cDto: programDTO.getCurriculums()){
            CurriculumEntity curriculumEntity = (oldCurriculums.containsKey(curriculumRepository.findByCode(cDto.getCode()).getId())) ? 
            oldCurriculums.get(programRepository.findByCode(cDto.getCode()).getId()) 
            : new CurriculumEntity();

            programMapper.updateEntityFromDto(cDto, curriculumEntity);
            curriculumEntity.setProgram(program);

            Map<Long, YearEntity> oldYears = new HashMap<>();

            for (YearEntity year : curriculumEntity.getYears()){
                oldYears.put(year.getId(), year);
            }

            List<YearEntity> newYears = new ArrayList<>();
            for (ProgramDTO.YearDTO yDto : cDto.getYears()){
                YearEntity yearEntity = (oldYears.containsKey(yearRepository.findByLevel(yDto.getLevel()).getId())) ? 
                oldYears.get(yearRepository.findByLevel(yDto.getLevel()).getId()) 
                : new YearEntity();

                programMapper.updateEntityFromDto(yDto, yearEntity);
                yearEntity.setCurriculum(curriculumEntity);
                newYears.add(yearEntity);
            }
            curriculumEntity.setYears(newYears);
            newCurriculums.add(curriculumEntity);
        }
        program.setCurriculums(newCurriculums);
        
        return programMapper.toDto(programRepository.save(program));
    }

    
    public List<GetProgramDTO> getAllPrograms(){
        return programMapper.toGetDtoList(programRepository.findAll());
    }

    public GetProgramDTO getProgram(Long id){
        return programMapper.toGetDto(programRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found.")));
    }
}