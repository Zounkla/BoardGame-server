package com.boardgame.dto.yathzee;

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
public class YathzeeLobbyDTO {
    private Long id;
    private YathzeeGameDTO game;
    private String name;
    private LobbyStatus status;
    private int maxPlayers;
    private List<YathzeePlayerDTO> players;
}
