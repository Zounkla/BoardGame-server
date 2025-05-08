package com.boardgame.service.lightsout;

import com.boardgame.entity.lightsout.LightsoutGame;
import com.boardgame.entity.lightsout.LightsoutPlayer;
import com.boardgame.entity.platform.AppUser;
import com.boardgame.exceptions.lightsout.*;
import com.boardgame.repository.lightsout.LightsoutGameRepository;
import com.boardgame.repository.lightsout.LightsoutPlayerRepository;
import com.boardgame.repository.platform.AppUserRepository;
import com.boardgame.utils.lightsout.LightsoutConstants;
import com.boardgame.utils.lightsout.LightsoutGridConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class LightsoutService {

    private final LightsoutGameRepository lightsoutGameRepository;
    private final AppUserRepository appUserRepository;
    private final LightsoutPlayerRepository lightsoutPlayerRepository;

    @Transactional
    public LightsoutGame createGame(int size, String username) throws LightsoutInvalidSizeException {
        if (size > LightsoutConstants.MAX_SIZE) throw new LightsoutInvalidSizeException("Size must be less than 256");
        LightsoutGame game = new LightsoutGame();
        AppUser user = appUserRepository.findByUsername(username).get();
        LightsoutPlayer player = new LightsoutPlayer();
        player.setGame(game);
        player.setUser(user);
        game.setPlayer(player);
        boolean[][] grid = createGrid(size);
        game.setGrid(LightsoutGridConverter.fromMatrix(grid));
        lightsoutGameRepository.save(game);
        return game;
    }

    public LightsoutGame getGame(Long id) throws LightsoutGameNotFoundException {
        return lightsoutGameRepository.findById(id)
                .orElseThrow(() -> new LightsoutGameNotFoundException("Game not found."));
    }

    @Transactional
    public LightsoutGame click(Long gameId, String username, int x, int y) throws
            LightsoutGameNotFoundException, LightsoutPlayerNotFoundException,
            LightsoutInvalidIndexException, LightsoutGameOverException {
        LightsoutGame lightsoutGame = getGame(gameId);
        getPlayer(username, lightsoutGame);
        if (lightsoutGame.isGameOver()) throw new LightsoutGameOverException("Game is over");
        boolean[][] grid = LightsoutGridConverter.toMatrix(lightsoutGame.getGrid());
        if (!isValid(grid, x, y)) throw new LightsoutInvalidIndexException("Box not valid");


        boolean[][] newGrid = clickBox(grid, x, y);
        lightsoutGame.setGrid(LightsoutGridConverter.fromMatrix(newGrid));
        checkEndOfGame(lightsoutGame);
        lightsoutGameRepository.save(lightsoutGame);
        return lightsoutGame;
    }

    private boolean[][] createGrid(int size) {
        boolean[][] grid = new boolean[size][size];
        for (int i = 0; i < LightsoutConstants.RANDOM_CLICK; i++) {
            int x = ThreadLocalRandom.current().nextInt(0, size);
            int y = ThreadLocalRandom.current().nextInt(0, size);
            clickBox(grid, x, y);
        }
        return grid;
    }

    private boolean[][] clickBox(boolean[][] grid, int x, int y) {
        int size = grid.length;

        int[][] directions = {
                {0, 0},
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                grid[newX][newY] = !grid[newX][newY];
            }
        }
        return grid;
    }

    private LightsoutPlayer getPlayer(String username, LightsoutGame game)
            throws LightsoutPlayerNotFoundException {
        return lightsoutPlayerRepository
                .findByUser_UsernameAndGame_Id(username, game.getId())
                .orElseThrow(() -> new LightsoutPlayerNotFoundException("Player not found."));
    }

    private boolean isValid(boolean[][] grid, int x, int y) {
        int size = grid.length;
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    private void checkEndOfGame(LightsoutGame game) {
        boolean[][] grid = LightsoutGridConverter.toMatrix(game.getGrid());

        boolean allOn = Arrays.stream(grid)
                .flatMapToInt(row -> IntStream.range(0, row.length).map(i -> row[i] ? 1 : 0))
                .allMatch(v -> v == 1);

        if (allOn) {
            game.setGameOver(true);
        }
    }
}
