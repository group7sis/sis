package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProgramQueryService {
    private final ProgramRepository programRepository;

    public ProgramEntity getProgramEntity(Long id){
        return programRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found"));
    }
}
