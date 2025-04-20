package com.boardgame.controller.yathzee;

import com.boardgame.exceptions.yathzee.YathzeeActivePlayerException;
import com.boardgame.exceptions.yathzee.YathzeeGameNotFoundException;
import com.boardgame.exceptions.yathzee.YathzeePlayerNotFoundException;
import com.boardgame.exceptions.yathzee.YathzeeRollsException;
import com.boardgame.service.yathzee.YathzeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/yathzee")
public class YathzeeController {

    private final YathzeeService yathzeeService;

    // TODO GET A GAME
    @PostMapping("/{gameId}/roll")
    public ResponseEntity<List<Integer>> rollDices(@PathVariable long gameId)
            throws YathzeeActivePlayerException, YathzeePlayerNotFoundException, YathzeeRollsException, YathzeeGameNotFoundException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        List<Integer> result = yathzeeService.rollDices(gameId, username);
        return ResponseEntity.ok(result);
    }

    // TODO CHOOSE BONUS
}
