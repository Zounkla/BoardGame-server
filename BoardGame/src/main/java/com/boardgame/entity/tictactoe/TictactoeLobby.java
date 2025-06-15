package com.boardgame.entity.tictactoe;

import java.util.ArrayList;
import java.util.List;

import com.boardgame.enums.lobby.LobbyStatus;
import com.boardgame.utils.tictactoe.TictactoeConstants;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TictactoeLobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TictactoePlayer> players = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private LobbyStatus status = LobbyStatus.WAITING;

    @Column(nullable = false)
    private int maxPlayers = TictactoeConstants.MAX_PLAYERS;

    public void addPlayer(TictactoePlayer player) {
        if (players.size() >= TictactoeConstants.MAX_PLAYERS) {
            throw new IllegalStateException("Lobby is full.");
        }
        players.add(player);
        player.setLobby(this);
    }

    public void removePlayer(TictactoePlayer player) {
        players.remove(player);
    }

}
