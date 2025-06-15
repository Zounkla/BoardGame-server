package com.boardgame.dto.tictactoe;

import com.boardgame.enums.lobby.LobbyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TictactoeLobbyDTO {
    private Long id;
    private String name;
    private LobbyStatus status;
    private int maxPlayers;
    private List<TicTacToePlayerDTO> players;
}
