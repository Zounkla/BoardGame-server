package com.boardgame.dto.platform;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AppUserDTO {

    private String username;
    private Set<String> roles;
}
