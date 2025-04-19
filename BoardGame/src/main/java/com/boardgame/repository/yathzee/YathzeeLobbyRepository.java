package com.boardgame.repository.yathzee;

import com.boardgame.entity.yathzee.YathzeeLobby;
import com.boardgame.enums.lobby.LobbyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YathzeeLobbyRepository  extends JpaRepository<YathzeeLobby, Long> {
    List<YathzeeLobby> findByStatus(LobbyStatus status);
    Optional<YathzeeLobby> findByName(String name);
}
