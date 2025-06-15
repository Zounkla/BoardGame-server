package com.boardgame.controller.tictactoe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.boardgame.dto.tictactoe.TictactoeGameDTO;
import com.boardgame.dto.tictactoe.TictactoeLobbyDTO;
import com.boardgame.entity.tictactoe.TictactoeLobby;
import com.boardgame.exceptions.yathzee.GameStartedException;
import com.boardgame.exceptions.lobby.LobbyAlreadyExistsException;
import com.boardgame.exceptions.lobby.LobbyFullException;
import com.boardgame.exceptions.lobby.LobbyInGameException;
import com.boardgame.exceptions.lobby.LobbyNotFoundException;
import com.boardgame.exceptions.lobby.NotEnoughPlayerException;
import com.boardgame.exceptions.lobby.PlayerAlreadyInLobbyException;
import com.boardgame.mapper.tictactoe.TictactoeMapper;
import com.boardgame.service.tictactoe.TictactoeLobbyService;
import com.boardgame.utils.tictactoe.TictactoeConstants;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lobbies/tictactoe")
@RequiredArgsConstructor
public class TictactoeLobbyController {

    private final TictactoeLobbyService lobbyService;
    private final TictactoeMapper mapper;

    @GetMapping("/{lobbyId}")
    public ResponseEntity<TictactoeLobbyDTO> getLobby(@PathVariable long lobbyId)
            throws LobbyNotFoundException {
        return ResponseEntity.ok(mapper.toTictactoeLobbyDTO(lobbyService.getLobby(lobbyId)));
    }

    @PostMapping("/create")
    public ResponseEntity<TictactoeLobbyDTO> createLobby(@RequestParam String name)
            throws LobbyAlreadyExistsException {
        return ResponseEntity.ok(mapper.toTictactoeLobbyDTO(lobbyService.createLobby(name)));
    }

    @PostMapping("/{lobbyId}/join")
    public ResponseEntity<TictactoeLobbyDTO> joinLobby(@PathVariable Long lobbyId)
            throws PlayerAlreadyInLobbyException, LobbyFullException, LobbyNotFoundException, LobbyInGameException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return ResponseEntity.ok(mapper.toTictactoeLobbyDTO(lobbyService.addPlayerToLobby(lobbyId, username)));
    }

    @PostMapping("/{lobbyId}/start")
    public ResponseEntity<TictactoeGameDTO> startGame(@PathVariable Long lobbyId)
            throws LobbyNotFoundException, GameStartedException, NotEnoughPlayerException, LobbyFullException {
        return ResponseEntity.ok(mapper.toTictactoeGameDTO(lobbyService.startGame(lobbyId)));
    }

    @GetMapping("/available")
    public ResponseEntity<List<TictactoeLobbyDTO>> getAvailableLobbies() {
        List<TictactoeLobby> lobbies = lobbyService.getAvailableLobbies();
        List<TictactoeLobbyDTO> lobbiesDTO = lobbies.stream()
                .map(mapper::toTictactoeLobbyDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lobbiesDTO);
    }
}
