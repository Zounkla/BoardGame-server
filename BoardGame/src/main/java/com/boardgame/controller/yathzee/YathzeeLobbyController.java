package com.boardgame.controller.yathzee;

import com.boardgame.dto.yathzee.YathzeeGameDTO;
import com.boardgame.dto.yathzee.YathzeeLobbyDTO;
import com.boardgame.entity.yathzee.YathzeeLobby;
import com.boardgame.exceptions.lobby.*;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.service.yathzee.YathzeeLobbyService;
import com.boardgame.mapper.yathzee.YathzeeMapper;
import com.boardgame.utils.yathzee.YathzeeConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lobbies/yathzee")
@RequiredArgsConstructor
public class YathzeeLobbyController {

    private final YathzeeLobbyService lobbyService;
    private final YathzeeMapper yathzeeMapper;

    @GetMapping("/{lobbyId}")
    public ResponseEntity<YathzeeLobbyDTO> getLobby(@PathVariable long lobbyId)
            throws LobbyNotFoundException {
        return ResponseEntity.ok(yathzeeMapper.toYathzeeLobbyDTO(lobbyService.getLobby(lobbyId)));
    }

    @PostMapping("/create")
    public ResponseEntity<YathzeeLobbyDTO> createLobby(@RequestParam String name,
                                                       @RequestParam(defaultValue = "" + YathzeeConstants.MAX_PLAYERS) int maxPlayers)
            throws LobbyAlreadyExistsException {
        return ResponseEntity.ok(yathzeeMapper.toYathzeeLobbyDTO(lobbyService.createLobby(name, maxPlayers)));
    }

    @PostMapping("/{lobbyId}/join")
    public ResponseEntity<YathzeeLobbyDTO> joinLobby(@PathVariable Long lobbyId)
            throws PlayerAlreadyInLobbyException, LobbyFullException, LobbyNotFoundException, LobbyInGameException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return ResponseEntity.ok(yathzeeMapper.toYathzeeLobbyDTO(lobbyService.addPlayerToLobby(lobbyId, username)));
    }

    @PostMapping("/{lobbyId}/start")
    public ResponseEntity<YathzeeGameDTO> startGame(@PathVariable Long lobbyId)
            throws LobbyNotFoundException, GameStartedException, NotEnoughPlayerException, LobbyFullException {
        return ResponseEntity.ok(yathzeeMapper.toYathzeeGameDTO(lobbyService.startGame(lobbyId)));
    }

    @GetMapping("/available")
    public ResponseEntity<List<YathzeeLobbyDTO>> getAvailableLobbies() {
        List<YathzeeLobby> lobbies = lobbyService.getAvailableLobbies();
        List<YathzeeLobbyDTO> lobbiesDTO = lobbies.stream()
                .map(yathzeeMapper::toYathzeeLobbyDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lobbiesDTO);
    }
}
