package com.boardgame.service.yathzee;

import com.boardgame.entity.platform.AppUser;
import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeeLobby;
import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.enums.lobby.LobbyStatus;
import com.boardgame.repository.platform.AppUserRepository;
import com.boardgame.repository.yathzee.YathzeeGameRepository;
import com.boardgame.repository.yathzee.YathzeeLobbyRepository;
import com.boardgame.repository.yathzee.YathzeePlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YathzeeLobbyService {

    private final YathzeeLobbyRepository lobbyRepository;
    private final YathzeePlayerRepository playerRepository;
    private final AppUserRepository appUserRepository;
    private final YathzeeGameRepository yathzeeGameRepository;

    @Transactional
    public YathzeeLobby createLobby(String name, int maxPlayers) {
        YathzeeLobby lobby = new YathzeeLobby();
        lobby.setName(name);
        lobby.setMaxPlayers(maxPlayers);
        return lobbyRepository.save(lobby);
    }

    @Transactional
    public YathzeeLobby addPlayerToLobby(Long lobbyId, String playerName) {
        YathzeeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found."));
        if (lobby.getStatus() != LobbyStatus.WAITING) {
            throw new IllegalStateException("Cannot join a lobby that is already in-game.");
        }
        AppUser user = appUserRepository.findByUsername(playerName).get();
        YathzeePlayer player = new YathzeePlayer();
        player.setUser(user);
        lobby.addPlayer(player);
        return lobbyRepository.save(lobby);
    }

    @Transactional
    public YathzeeGame startGame(Long lobbyId) {
        YathzeeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found."));
        lobby.setStatus(LobbyStatus.IN_GAME);
        lobbyRepository.save(lobby);
        YathzeeGame game = new YathzeeGame();
        List<YathzeePlayer> players = new ArrayList<>(lobby.getPlayers());
        game.setPlayers(players);
        game.setDices(new ArrayList<>(5));
        game.setActivePlayer(lobby.getPlayers().get(0));
        players.forEach(player -> {
            player.setGame(game);
            playerRepository.save(player);
        });
        yathzeeGameRepository.save(game);
        return game;
    }

    public List<YathzeeLobby> getAvailableLobbies() {
        return lobbyRepository.findByStatus(LobbyStatus.WAITING);
    }
}
