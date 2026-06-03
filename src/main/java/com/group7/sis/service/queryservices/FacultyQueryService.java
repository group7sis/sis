package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.users.FacultyEntity;
import com.group7.sis.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacultyQueryService {
    private final FacultyRepository facultyRepository;

    public FacultyEntity getFacultyEntity(Long id){
        return facultyRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Faculty not found"));
    }
}
