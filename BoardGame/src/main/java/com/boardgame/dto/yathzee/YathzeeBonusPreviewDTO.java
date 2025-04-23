package com.boardgame.dto.yathzee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YathzeeBonusPreviewDTO {
    private int bonusIndex;
    private String bonusName;
    private int potentialScore;
    private boolean alreadyChosen;
}

