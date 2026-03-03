package com.boardgame.mapper.yathzee;

import com.boardgame.dto.yathzee.YathzeeGameDTO;
import com.boardgame.dto.yathzee.YathzeeLobbyDTO;
import com.boardgame.dto.yathzee.YathzeePlayerBonusDTO;
import com.boardgame.entity.yathzee.YathzeeGame;
import com.boardgame.entity.yathzee.YathzeeLobby;
import com.boardgame.entity.yathzee.YathzeePlayerBonus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface YathzeeMapper {

    YathzeeLobbyDTO toYathzeeLobbyDTO(YathzeeLobby lobby);

    YathzeeGameDTO toYathzeeGameDTO(YathzeeGame game);

    @Mapping(source = "bonus", target = "bonusName")
    @Mapping(source = "player.user.username", target = "playerName")
    YathzeePlayerBonusDTO toBonusDTO(YathzeePlayerBonus bonus);
}
