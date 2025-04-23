package com.boardgame.dto.yathzee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YathzeeGameDTO {
    private Long id;
    private List<YathzeePlayerDTO> players;
    private YathzeePlayerDTO activePlayer;
    private List<Integer> dices;
    private List<YathzeeBonusPreviewDTO> bonusPreviews;
    private int remainingRolls;
    private boolean gameOver;
}
