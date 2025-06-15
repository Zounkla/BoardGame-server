package com.boardgame.repository.tictactoe;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgame.entity.tictactoe.TictactoePlayer;

public interface TictactoePlayerRepository extends JpaRepository<TictactoePlayer, Long> {
    
    
}
