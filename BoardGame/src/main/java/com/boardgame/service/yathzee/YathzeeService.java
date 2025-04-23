package com.boardgame.service.yathzee;

import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.entity.yathzee.YathzeePlayerBonus;
import com.boardgame.enums.yathzee.YathzeeBonus;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.repository.yathzee.YathzeeGameRepository;
import com.boardgame.repository.yathzee.YathzeePlayerBonusRepository;
import com.boardgame.repository.yathzee.YathzeePlayerRepository;
import com.boardgame.utils.yathzee.YathzeeConstants;
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
            throws YathzeeGameNotFoundException, YathzeeRollsException, YathzeeActivePlayerException,
            YathzeePlayerNotFoundException, YathzeeGameOverException {
        YathzeeGame game = getGame(gameId);
        YathzeePlayer player = getPlayer(username, game);
        if (game.isGameOver()) {
            throw new YathzeeGameOverException("Game is over");
        }
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
            YathzeeBonusIndexException, YathzeeBonusAlreadyChosenException, YathzeeBonusNotFoundException, 
            YathzeeDicesNotRolledException, YathzeeGameOverException {
        YathzeeGame game = getGame(gameId);
        YathzeePlayer player = getPlayer(username, game);
        if (game.isGameOver()) {
            throw new YathzeeGameOverException("Game is over");
        }
        if (game.getActivePlayer() != player) {
            throw new YathzeeActivePlayerException("Not active player.");
        }
        if (yathzeeBonusIndex < 0 || yathzeeBonusIndex >= YathzeeBonus.values().length) {
            throw new YathzeeBonusIndexException("Invalid bonus index.");
        }
        if (game.getDices().isEmpty()) {
            throw new YathzeeDicesNotRolledException("Must roll dices before choosing a bonus");
        }
        YathzeeBonus bonus = YathzeeBonus.values()[yathzeeBonusIndex];
        if (yathzeePlayerBonusRepository.existsYathzeePlayerBonusByPlayerAndBonus(player, bonus)) {
            throw new YathzeeBonusAlreadyChosenException("Bonus already taken.");
        }
        YathzeePlayerBonus yathzeePlayerBonus = new YathzeePlayerBonus();
        yathzeePlayerBonus.setPlayer(player);
        yathzeePlayerBonus.setBonus(bonus);
        player.getBonuses().add(yathzeePlayerBonus);
        int oldSimpleScore = getSumOfSimple(player);
        boolean hasYathzee = player.hasYathzee();
        int score = computePoints(player, bonus);
        yathzeePlayerBonus.setScore(score);
        int newSimpleScore = getSumOfSimple(player);
        if (oldSimpleScore < YathzeeConstants.SIMPLE_SUM_LIMIT && newSimpleScore >= YathzeeConstants.SIMPLE_SUM_LIMIT) {
            score += YathzeeConstants.SIMPLE_SUM_BONUS;
        }
        if (canYathzee(game.getDices())) {
            if (hasYathzee) {
                score += YathzeeConstants.YATHZEE_BONUS;
            }
            if (bonus == YathzeeBonus.YATHZEE) {
                player.setHasYathzee(true);
            }
        }
        player.setScore(player.getScore() + score);
        changeActivePlayer(game);
        yathzeePlayerRepository.save(player);
        yathzeeGameRepository.save(game);
        return score;
    }

    private YathzeePlayer getPlayer(String username, YathzeeGame game) throws YathzeePlayerNotFoundException {
        return yathzeePlayerRepository.findByUser_UsernameAndGame_Id(username, game.getId())
                .orElseThrow(
                        () -> new YathzeePlayerNotFoundException("Player not found.")
                );
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

    private boolean canYathzee(List<Integer> dices) {
        return getNOfKind(dices, 5) != 0;
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

    private void changeActivePlayer(YathzeeGame game) {
        List<YathzeePlayer> players = game.getPlayers();
        int index = players.indexOf(game.getActivePlayer());
        int newIndex = (index +  1)% players.size();
        YathzeePlayer newPlayer = players.get(newIndex);
        game.setActivePlayer(newPlayer);
        game.setRemainingRolls(YathzeeConstants.MAX_ROLLS);
        clearOldDices(game);
        checkEndOfGame(game);
    }

    private void checkEndOfGame(YathzeeGame game) {
        if (game.getPlayers().stream().allMatch(this::playerHasFinished)) {
            game.setGameOver(true);
        }
    }

    private boolean playerHasFinished(YathzeePlayer player) {
        return player.getBonuses().size() == YathzeeBonus.values().length;
    }

    private int getSumOfSimple(YathzeePlayer player) {
        List<YathzeePlayerBonus> playerBonuses = player.getBonuses();
        int simpleScoreSum = 0;
        for (YathzeePlayerBonus playerBonus : playerBonuses) {
            switch (playerBonus.getBonus()) {
                case SUM_OF_ONE, SUM_OF_TWO, SUM_OF_THREE, SUM_OF_FOUR, SUM_OF_FIVE, SUM_OF_SIX:
                    simpleScoreSum += playerBonus.getScore();
                    break;
                default:
                    break;
            }
        }
        return simpleScoreSum;
    }
}
