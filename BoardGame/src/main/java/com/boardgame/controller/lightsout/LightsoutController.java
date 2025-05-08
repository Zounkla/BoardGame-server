package com.boardgame.controller.lightsout;

import com.boardgame.dto.lightsout.LightsoutClickRequestDTO;
import com.boardgame.dto.lightsout.LightsoutGameDTO;
import com.boardgame.entity.lightsout.LightsoutGame;
import com.boardgame.exceptions.lightsout.*;
import com.boardgame.mapper.lightsout.LightsoutMapper;
import com.boardgame.service.lightsout.LightsoutService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lightsout")
@AllArgsConstructor
public class LightsoutController {

    private final LightsoutMapper lightsoutMapper;
    private final LightsoutService lightsoutService;

    @PostMapping("/new")
    public ResponseEntity<LightsoutGameDTO> createGame(@RequestParam int size) throws LightsoutInvalidSizeException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return ResponseEntity.ok(lightsoutMapper.toLightsoutGameDTO(lightsoutService.createGame(size, username)));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<LightsoutGameDTO> getGame(@PathVariable Long gameId) throws LightsoutGameNotFoundException {
        LightsoutGame game = lightsoutService.getGame(gameId);
        LightsoutGameDTO dto = lightsoutMapper.toLightsoutGameDTO(game);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/click")
    public ResponseEntity<LightsoutGameDTO> click(@RequestBody LightsoutClickRequestDTO request)
            throws LightsoutGameNotFoundException, LightsoutPlayerNotFoundException,
            LightsoutInvalidIndexException, LightsoutGameOverException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return ResponseEntity.ok(lightsoutMapper.toLightsoutGameDTO(lightsoutService.click(request.gameId, username,
                request.x, request.y)));
    }
}
