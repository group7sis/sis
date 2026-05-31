package com.group7.sis.mapper;

import org.mapstruct.*;
import java.util.List;
import com.group7.sis.dto.ProgramDTO;
import com.group7.sis.dto.GetProgramDTO;
import com.group7.sis.entity.academic.CurriculumEntity;
import com.group7.sis.entity.academic.ProgramEntity;
import com.group7.sis.entity.academic.YearEntity;

@Mapper(componentModel = "spring")
public interface ProgramMapper {
    
    //MainDTO
    List<CurriculumEntity> toCurriculumEntity(List<ProgramDTO.CurriculumDTO> curriculumDTO);
    
    List<YearEntity> toYearEntity(List<ProgramDTO.YearDTO> yearEntity);

    @Mapping(target = "id", ignore = true)
    ProgramEntity toEntity(ProgramDTO programDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "program", ignore = true)
    CurriculumEntity toEntity(ProgramDTO.CurriculumDTO curriculumDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    YearEntity toEntity(ProgramDTO.YearDTO yearDTO);


    //ProgramDTO
    ProgramDTO toDto(ProgramEntity programEntity);


    //GetProgramDTO
    List<GetProgramDTO> toGetDtoList(List<ProgramEntity> programEntityList);


    @Mapping(target = "programId", source = "program.id")
    GetProgramDTO.CurriculumDTO toGetDto(CurriculumEntity curriculumEntity);

    @Mapping(target = "curriculumId", source = "curriculum.id")
    GetProgramDTO.YearDTO toGetDto(YearEntity yearEntity);


    //Update Entities
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculums", ignore = true)
    void updateEntityFromDto(ProgramDTO dto, @MappingTarget ProgramEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "years", ignore = true)
    @Mapping(target = "program", ignore = true)
    void updateEntityFromDto(ProgramDTO.CurriculumDTO dto, @MappingTarget CurriculumEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    void updateEntityFromDto(ProgramDTO.YearDTO dto, @MappingTarget YearEntity entity);
}
