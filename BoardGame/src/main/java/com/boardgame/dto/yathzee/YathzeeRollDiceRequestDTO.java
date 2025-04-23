package com.boardgame.dto.yathzee;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class YathzeeRollDiceRequestDTO {
    private List<Integer> diceIndexes;
}
