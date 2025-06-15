package com.boardgame.service.tictactoe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boardgame.entity.platform.AppUser;
import com.boardgame.entity.tictactoe.TictactoeGame;
import com.boardgame.entity.tictactoe.TictactoeLobby;
import com.boardgame.entity.tictactoe.TictactoePlayer;
import com.boardgame.enums.lobby.LobbyStatus;
import com.boardgame.exceptions.yathzee.GameStartedException;
import com.boardgame.exceptions.lobby.LobbyAlreadyExistsException;
import com.boardgame.exceptions.lobby.LobbyFullException;
import com.boardgame.exceptions.lobby.LobbyInGameException;
import com.boardgame.exceptions.lobby.LobbyNotFoundException;
import com.boardgame.exceptions.lobby.NotEnoughPlayerException;
import com.boardgame.exceptions.lobby.PlayerAlreadyInLobbyException;
import com.boardgame.repository.platform.AppUserRepository;
import com.boardgame.repository.tictactoe.TictactoeGameRepository;
import com.boardgame.repository.tictactoe.TictactoeLobbyRepository;
import com.boardgame.repository.tictactoe.TictactoePlayerRepository;
import com.boardgame.utils.tictactoe.TictactoeConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TictactoeLobbyService {
     
    private final TictactoeLobbyRepository lobbyRepository;
    private final AppUserRepository appUserRepository;
    private final TictactoeGameRepository tictactoeGameRepository;
    private final TictactoePlayerRepository playerRepository;
    
    public TictactoeLobby getLobby(Long lobbyId) throws LobbyNotFoundException {
        return lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found.")
        );
    }

    @Transactional
    public TictactoeLobby createLobby(String name) throws LobbyAlreadyExistsException {
        if (lobbyRepository.findByName(name).isPresent()) {
            throw new LobbyAlreadyExistsException("TictactoeLobby with name " + name + " already exists");
        }
        TictactoeLobby lobby = new TictactoeLobby();
        lobby.setName(name);
        return lobbyRepository.save(lobby);
    }
    
    @Transactional
    public TictactoeLobby addPlayerToLobby(Long lobbyId, String playerName)
            throws LobbyNotFoundException, LobbyInGameException, PlayerAlreadyInLobbyException, LobbyFullException {
        TictactoeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found."));
        if (lobby.getStatus() != LobbyStatus.WAITING) {
            throw new LobbyInGameException("Cannot join a lobby that is already in-game.");
        }
        if (lobby.getPlayers().stream().anyMatch(player -> player.getUser().getUsername().equals(playerName))) {
            throw new PlayerAlreadyInLobbyException("Player " + playerName + " is already in this lobby.");
        }
        if (lobby.getPlayers().size() == lobby.getMaxPlayers()) {
            throw new LobbyFullException("Lobby is full.");
        }
        AppUser user = appUserRepository.findByUsername(playerName).get();
        TictactoePlayer player = new TictactoePlayer();
        player.setUser(user);
        lobby.addPlayer(player);
        return lobbyRepository.save(lobby);
    }
    @Transactional
    public TictactoeGame startGame(Long lobbyId) throws LobbyNotFoundException,
            GameStartedException, LobbyFullException, NotEnoughPlayerException {
        // TODO faire en sorte que seul le créateur puisse lancer la game
        TictactoeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found."));
        if (lobby.getStatus() != LobbyStatus.WAITING) {
            throw new GameStartedException("Game already started.");
        }
        if (lobby.getPlayers().size() < TictactoeConstants.MIN_PLAYERS) {
            throw new NotEnoughPlayerException("Game must have at least 2 players");
        }
        if (lobby.getPlayers().size() > TictactoeConstants.MAX_PLAYERS) {
            throw new LobbyFullException("Game must have 2 players max");
        }
        lobby.setStatus(LobbyStatus.IN_GAME);
        lobbyRepository.save(lobby);
        TictactoeGame game = new TictactoeGame();
        List<TictactoePlayer> players = new ArrayList<>(lobby.getPlayers());
        game.setPlayers(players);
        game.setActivePlayer(lobby.getPlayers().get(0));
        players.forEach(player -> {
            player.setGame(game);
            playerRepository.save(player);
        });
        tictactoeGameRepository.save(game);
        return game;
    }
     public List<TictactoeLobby> getAvailableLobbies() {
        return lobbyRepository.findByStatus(LobbyStatus.WAITING);
    }
}
