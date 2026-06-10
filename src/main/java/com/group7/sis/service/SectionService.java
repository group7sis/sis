package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.SectionDTO;
import com.group7.sis.entity.academic.SectionEntity;
import com.group7.sis.mapper.SectionMapper;
import com.group7.sis.repository.SectionRepository;
import com.group7.sis.dto.StudentDTO;
import com.group7.sis.service.queryservices.SectionQueryService;
import com.group7.sis.service.queryservices.ProgramQueryService;
import com.group7.sis.service.queryservices.StudentQueryService;
import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SectionService {
    private final SectionQueryService sectionQueryService;
    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    private final ProgramQueryService programQueryService;
    private final StudentQueryService studentQueryService;

    public SectionDTO addSection(SectionDTO sectionDTO){
        SectionEntity sectionEntity = sectionMapper.toEntity(sectionDTO);

        if(sectionDTO.getProgramId() != null){
        sectionEntity.setProgram(programQueryService.getProgramEntity(sectionDTO.getProgramId()));
        }
        return sectionMapper.toDto(sectionRepository.save(sectionEntity));
    }

    public SectionDTO updateSection(Long id, SectionDTO sectionDTO){
        SectionEntity sectionEntity = sectionQueryService.getSectionEntity(id);
        sectionMapper.updateEntityFromDto(sectionDTO, sectionEntity);
        if(sectionDTO.getProgramId() != null){
            sectionEntity.setProgram(programQueryService.getProgramEntity(sectionDTO.getProgramId()));
        }
        return sectionMapper.toDto(sectionRepository.save(sectionEntity));

    }

    public SectionDTO getSection(Long id){
        SectionEntity sectionEntity = sectionQueryService.getSectionEntity(id);
        return sectionMapper.toDto(sectionEntity);
    }

    public List<SectionDTO> getAllSections(){
        List<SectionDTO> sections = new ArrayList<>();

        for (SectionEntity sectionEntity : sectionRepository.findAll()){
            sections.add(sectionMapper.toDto(sectionEntity));
        }
        return sections;
    }

    public List<StudentDTO> getStudentsInSection(Long sectionId){
        sectionQueryService.getSectionEntity(sectionId);
        return studentQueryService.findStudentsFromSectionWithStatus(sectionId, EnrollmentStatus.ACTIVE);
    }
}