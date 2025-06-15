package com.boardgame.dto.tictactoe;

import com.boardgame.dto.platform.AppUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicTacToePlayerDTO {
    private Long id;
    private AppUserDTO user;
}
