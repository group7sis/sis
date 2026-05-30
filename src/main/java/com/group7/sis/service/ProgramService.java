package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.GetProgramDTO.CurriculumDTO;
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
    public ProgramDTO updateProgram(Long id, ProgramDTO programToModify){
        ProgramEntity program = programRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found."));

        programMapper.updateEntityFromDto(programToModify, program);

        Map<Long, CurriculumEntity> oldCurriculums = new HashMap<>();

        for (CurriculumEntity curriculum : program.getCurriculums()){
            oldCurriculums.put(curriculum.getId(), curriculum);
        }

        List<CurriculumEntity> newCurriculums = new ArrayList<>();
        for (ProgramDTO.CurriculumDTO cDto: programToModify.getCurriculums()){
            CurriculumEntity curriculumEntity = (cDto.getId() !=null && oldCurriculums.containsKey(cDto.getId())) ? oldCurriculums.get(cDto.getId()) : new CurriculumEntity ();
        }
        
        return null;
    }

    
    public List<GetProgramDTO> getAllPrograms(){
        return programMapper.toGetDtoList(programRepository.findAll());
    }

    public ProgramDTO getProgram(Long id){
        return programMapper.toDto(programRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found.")));
    }
}