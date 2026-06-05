package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.repository.CurriculumRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CurriculumQueryService {
    private final CurriculumRepository curriculumRepository;

    public CurriculumEntity getCurriculumEntity(Long id){
        return curriculumRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Curriculum not found."));
    }
}
