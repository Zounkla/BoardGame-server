package com.boardgame.dto.yathzee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YathzeePlayerBonusDTO {
    private int score;
    private String bonusName;
    private String playerName;
}