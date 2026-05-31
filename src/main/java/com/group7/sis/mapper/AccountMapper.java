package com.group7.sis.mapper;
import org.mapstruct.*;

import com.group7.sis.dto.AccountDTO;
import com.group7.sis.entity.users.AccountEntity;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDTO toDto(AccountEntity accountEntity);

    @Mapping(target = "id", ignore = true)
    AccountEntity toEntity(AccountDTO accountDTO);

     @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(AccountDTO dto, @MappingTarget AccountEntity entity);
}