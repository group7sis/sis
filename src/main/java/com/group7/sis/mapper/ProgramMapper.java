package com.group7.sis.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.GetProgramDTO;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.entity.academic.YearEntity;
import com.group7.sis.repository.ProgramRepository;

@Mapper(componentModel = "spring")
public abstract class ProgramMapper {
    @Autowired
    protected ProgramRepository programRepository;
    //BAKIT HINDI TO NAGSESAVE NG MATINO
    //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    
    //MainDTO
    protected abstract List<CurriculumEntity> toCurriculumDto(List<ProgramDTO.CurriculumDTO> curriculumDTO);
    
    protected abstract List<YearEntity> toYearDto(List<ProgramDTO.YearDTO> yearEntity);

    @Mapping(target = "id", ignore = true)
    public abstract ProgramEntity toEntity(ProgramDTO programDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    protected abstract CurriculumEntity toEntity(ProgramDTO.CurriculumDTO curriculumDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    protected abstract YearEntity toEntity(ProgramDTO.YearDTO yearDTO);



    //GetProgramDTO
    public abstract List<GetProgramDTO> toDtoList(List<ProgramEntity> programEntityList);
    
    protected abstract List<GetProgramDTO.CurriculumDTO> toGetCurriculumDto(List<CurriculumEntity> curriculumEntity);
    
    protected abstract List<GetProgramDTO.YearDTO> toGetYearDto(List<YearEntity> yearEntity);


    public abstract GetProgramDTO toGetDto(ProgramEntity programEntity);

    @Mapping(target = "programId", ignore = true)
    protected abstract GetProgramDTO.CurriculumDTO toGetDto(CurriculumEntity curriculumEntity);

    @Mapping(target = "curriculumId", ignore = true)
    protected abstract GetProgramDTO.YearDTO toGetDto(YearEntity yearEntity);
}
