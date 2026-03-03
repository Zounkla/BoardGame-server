package com.boardgame.service.yathzee;

import com.boardgame.dto.yathzee.YathzeeLobbyDTO;
import com.boardgame.entity.platform.AppUser;
import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeeLobby;
import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.enums.lobby.LobbyStatus;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.mapper.yathzee.YathzeeMapper;
import com.boardgame.repository.platform.AppUserRepository;
import com.boardgame.repository.yathzee.YathzeeGameRepository;
import com.boardgame.repository.yathzee.YathzeeLobbyRepository;
import com.boardgame.repository.yathzee.YathzeePlayerRepository;
import com.boardgame.utils.yathzee.YathzeeConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class YathzeeLobbyService {

    private final YathzeeLobbyRepository lobbyRepository;
    private final YathzeePlayerRepository playerRepository;
    private final AppUserRepository appUserRepository;
    private final YathzeeGameRepository yathzeeGameRepository;
    private final Map<Long, Map<String, SseEmitter>> lobbyEmitters = new ConcurrentHashMap<>();
    private final YathzeeMapper yathzeeMapper;
    private final YathzeeService  yathzeeService;

    public YathzeeLobby getLobby(Long lobbyId) throws LobbyNotFoundException {
        return lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found.")
        );
    }

    public SseEmitter createLobbySseEmitter(Long lobbyId, String username) {
        SseEmitter emitter = new SseEmitter(0L);

        lobbyEmitters
                .computeIfAbsent(lobbyId, id -> new ConcurrentHashMap<>())
                .put(username, emitter);

        emitter.onCompletion(() -> removeLobbyEmitter(lobbyId, username));
        emitter.onTimeout(() -> removeLobbyEmitter(lobbyId, username));
        emitter.onError(e -> removeLobbyEmitter(lobbyId, username));

        return emitter;
    }

    public void sendLobbyUpdate(Long lobbyId, YathzeeLobbyDTO lobbyDto) {
        Map<String, SseEmitter> emitters = lobbyEmitters.get(lobbyId);
        if (emitters != null) {
            emitters.forEach((username, emitter) -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("lobby-update")
                            .data(lobbyDto));
                } catch (IOException e) {
                    emitter.complete();
                    removeLobbyEmitter(lobbyId, username);
                }
            });
        }
    }

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
            throws LobbyNotFoundException, LobbyInGameException, PlayerAlreadyInLobbyException, LobbyFullException, YathzeePlayerNotFoundException {
        YathzeeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found."));

        if (lobby.getStatus() != LobbyStatus.WAITING) throw new LobbyInGameException("Cannot join a lobby in game.");
        if (lobby.getPlayers().stream().anyMatch(p -> p.getUser().getUsername().equals(playerName)))
            throw new PlayerAlreadyInLobbyException("Player already in lobby.");
        if (lobby.getPlayers().size() >= lobby.getMaxPlayers())
            throw new LobbyFullException("Lobby is full.");

        AppUser user = appUserRepository.findByUsername(playerName)
                .orElseThrow(() -> new YathzeePlayerNotFoundException("User not found."));
        YathzeePlayer player = new YathzeePlayer();
        player.setUser(user);
        lobby.addPlayer(player);

        lobbyRepository.save(lobby);
        YathzeeLobbyDTO lobbyDto = yathzeeMapper.toYathzeeLobbyDTO(lobby);
        sendLobbyUpdate(lobbyId, lobbyDto);

        return lobby;
    }

    @Transactional
    public YathzeeLobby startGame(Long lobbyId)
            throws LobbyNotFoundException, GameStartedException, LobbyFullException, NotEnoughPlayerException, YathzeeGameOverException, YathzeeActivePlayerException, YathzeePlayerNotFoundException, YathzeeRollsException, YathzeeDiceInvalidIndexesException, YathzeeGameNotFoundException {

        YathzeeLobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found."));

        if (lobby.getStatus() != LobbyStatus.WAITING) throw new GameStartedException("Game already started.");
        if (lobby.getPlayers().isEmpty()) throw new NotEnoughPlayerException("Need at least 1 player.");
        if (lobby.getPlayers().size() > YathzeeConstants.MAX_PLAYERS) throw new LobbyFullException("Too many players.");

        lobby.setStatus(LobbyStatus.IN_GAME);
        lobbyRepository.save(lobby);

        YathzeeGame game = new YathzeeGame();
        game.setPlayers(new ArrayList<>(lobby.getPlayers()));
        game.setActivePlayer(lobby.getPlayers().get(0));
        game.setDices(new ArrayList<>(YathzeeConstants.NB_DICES));
        yathzeeGameRepository.save(game);

        lobby.getPlayers().forEach(p -> {
            p.setGame(game);
            playerRepository.save(p);
        });
        lobby.setGame(game);
        YathzeeLobbyDTO lobbyDto = yathzeeMapper.toYathzeeLobbyDTO(lobby);
        sendLobbyUpdate(lobbyId, lobbyDto);
        yathzeeService.rollDices(game.getId(), game.getActivePlayer().getUser().getUsername(), Arrays.asList(1, 2, 3, 4, 0));
        return lobby;
    }

    public List<YathzeeLobby> getAvailableLobbies() {
        return lobbyRepository.findByStatus(LobbyStatus.WAITING);
    }

    private void removeLobbyEmitter(Long lobbyId, String username) {
        Map<String, SseEmitter> emitters = lobbyEmitters.get(lobbyId);
        if (emitters != null) {
            emitters.remove(username);
            if (emitters.isEmpty()) {
                lobbyEmitters.remove(lobbyId);
            }
        }
    }
}
