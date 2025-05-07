package com.boardgame.repository.lightsout;

import com.boardgame.entity.lightsout.LightsoutPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LightsoutPlayerRepository extends JpaRepository<LightsoutPlayer, Long> {
    Optional<LightsoutPlayer> findByUser_UsernameAndGame_Id(String username, Long gameId);
}
