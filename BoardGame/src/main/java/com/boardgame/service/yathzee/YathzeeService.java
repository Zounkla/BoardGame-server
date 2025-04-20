package com.boardgame.service.yathzee;

import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.repository.yathzee.YathzeeGameRepository;
import com.boardgame.repository.yathzee.YathzeePlayerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class YathzeeService {

    private final YathzeeGameRepository yathzeeGameRepository;
    private final YathzeePlayerRepository yathzeePlayerRepository;

    @Transactional
    public List<Integer> rollDices(long gameId, String username)
            throws YathzeeGameNotFoundException, YathzeeRollsException, YathzeeActivePlayerException, YathzeePlayerNotFoundException {
        YathzeeGame game = yathzeeGameRepository.findById(gameId).orElseThrow(
                () -> new YathzeeGameNotFoundException("Game not found.")
        );
        YathzeePlayer player = yathzeePlayerRepository.findByUser_Username(username).orElseThrow(
                () -> new YathzeePlayerNotFoundException("Player not found.")
        );
        if (game.getActivePlayer() != player) {
            throw new YathzeeActivePlayerException("Not active player.");
        }
        if (game.getRemainingRolls() == 0) {
            throw new YathzeeRollsException("Player can't roll anymore");
        }
        clearOldDices(game);
        game.setRemainingRolls(game.getRemainingRolls() - 1);
        throwDices(game);
        return game.getDices();
    }

    private void clearOldDices(YathzeeGame game) {
        game.getDices().clear();
    }

    private void throwDices(YathzeeGame game) {
        Random random = new Random();
        List<Integer> result = IntStream.range(0, 5)
                .map(i -> random.nextInt(6) + 1)
                .boxed()
                .toList();
        game.setDices(result);
    }
}
