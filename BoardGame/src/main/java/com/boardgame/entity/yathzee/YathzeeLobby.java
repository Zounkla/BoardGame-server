package com.boardgame.entity.yathzee;

import com.boardgame.enums.lobby.LobbyStatus;
import com.boardgame.utils.yathzee.YathzeeConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YathzeeLobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YathzeePlayer> players = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private LobbyStatus status = LobbyStatus.WAITING;

    @Column(nullable = false)
    private int maxPlayers = YathzeeConstants.MAX_PLAYERS;

    @OneToOne
    @JoinColumn(name = "game_id")
    private YathzeeGame game;

    public void addPlayer(YathzeePlayer player) {
        if (players.size() >= maxPlayers) {
            throw new IllegalStateException("Lobby is full.");
        }
        players.add(player);
        player.setLobby(this);
    }

    public void removePlayer(YathzeePlayer player) {
        players.remove(player);
    }
}
