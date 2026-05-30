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
    public GetProgramDTO updateProgram(Long id, ProgramDTO programToModify){
        ProgramEntity program = programRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found."));

        programMapper.updateEntityFromDto(programToModify, program);

        List<CurriculumEntity> curriculumContainer = new ArrayList<>();

        //program.setCurriculums(programMapper.toCurriculumEntity(programToModify.getCurriculums()));

        
        // List <CurriculumEntity> curriculums = program.getCurriculums();

        // for (ProgramDTO.CurriculumDTO cDto : programToModify.getCurriculums()){

        // }
        //ye this is confusing god help me

            // for (YearEntity year : curriculum.getYears()){
            //     year.setCurriculum(curriculum);
            // }
        
        return programMapper.toGetDto(programRepository.save(program));
    }
    
    public List<GetProgramDTO> getAllPrograms(){
        return programMapper.toGetDtoList(programRepository.findAll());
    }

    public ProgramDTO getProgram(Long id){
        return programMapper.toDto(programRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found.")));
    }

}
