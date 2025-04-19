package com.boardgame.controller.yathzee;

import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeeLobby;
import com.boardgame.service.yathzee.YathzeeLobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lobbies/yathzee")
@RequiredArgsConstructor
public class YathzeeLobbyController {

    private final YathzeeLobbyService lobbyService;

    @PostMapping("/create")
    public ResponseEntity<YathzeeLobby> createLobby(@RequestParam String name,
                                                    @RequestParam(defaultValue = "6") int maxPlayers) {
        return ResponseEntity.ok(lobbyService.createLobby(name, maxPlayers));
    }

    @PostMapping("/{lobbyId}/join")
    public ResponseEntity<YathzeeLobby> joinLobby(@PathVariable Long lobbyId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return ResponseEntity.ok(lobbyService.addPlayerToLobby(lobbyId, username));
    }

    @PostMapping("/{lobbyId}/start")
    public ResponseEntity<YathzeeGame> startGame(@PathVariable Long lobbyId) {
        return ResponseEntity.ok(lobbyService.startGame(lobbyId));
    }

    @GetMapping
    public ResponseEntity<List<YathzeeLobby>> getAvailableLobbies() {
        return ResponseEntity.ok(lobbyService.getAvailableLobbies());
    }
}
