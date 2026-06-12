package com.group7.sis.service;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.SectionDTO;
import com.group7.sis.entity.academic.CurriculumEntity;
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
            validateCurriculumCoverage(sectionDTO.getAcademicYear(), sectionDTO.getProgramId());
            sectionEntity.setProgram(programQueryService.getProgramEntity(sectionDTO.getProgramId()));
        }
        
        sectionEntity.setSequence(sequenceGenerator(sectionDTO));
        sectionEntity.setCode(sectionCodeGenerate(sectionDTO.getProgramId(), sectionEntity));
        return sectionMapper.toDto(sectionRepository.save(sectionEntity));
    }

    public SectionDTO updateSection(Long id, SectionDTO sectionDTO){
        SectionEntity sectionEntity = sectionQueryService.getSectionEntity(id);
        // sectionDTO.setAcademicYear(sectionEntity.getAcademicYear());
        // sectionDTO.setProgramId(sectionEntity.getProgram().getId());
        // sectionDTO.setSemester(sectionEntity.getSemester());
        // sectionDTO.setYearLevel(sectionEntity.getYearLevel());

        if(sectionDTO.getProgramId() != null){
            validateCurriculumCoverage(sectionDTO.getAcademicYear(), sectionDTO.getProgramId());
            sectionEntity.setProgram(programQueryService.getProgramEntity(sectionDTO.getProgramId()));
        }
        sectionMapper.updateEntityFromDto(sectionDTO, sectionEntity);
        sectionEntity.setCode(sectionCodeGenerate(sectionDTO.getProgramId(), sectionEntity));
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

    private String sectionCodeGenerate(Long programId, SectionEntity section){
        String programCode = programQueryService.getProgramEntity(programId).getCode();
        String yearLevel = Integer.toString(section.getYearLevel());
        int seq = section.getSequence();

        String id = programCode + "-" + yearLevel + seq;
        return id;
        
    }

    private int sequenceGenerator(SectionDTO sectionDTO){
        SectionEntity latest = sectionRepository.findTopByProgramIdAndAcademicYearAndSemesterOrderBySequenceDesc(sectionDTO.getProgramId(), sectionDTO.getAcademicYear(), sectionDTO.getSemester());
        return latest == null ? 1 : latest.getSequence() + 1;
    }


    //this is very very very very very very confusing. 
    private void validateCurriculumCoverage(String year, Long programId){
        int startYear = Integer.parseInt(year.substring(0, 4));
        int endYear = Integer.parseInt(year.substring(5, 9));

        int lowestYear = 5000;
        int highestYear = 0;

        for(CurriculumEntity curriculum : programQueryService.getProgramEntity(programId).getCurriculums()){
            int cStartYear = Integer.parseInt(curriculum.getEffectiveYear().substring(0, 4));
            int cEndYear = Integer.parseInt(curriculum.getEffectiveYear().substring(5, 9));

            lowestYear = cStartYear < lowestYear ? cStartYear : lowestYear;
            highestYear = cEndYear > highestYear ? cEndYear : highestYear;
        }

        if(startYear < lowestYear){
            throw new RuntimeException("No curriculum exists before " + lowestYear);
        }

        if(endYear > highestYear){
            throw new RuntimeException("No curriculum exists after " + highestYear);
        }
    }
}