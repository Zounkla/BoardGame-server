package com.boardgame.repository.yathzee;

import com.boardgame.entity.yathzee.YathzeeGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YathzeeGameRepository extends JpaRepository<YathzeeGame, Long> {
}
