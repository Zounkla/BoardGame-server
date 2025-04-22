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

import java.util.*;
import java.util.stream.Collectors;
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
            YathzeeBonusIndexException, YathzeeBonusAlreadyChosenException, YathzeeBonusNotFoundException {
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

    private int computePoints(YathzeePlayer player, YathzeeBonus bonus) throws YathzeeBonusNotFoundException {
        YathzeeGame yathzeeGame = player.getGame();
        List<Integer> dices = yathzeeGame.getDices();
        switch (bonus) {
            case SUM_OF_ONE -> {
                return computeSimplePoints(dices, 1);
            }
            case SUM_OF_TWO -> {
                return computeSimplePoints(dices, 2);
            }
            case SUM_OF_THREE -> {
                return computeSimplePoints(dices, 3);
            }
            case SUM_OF_FOUR -> {
                return computeSimplePoints(dices, 4);
            }
            case SUM_OF_FIVE -> {
                return computeSimplePoints(dices, 5);
            }
            case SUM_OF_SIX -> {
                return computeSimplePoints(dices, 6);
            }
            case THREE_OF_KIND -> {
                return getNOfKind(dices, 3);
            }
            case FOUR_OF_KIND -> {
                return getNOfKind(dices, 4);
            }
            case FULL_HOUSE -> {
                return fullHouse(dices);
            }
            case SM_STRAIGHT ->  {
                return smallStraight(dices);
            }
            case LG_STRAIGHT -> {
                return largeStraight(dices);
            }
            case YATHZEE -> {
                return getNOfKind(dices, 5);
            }
            case CHANCE -> {
                return dices.stream().mapToInt(Integer::intValue).sum();
            }
            default -> throw new YathzeeBonusNotFoundException("This bonus does not exist");
        }
    }

    private int smallStraight(List<Integer> dices) {
            Set<Integer> unique = new HashSet<>(dices);
            return (unique.containsAll(List.of(1, 2, 3, 4)) ||
                    unique.containsAll(List.of(2, 3, 4, 5)) ||
                    unique.containsAll(List.of(3, 4, 5, 6)))
                    ? 20 : 0;
    }

    private int largeStraight(List<Integer> dices) {
        Set<Integer> unique = new HashSet<>(dices);
        return (unique.containsAll(List.of(1, 2, 3, 4, 5)) ||
                unique.containsAll(List.of(2, 3, 4, 5, 6)))
                ? 40 : 0;
    }

    private int computeSimplePoints(List<Integer> dices, int number) {
        return number * dices.stream().filter(dice -> dice == number).toList().size();
    }

    private int getNOfKind(List<Integer> dices, int n) {
        Integer value = dices.stream()
                .filter(d -> Collections.frequency(dices, d) >= n)
                .findFirst()
                .orElse(null);
        return value != null ? value * n : 0;
    }

    private int fullHouse(List<Integer> dices) {
        int threeOfKind = getNOfKind(dices, 3) / 3;
        if (threeOfKind == 0) {
            return 0;
        }

        List<Integer> remaining = dices.stream()
                .filter(d -> !Objects.equals(d, threeOfKind))
                .collect(Collectors.toList());
        return getNOfKind(remaining, 2) > 0 ? 25 : 0;
    }
}
