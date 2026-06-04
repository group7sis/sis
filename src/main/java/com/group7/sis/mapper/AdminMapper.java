package com.group7.sis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.group7.sis.dto.AdminDTO;
import com.group7.sis.entity.AdminEntity;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "id", ignore = true)
    AdminEntity toEntity (AdminDTO adminDTO);

    AdminDTO toDto (AdminEntity adminEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(AdminDTO adminDTO, @MappingTarget AdminEntity adminEntity);
}
