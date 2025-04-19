package com.boardgame.dto.yathzee;

import com.boardgame.dto.platform.AppUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YathzeePlayerDTO {
    private Long id;
    private int score;
    private AppUserDTO user;
}
