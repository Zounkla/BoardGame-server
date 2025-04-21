package com.boardgame.service.yathzee;

import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.entity.yathzee.YathzeePlayerBonus;
import com.boardgame.enums.yathzee.YathzeeBonus;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.repository.yathzee.YathzeeGameRepository;
import com.boardgame.repository.yathzee.YathzeePlayerBonusRepository;
import com.boardgame.repository.yathzee.YathzeePlayerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class YathzeeService {

    private final YathzeeGameRepository yathzeeGameRepository;
    private final YathzeePlayerRepository yathzeePlayerRepository;
    private final YathzeePlayerBonusRepository yathzeePlayerBonusRepository;

    public YathzeeGame getGame(long gameId) throws YathzeeGameNotFoundException {
        return yathzeeGameRepository.findById(gameId).orElseThrow(
                () -> new YathzeeGameNotFoundException("Game not found.")
        );
    }

    @Transactional
    public List<Integer> rollDices(long gameId, String username)
            throws YathzeeGameNotFoundException, YathzeeRollsException, YathzeeActivePlayerException, YathzeePlayerNotFoundException {
        YathzeeGame game = yathzeeGameRepository.findById(gameId).orElseThrow(
                () -> new YathzeeGameNotFoundException("Game not found.")
        );
        YathzeePlayer player = yathzeePlayerRepository.findByUser_UsernameAndGame_Id(username, game.getId())
                .orElseThrow(
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

    @Transactional
    public int chooseBonus(long gameId, String username, int yathzeeBonusIndex)
            throws YathzeeGameNotFoundException, YathzeePlayerNotFoundException, YathzeeActivePlayerException,
            YathzeeBonusIndexException, YathzeeBonusAlreadyChosenException {
        YathzeeGame game = yathzeeGameRepository.findById(gameId).orElseThrow(
                () -> new YathzeeGameNotFoundException("Game not found.")
        );
        YathzeePlayer player = yathzeePlayerRepository.findByUser_UsernameAndGame_Id(username, game.getId())
                .orElseThrow(
                        () -> new YathzeePlayerNotFoundException("Player not found.")
                );
        if (game.getActivePlayer() != player) {
            throw new YathzeeActivePlayerException("Not active player.");
        }
        if (yathzeeBonusIndex < 0 || yathzeeBonusIndex >= YathzeeBonus.values().length) {
            throw new YathzeeBonusIndexException("Invalid bonus index.");
        }
        YathzeeBonus bonus = YathzeeBonus.values()[yathzeeBonusIndex];
        if (yathzeePlayerBonusRepository.existsYathzeePlayerBonusByPlayerAndBonus(player, bonus)) {
            throw new YathzeeBonusAlreadyChosenException("Bonus already taken.");
        }
        YathzeePlayerBonus yathzeePlayerBonus = new YathzeePlayerBonus();
        yathzeePlayerBonus.setPlayer(player);
        yathzeePlayerBonus.setBonus(bonus);
        int score = computePoints(player, bonus);
        player.setScore(player.getScore() + score);
        // TODO Check si somme des 1, 2, ..., 6 >= 63 alors bonus mais qu'une fois (oldSomme < et newSomme >=) (à check)
        // TODO pour Yathzee, si pas première fois, donner 50 en plus ? (à check la vraie règle)

        //TODO GERER FIN DE TOUR
        return score;
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

    private int computePoints(YathzeePlayer player, YathzeeBonus bonus) {
        YathzeeGame yathzeeGame = player.getGame();
        List<Integer> dices = yathzeeGame.getDices();
        switch (bonus) {
            case SUM_OF_ONE -> {
                return dices.stream().filter(dice -> dice == 1).toList().size();
            }
            case SUM_OF_TWO -> {
                return 2 * dices.stream().filter(dice -> dice == 2).toList().size();
            }
            case SUM_OF_THREE -> {
                return 3 * dices.stream().filter(dice -> dice == 3).toList().size();
            }
            case SUM_OF_FOUR -> {
                return 4 * dices.stream().filter(dice -> dice == 4).toList().size();
            }
            case SUM_OF_FIVE -> {
                return 5 * dices.stream().filter(dice -> dice == 5).toList().size();
            }
            case SUM_OF_SIX -> {
                return 6 * dices.stream().filter(dice -> dice == 6).toList().size();
            }
            default -> {
                return 0;
            }
        }
    }
}
