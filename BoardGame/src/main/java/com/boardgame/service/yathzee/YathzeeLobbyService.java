package com.boardgame.service.yathzee;

import com.boardgame.entity.platform.AppUser;
import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeeLobby;
import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.enums.lobby.LobbyStatus;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.repository.platform.AppUserRepository;
import com.boardgame.repository.yathzee.YathzeeGameRepository;
import com.boardgame.repository.yathzee.YathzeeLobbyRepository;
import com.boardgame.repository.yathzee.YathzeePlayerRepository;
import com.boardgame.utils.yathzee.YathzeeConstants;
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
    public YathzeeLobby createLobby(String name, int maxPlayers) throws LobbyAlreadyExistsException {
        if (lobbyRepository.findByName(name).isPresent()) {
            throw new LobbyAlreadyExistsException("YathzeeLobby with name " + name + " already exists");
        }
        YathzeeLobby lobby = new YathzeeLobby();
        lobby.setName(name);
        lobby.setMaxPlayers(maxPlayers);
        return lobbyRepository.save(lobby);
    }

    @Transactional
    public YathzeeLobby addPlayerToLobby(Long lobbyId, String playerName)
            throws LobbyNotFoundException, LobbyInGameException, PlayerAlreadyInLobbyException, LobbyFullException {
        YathzeeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found."));
        if (lobby.getStatus() != LobbyStatus.WAITING) {
            throw new LobbyInGameException("Cannot join a lobby that is already in-game.");
        }
        if (lobby.getPlayers().stream().anyMatch(player -> player.getUser().getUsername().equals(playerName))) {
            throw new PlayerAlreadyInLobbyException("Player " + playerName + " is already in a lobby.");
        }
        if (lobby.getPlayers().size() == lobby.getMaxPlayers()) {
            throw new LobbyFullException("Lobby is full.");
        }
        AppUser user = appUserRepository.findByUsername(playerName).get();
        YathzeePlayer player = new YathzeePlayer();
        player.setUser(user);
        lobby.addPlayer(player);
        return lobbyRepository.save(lobby);
    }

    @Transactional
    public YathzeeGame startGame(Long lobbyId) throws LobbyNotFoundException,
            GameStartedException, LobbyFullException, NotEnoughPlayerException {
        // TODO faire en sorte que seul le créateur puisse lancer la game
        YathzeeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found."));
        if (lobby.getStatus() != LobbyStatus.WAITING) {
            throw new GameStartedException("Game already started.");
        }
        if (lobby.getPlayers().isEmpty()) {
            throw new NotEnoughPlayerException("Game must have at least one player");
        }
        if (lobby.getPlayers().size() > YathzeeConstants.MAX_PLAYERS) {
            throw new LobbyFullException("Game must have 6 players max");
        }
        lobby.setStatus(LobbyStatus.IN_GAME);
        lobbyRepository.save(lobby);
        YathzeeGame game = new YathzeeGame();
        List<YathzeePlayer> players = new ArrayList<>(lobby.getPlayers());
        game.setPlayers(players);
        game.setDices(new ArrayList<>(YathzeeConstants.NB_DICES));
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
