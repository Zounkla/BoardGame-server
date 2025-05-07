package com.boardgame.mapper.lightsout;

import com.boardgame.dto.lightsout.LightsoutGameDTO;
import com.boardgame.entity.lightsout.LightsoutGame;
import com.boardgame.utils.lightsout.LightsoutGridConverter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = LightsoutGridConverter.class)
public interface LightsoutMapper {
    LightsoutGameDTO toLightsoutGameDTO(LightsoutGame lightsoutGame);
}
