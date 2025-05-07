package com.boardgame.dto.lightsout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LightsoutGameDTO {
    private Long id;
    private LightsoutPlayerDTO player;
    private boolean[][] grid;
    private boolean gameOver;
}
