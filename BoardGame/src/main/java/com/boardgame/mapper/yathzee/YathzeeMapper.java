package com.boardgame.mapper.yathzee;

import com.boardgame.dto.yathzee.YathzeeGameDTO;
import com.boardgame.dto.yathzee.YathzeeLobbyDTO;
import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeeLobby;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface YathzeeMapper {

    YathzeeLobbyDTO toYathzeeLobbyDTO(YathzeeLobby lobby);

    YathzeeGameDTO toYathzeeGameDTO(YathzeeGame game);
}
