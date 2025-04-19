package com.boardgame.mapper.platform;

import com.boardgame.dto.platform.AppUserDTO;
import com.boardgame.entity.platform.AppUser;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUserDTO toDTO(AppUser user) {
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setUsername(user.getUsername());
        appUserDTO.setRoles(user.getRoles());
        return appUserDTO;
    }
}
