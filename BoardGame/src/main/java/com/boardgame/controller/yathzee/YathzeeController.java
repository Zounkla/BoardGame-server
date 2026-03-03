package com.boardgame.controller.yathzee;

import com.boardgame.dto.yathzee.YathzeeGameDTO;
import com.boardgame.dto.yathzee.YathzeeRollDiceRequestDTO;
import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.mapper.yathzee.YathzeeMapper;
import com.boardgame.service.yathzee.YathzeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/yathzee")
public class YathzeeController {

    private final YathzeeService yathzeeService;
    private final YathzeeMapper yathzeeMapper;

    @GetMapping("/{gameId}")
    public ResponseEntity<YathzeeGameDTO> getGame(@PathVariable long gameId)
            throws YathzeeGameNotFoundException, YathzeePlayerNotFoundException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        YathzeeGame game = yathzeeService.getGame(gameId);
        YathzeeGameDTO dto = yathzeeMapper.toYathzeeGameDTO(game);
        dto.setBonusPreviews(yathzeeService.previewBonusesForUser(game, username));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{gameId}/roll")
    public ResponseEntity<YathzeeGameDTO> rollDices(@PathVariable long gameId,
                                                   @RequestBody YathzeeRollDiceRequestDTO yathzeeRollDiceRequestDTO)
            throws YathzeeActivePlayerException, YathzeePlayerNotFoundException, YathzeeRollsException,
            YathzeeGameNotFoundException, YathzeeGameOverException, YathzeeDiceInvalidIndexesException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        List<Integer> diceIndexes = yathzeeRollDiceRequestDTO == null || isNullOrEmpty(yathzeeRollDiceRequestDTO.getDiceIndexes())
                ? new ArrayList<>()
                : yathzeeRollDiceRequestDTO.getDiceIndexes();
        YathzeeGameDTO gameDTO = yathzeeService.rollDices(gameId, username, diceIndexes);
        YathzeeGame game = yathzeeService.getGame(gameId);
        gameDTO.setBonusPreviews(yathzeeService.previewBonusesForUser(game, username));
        return ResponseEntity.ok(gameDTO);
    }

    @PostMapping("/{gameId}/choose/{bonusIndex}")
    public ResponseEntity<YathzeeGameDTO> chooseBonus(@PathVariable long gameId,
                                                   @PathVariable int bonusIndex)
            throws YathzeePlayerNotFoundException, YathzeeActivePlayerException, YathzeeBonusIndexException,
            YathzeeGameNotFoundException, YathzeeBonusAlreadyChosenException,
            YathzeeDicesNotRolledException, YathzeeGameOverException, YathzeeRollsException, YathzeeDiceInvalidIndexesException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        YathzeeGameDTO gameDTO = yathzeeService.chooseBonus(gameId, username, bonusIndex);
        YathzeeGame game = yathzeeService.getGame(gameId);
        gameDTO.setBonusPreviews(yathzeeService.previewBonusesForUser(game, username));
        return ResponseEntity.ok(gameDTO);
    }

    private boolean isNullOrEmpty(List<Integer> list) {
        return list == null || list.isEmpty();
    }
}
