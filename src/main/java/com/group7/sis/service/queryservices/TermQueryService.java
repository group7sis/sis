package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.TermEntity;
import com.group7.sis.repository.TermRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TermQueryService {
    private final TermRepository termRepository;

    public TermEntity getTermEntity(Long id){
        return termRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Term not found."));
    }
}
