package com.boardgame.mapper.platform;

import com.boardgame.dto.platform.AppUserDTO;
import com.boardgame.entity.platform.AppUser;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserDTO toDTO(AppUser user);
}