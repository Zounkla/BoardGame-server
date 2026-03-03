package com.boardgame.service.yathzee;

import com.boardgame.dto.yathzee.YathzeeBonusPreviewDTO;
import com.boardgame.dto.yathzee.YathzeeGameDTO;
import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeePlayer;
import com.boardgame.entity.yathzee.YathzeePlayerBonus;
import com.boardgame.enums.yathzee.YathzeeBonus;
import com.boardgame.exceptions.yathzee.*;
import com.boardgame.mapper.yathzee.YathzeeMapper;
import com.boardgame.repository.yathzee.YathzeeGameRepository;
import com.boardgame.repository.yathzee.YathzeePlayerBonusRepository;
import com.boardgame.repository.yathzee.YathzeePlayerRepository;
import com.boardgame.utils.yathzee.YathzeeConstants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class YathzeeService {

    private final YathzeeGameRepository yathzeeGameRepository;
    private final YathzeePlayerRepository yathzeePlayerRepository;
    private final YathzeePlayerBonusRepository yathzeePlayerBonusRepository;
    private final YathzeeMapper yathzeeMapper;

    private final Map<Long, Map<String, SseEmitter>> emittersPerGame = new ConcurrentHashMap<>();

    public SseEmitter createSseEmitterForGame(Long gameId, String username) throws YathzeeGameNotFoundException,
            YathzeePlayerNotFoundException {

        YathzeeGame game = getGame(gameId);
        getPlayer(username, game);

        SseEmitter emitter = new SseEmitter(0L);

        emittersPerGame
                .computeIfAbsent(gameId, id -> new ConcurrentHashMap<>())
                .put(username, emitter);

        emitter.onCompletion(() -> removeEmitter(gameId, username));
        emitter.onTimeout(() -> removeEmitter(gameId, username));
        emitter.onError((e) -> removeEmitter(gameId, username));

        return emitter;
    }


    public YathzeeGame getGame(long gameId) throws YathzeeGameNotFoundException {
        return yathzeeGameRepository.findById(gameId)
                .orElseThrow(() -> new YathzeeGameNotFoundException("Game not found."));
    }

    @Transactional
    public YathzeeGameDTO rollDices(long gameId, String username, List<Integer> diceIndexesToRoll)
            throws YathzeeGameNotFoundException, YathzeeRollsException, YathzeeActivePlayerException,
            YathzeePlayerNotFoundException, YathzeeGameOverException, YathzeeDiceInvalidIndexesException {
        YathzeeGame game = getGame(gameId);
        YathzeePlayer player = getPlayer(username, game);

        if (game.isGameOver()) throw new YathzeeGameOverException("Game is over");
        if (!game.getActivePlayer().equals(player)) throw new YathzeeActivePlayerException("Not active player.");
        if (game.getRemainingRolls() == 0) throw new YathzeeRollsException("No rolls left.");
        if (!isValidDiceIndexes(diceIndexesToRoll)) throw new YathzeeDiceInvalidIndexesException("Invalid dice indexes");

        List<Integer> currentDices = game.getDices().isEmpty()
                ? IntStream.range(0, 5).mapToObj(i -> 0).collect(Collectors.toList())
                : new ArrayList<>(game.getDices());

        Random random = new Random();
        for (Integer index : diceIndexesToRoll) {
            currentDices.set(index, random.nextInt(6) + 1);
        }

        game.setRemainingRolls(game.getRemainingRolls() - 1);
        game.setDices(currentDices);
        YathzeeGameDTO dto = yathzeeMapper.toYathzeeGameDTO(game);
        sendGameUpdate(gameId, dto);
        return dto;
    }

    @Transactional
    public YathzeeGameDTO chooseBonus(long gameId, String username, int bonusIndex)
            throws YathzeeGameNotFoundException, YathzeePlayerNotFoundException, YathzeeActivePlayerException,
            YathzeeBonusIndexException, YathzeeBonusAlreadyChosenException,
            YathzeeDicesNotRolledException, YathzeeGameOverException, YathzeeRollsException, YathzeeDiceInvalidIndexesException {

        YathzeeGame game = getGame(gameId);
        YathzeePlayer player = getPlayer(username, game);

        if (game.isGameOver()) throw new YathzeeGameOverException("Game is over");
        if (!game.getActivePlayer().equals(player)) throw new YathzeeActivePlayerException("Not active player.");
        if (bonusIndex < 0 || bonusIndex >= YathzeeBonus.values().length)
            throw new YathzeeBonusIndexException("Invalid bonus index.");
        if (game.getDices().isEmpty()) throw new YathzeeDicesNotRolledException("Roll the dice first");

        YathzeeBonus bonus = YathzeeBonus.values()[bonusIndex];
        if (yathzeePlayerBonusRepository.existsYathzeePlayerBonusByPlayerAndBonus(player, bonus))
            throw new YathzeeBonusAlreadyChosenException("Bonus already taken.");

        int oldSimple = getSumOfSimple(player);
        boolean hadYathzee = player.hasYathzee();
        List<Integer> dices = game.getDices();

        int score = computePointsInternal(dices, bonus);

        if (isSimple(bonus)) {
            int newSimple = oldSimple + score;
            if (oldSimple < YathzeeConstants.SIMPLE_SUM_LIMIT && newSimple >= YathzeeConstants.SIMPLE_SUM_LIMIT) {
                score += YathzeeConstants.SIMPLE_SUM_BONUS;
            }
        }

        if (canYathzee(dices)) {
            if (hadYathzee) {
                score += YathzeeConstants.YATHZEE_BONUS;
            }
            if (bonus == YathzeeBonus.YATHZEE) {
                player.setHasYathzee(true);
            }
        }

        YathzeePlayerBonus bonusEntity = new YathzeePlayerBonus();
        bonusEntity.setPlayer(player);
        bonusEntity.setBonus(bonus);
        bonusEntity.setScore(score);
        player.getBonuses().add(bonusEntity);
        player.setScore(player.getScore() + score);

        changeActivePlayer(game);

        yathzeePlayerRepository.save(player);
        yathzeeGameRepository.save(game);
        YathzeeGameDTO dto = yathzeeMapper.toYathzeeGameDTO(game);

        rollDices(gameId, game.getActivePlayer().getUser().getUsername(),Arrays.asList(1, 2, 3 ,4 ,0));
        sendGameUpdate(gameId, dto);
        return dto;
    }

    public List<YathzeeBonusPreviewDTO> previewBonusesForUser(YathzeeGame game, String username)
            throws YathzeePlayerNotFoundException {
        YathzeePlayer player = getPlayer(username, game);
        List<Integer> dices = game.getDices();
        boolean hasYathzee = player.hasYathzee();
        int simpleBefore = getSumOfSimple(player);
        Set<YathzeeBonus> takenBonuses = player.getBonuses().stream()
                .map(YathzeePlayerBonus::getBonus)
                .collect(Collectors.toSet());

        List<YathzeeBonusPreviewDTO> previews = new ArrayList<>();
        for (YathzeeBonus bonus : YathzeeBonus.values()) {
            YathzeeBonusPreviewDTO preview = new YathzeeBonusPreviewDTO();
            preview.setBonusIndex(bonus.ordinal());
            preview.setBonusName(bonus.name());
            preview.setAlreadyChosen(takenBonuses.contains(bonus));
            int score = 0;

            if (!preview.isAlreadyChosen() && !dices.isEmpty()) {
                score = computePointsInternal(dices, bonus);

                if (isSimple(bonus)) {
                    int newSimple = simpleBefore + score;
                    if (simpleBefore < YathzeeConstants.SIMPLE_SUM_LIMIT && newSimple >= YathzeeConstants.SIMPLE_SUM_LIMIT) {
                        score += YathzeeConstants.SIMPLE_SUM_BONUS;
                    }
                }

                if (canYathzee(dices)) {
                    if (hasYathzee) {
                        score += YathzeeConstants.YATHZEE_BONUS;
                    }
                }
            }

            preview.setPotentialScore(score);
            previews.add(preview);
        }

        return previews;
    }

    public YathzeePlayer getPlayer(String username, YathzeeGame game)
            throws YathzeePlayerNotFoundException {
        return yathzeePlayerRepository
                .findByUser_UsernameAndGame_Id(username, game.getId())
                .orElseThrow(() -> new YathzeePlayerNotFoundException("Player not found."));
    }

    public void sendGameUpdate(Long gameId, Object data) {
        Map<String, SseEmitter> emitters = emittersPerGame.get(gameId);
        if (emitters != null) {
            emitters.forEach((username, emitter) -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("game-update")
                            .data(data));
                } catch (IOException e) {
                    emitter.complete();
                    removeEmitter(gameId, username);
                }
            });
        }
    }

    private int computePointsInternal(List<Integer> dices, YathzeeBonus bonus) {
        return switch (bonus) {
            case SUM_OF_ONE -> computeSimplePoints(dices, 1);
            case SUM_OF_TWO -> computeSimplePoints(dices, 2);
            case SUM_OF_THREE -> computeSimplePoints(dices, 3);
            case SUM_OF_FOUR -> computeSimplePoints(dices, 4);
            case SUM_OF_FIVE -> computeSimplePoints(dices, 5);
            case SUM_OF_SIX -> computeSimplePoints(dices, 6);
            case THREE_OF_KIND -> getNOfKind(dices, 3);
            case FOUR_OF_KIND -> getNOfKind(dices, 4);
            case FULL_HOUSE -> fullHouse(dices);
            case SM_STRAIGHT -> smallStraight(dices);
            case LG_STRAIGHT -> largeStraight(dices);
            case YATHZEE -> getNOfKind(dices, 5);
            case CHANCE -> dices.stream().mapToInt(Integer::intValue).sum();
        };
    }

    private int computeSimplePoints(List<Integer> dices, int number) {
        return (int) dices.stream().filter(d -> d == number).count() * number;
    }

    private int getNOfKind(List<Integer> dices, int n) {
        return dices.stream()
                .filter(d -> Collections.frequency(dices, d) >= n)
                .findFirst()
                .map(d -> d * n)
                .orElse(0);
    }

    private boolean canYathzee(List<Integer> dices) {
        return getNOfKind(dices, 5) != 0;
    }

    private int fullHouse(List<Integer> dices) {
        int three = getNOfKind(dices, 3) / 3;
        if (three == 0) return 0;
        List<Integer> rest = dices.stream().filter(d -> d != three).toList();
        return getNOfKind(rest, 2) > 0 ? 25 : 0;
    }

    private int smallStraight(List<Integer> dices) {
        Set<Integer> set = new HashSet<>(dices);
        return (set.containsAll(List.of(1,2,3,4)) ||
                set.containsAll(List.of(2,3,4,5)) ||
                set.containsAll(List.of(3,4,5,6))) ? 20 : 0;
    }

    private int largeStraight(List<Integer> dices) {
        Set<Integer> set = new HashSet<>(dices);
        return (set.containsAll(List.of(1,2,3,4,5)) ||
                set.containsAll(List.of(2,3,4,5,6))) ? 40 : 0;
    }

    private void changeActivePlayer(YathzeeGame game) {
        List<YathzeePlayer> players = game.getPlayers();
        int index = players.indexOf(game.getActivePlayer());
        YathzeePlayer next = players.get((index + 1) % players.size());
        game.setActivePlayer(next);
        game.setRemainingRolls(YathzeeConstants.MAX_ROLLS);
        game.getDices().clear();
        checkEndOfGame(game);
    }

    private void checkEndOfGame(YathzeeGame game) {
        boolean allFinished = game.getPlayers().stream()
                .allMatch(p -> p.getBonuses().size() == YathzeeBonus.values().length);
        if (allFinished) game.setGameOver(true);
    }

    private int getSumOfSimple(YathzeePlayer player) {
        return player.getBonuses().stream()
                .filter(b -> isSimple(b.getBonus()))
                .mapToInt(YathzeePlayerBonus::getScore)
                .sum();
    }

    private boolean isValidDiceIndexes(List<Integer> indexes) {
        return indexes != null && indexes.stream().allMatch(i -> i >= 0 && i < 5);
    }

    private boolean isSimple(YathzeeBonus bonus) {
        return bonus.ordinal() <= YathzeeBonus.SUM_OF_SIX.ordinal();
    }

    private void removeEmitter(Long gameId, String username) {
        Map<String, SseEmitter> emitters = emittersPerGame.get(gameId);
        if (emitters != null) {
            emitters.remove(username);
            if (emitters.isEmpty()) {
                emittersPerGame.remove(gameId);
            }
        }
    }
}
