package com.group7.sis.service;

import org.springframework.stereotype.Service;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.GetProgramDTO;
import com.group7.sis.repository.ProgramRepository;
import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.mapper.ProgramMapper;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    public ProgramEntity addProgram (ProgramDTO request){
        return programRepository.save(programMapper.toEntity(request));
    }

    public List<GetProgramDTO> getAllPrograms(){
        return programMapper.toDtoList(programRepository.findAll());
    }

}
