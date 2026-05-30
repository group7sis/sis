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
    
    //MainDTO
    public abstract List<CurriculumEntity> toCurriculumEntity(List<ProgramDTO.CurriculumDTO> curriculumDTO);
    
    public abstract List<YearEntity> toYearEntity(List<ProgramDTO.YearDTO> yearEntity);

    @Mapping(target = "id", ignore = true)
    public abstract ProgramEntity toEntity(ProgramDTO programDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    protected abstract CurriculumEntity toEntity(ProgramDTO.CurriculumDTO curriculumDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    protected abstract YearEntity toEntity(ProgramDTO.YearDTO yearDTO);


    //ProgramDTO
    public abstract ProgramDTO toDto(ProgramEntity programEntity);


    //GetProgramDTO
    public abstract List<GetProgramDTO> toGetDtoList(List<ProgramEntity> programEntityList);
    
    protected abstract List<GetProgramDTO.CurriculumDTO> toGetCurriculumDto(List<CurriculumEntity> curriculumEntity);
    
    protected abstract List<GetProgramDTO.YearDTO> toGetYearDto(List<YearEntity> yearEntity);


    public abstract GetProgramDTO toGetDto(ProgramEntity programEntity);

    @Mapping(target = "programId", source = "program.id")
    protected abstract GetProgramDTO.CurriculumDTO toGetDto(CurriculumEntity curriculumEntity);

    @Mapping(target = "curriculumId", source = "curriculum.id")
    protected abstract GetProgramDTO.YearDTO toGetDto(YearEntity yearEntity);


    //Update Entities
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculums", ignore = true)
    public abstract void updateEntityFromDto(ProgramDTO dto, @MappingTarget ProgramEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "years", ignore = true)
    @Mapping(target = "program", ignore = true)
    public abstract void updateEntityFromDto(ProgramDTO.CurriculumDTO dto, @MappingTarget CurriculumEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    public abstract void updateEntityFromDto(ProgramDTO.YearDTO dto, @MappingTarget YearEntity entity);
}
