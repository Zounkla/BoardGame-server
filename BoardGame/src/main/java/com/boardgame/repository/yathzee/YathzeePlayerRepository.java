package com.boardgame.repository.yathzee;

import com.boardgame.entity.yathzee.YathzeePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YathzeePlayerRepository extends JpaRepository<YathzeePlayer, Long> {

    Optional<YathzeePlayer> findByUser_UsernameAndGame_Id(String username, Long gameId);
}
