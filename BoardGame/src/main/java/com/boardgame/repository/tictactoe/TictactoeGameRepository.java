package com.boardgame.repository.tictactoe;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgame.entity.tictactoe.TictactoeGame;

public interface TictactoeGameRepository extends JpaRepository<TictactoeGame, Long>  {
    
}
