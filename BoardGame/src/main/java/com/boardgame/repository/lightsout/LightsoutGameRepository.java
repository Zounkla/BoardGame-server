package com.boardgame.repository.lightsout;

import com.boardgame.entity.lightsout.LightsoutGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LightsoutGameRepository extends JpaRepository<LightsoutGame, Long> {
}
