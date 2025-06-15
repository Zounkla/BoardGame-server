package com.boardgame.repository.tictactoe;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgame.entity.tictactoe.TictactoeLobby;
import com.boardgame.enums.lobby.LobbyStatus;

public interface TictactoeLobbyRepository extends JpaRepository<TictactoeLobby, Long>{
    Optional<TictactoeLobby> findByName(String name);
    List<TictactoeLobby> findByStatus(LobbyStatus status);

}
