package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.SectionDTO;
import com.group7.sis.entity.academic.SectionEntity;
import com.group7.sis.entity.users.student.AcademicInfoEntity;
import com.group7.sis.mapper.SectionMapper;
import com.group7.sis.repository.SectionRepository;
import com.group7.sis.dto.StudentDTO;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SectionService {
    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    private final ProgramService programService;
    private final StudentService studentService;

    public SectionDTO addSection(SectionDTO sectionDTO){
        SectionEntity sectionEntity = sectionMapper.toEntity(sectionDTO);
        sectionEntity.setProgram(programService.getProgramEntity(sectionDTO.getProgramId()));
        return sectionMapper.toDto(sectionRepository.save(sectionEntity));
    }

    public SectionDTO updateSection(Long id, SectionDTO sectionDTO){
        SectionEntity sectionEntity = getSectionEntity(id);
        sectionMapper.updateEntityFromDto(sectionDTO, sectionEntity);
        sectionEntity.setProgram(programService.getProgramEntity(sectionDTO.getProgramId()));
        return sectionMapper.toDto(sectionRepository.save(sectionEntity));

    }

    public SectionDTO getSection(Long id){
        SectionEntity sectionEntity = getSectionEntity(id);
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
        SectionEntity sectionEntity = getSectionEntity(sectionId);
        List<StudentDTO> students = new ArrayList<>();

        for (AcademicInfoEntity academicInfoEntity : sectionEntity.getStudentAcademicInfos()){
            students.add(studentService.getStudentByAcademicInfoId(academicInfoEntity.getId()));
        }
        return students;
    }


    //Helper
    public SectionEntity getSectionEntity(Long id){
        return sectionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Section not found"));
    }
}
