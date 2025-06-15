package com.boardgame.mapper.tictactoe;

import org.mapstruct.Mapper;

import com.boardgame.dto.tictactoe.TictactoeGameDTO;
import com.boardgame.dto.tictactoe.TictactoeLobbyDTO;
import com.boardgame.entity.tictactoe.TictactoeGame;
import com.boardgame.entity.tictactoe.TictactoeLobby;
@Mapper(componentModel = "spring")
public interface TictactoeMapper {
    TictactoeGameDTO toTictactoeGameDTO(TictactoeGame tictactoeGame);
    TictactoeLobbyDTO toTictactoeLobbyDTO(TictactoeLobby lobby);
}