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
    public ResponseEntity<List<Integer>> rollDices(@PathVariable long gameId,
                                                   @RequestBody YathzeeRollDiceRequestDTO yathzeeRollDiceRequestDTO)
            throws YathzeeActivePlayerException, YathzeePlayerNotFoundException, YathzeeRollsException,
            YathzeeGameNotFoundException, YathzeeGameOverException, YathzeeDiceInvalidIndexesException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        List<Integer> diceIndexes = yathzeeRollDiceRequestDTO == null || isNullOrEmpty(yathzeeRollDiceRequestDTO.getDiceIndexes())
                ? new ArrayList<>()
                : yathzeeRollDiceRequestDTO.getDiceIndexes();
        return ResponseEntity.ok(yathzeeService.rollDices(gameId, username, diceIndexes));
    }

    @PostMapping("/{gameId}/choose/{bonusIndex}")
    public ResponseEntity<Integer> chooseBonus(@PathVariable long gameId,
                                                   @PathVariable int bonusIndex)
            throws YathzeePlayerNotFoundException, YathzeeActivePlayerException, YathzeeBonusIndexException,
            YathzeeGameNotFoundException, YathzeeBonusAlreadyChosenException,
            YathzeeDicesNotRolledException, YathzeeGameOverException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        return ResponseEntity.ok(yathzeeService.chooseBonus(gameId, username, bonusIndex));
    }

    private boolean isNullOrEmpty(List<Integer> list) {
        return list == null || list.isEmpty();
    }
}
