package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.academic.SectionEntity;
import com.group7.sis.repository.SectionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SectionQueryService {
    private final SectionRepository sectionRepository;

    public SectionEntity getSectionEntity(Long id){
        return sectionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    public long getTotalSections(){
        return sectionRepository.count();
    }
}
