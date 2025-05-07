package com.boardgame.controller.lightsout;

import com.boardgame.dto.lightsout.LightsoutGameDTO;
import com.boardgame.entity.lightsout.LightsoutGame;
import com.boardgame.exceptions.lightsout.LightsoutGameNotFoundException;
import com.boardgame.exceptions.lightsout.LightsoutGameOverException;
import com.boardgame.exceptions.lightsout.LightsoutInvalidIndexException;
import com.boardgame.exceptions.lightsout.LightsoutPlayerNotFoundException;
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
    public ResponseEntity<LightsoutGameDTO> createGame(@RequestParam int size) {
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
    public ResponseEntity<LightsoutGameDTO> click(@RequestParam Long gameId,
                                                  @RequestParam int x,
                                                  @RequestParam int y)
            throws LightsoutGameNotFoundException, LightsoutPlayerNotFoundException,
            LightsoutInvalidIndexException, LightsoutGameOverException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return ResponseEntity.ok(lightsoutMapper.toLightsoutGameDTO(lightsoutService.click(gameId, username, x, y)));
    }
}
