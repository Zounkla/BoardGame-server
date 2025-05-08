package com.boardgame.dto.lightsout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LightsoutClickRequestDTO {
    public Long gameId;
    public int x;
    public int y;
}
