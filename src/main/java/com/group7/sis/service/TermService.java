package com.group7.sis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.TermDTO;
import com.group7.sis.entity.academic.TermEntity;
import com.group7.sis.mapper.TermMapper;
import com.group7.sis.repository.TermRepository;
import com.group7.sis.service.queryservices.TermQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TermService {
    private final TermQueryService termQueryService;
    private final TermRepository termRepository;
    private final TermMapper termMapper;

    public TermEntity addTerm(TermDTO termDTO){
        if (termRepository.count() >= 1) {
            throw new RuntimeException("Only one term can be created");
        }
        TermEntity term = termMapper.toEntity(termDTO);
        return termRepository.save(term);
    }

    public TermEntity updateTerm(Long id, TermDTO termDTO){
        TermEntity term = termQueryService.getTermEntity(id);
        termMapper.upateEntityFromDTO(termDTO, term);

        return termRepository.save(term);
    }

    public TermDTO getTerm(Long id){
        return termMapper.toDto(termQueryService.getTermEntity(id));
    }

    public List<TermEntity> getAllTerms(){
        return termRepository.findAll();
    }
}
