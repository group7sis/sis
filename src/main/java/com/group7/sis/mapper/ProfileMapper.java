package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.ProfileDTO;
import com.group7.sis.entity.users.ProfileEntity;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileDTO toDto(ProfileEntity profileEntity);

    @Mapping(target = "id", ignore = true)
    ProfileEntity toEntity(ProfileDTO profileDTO);

     @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ProfileDTO dto, @MappingTarget ProfileEntity entity);
}
