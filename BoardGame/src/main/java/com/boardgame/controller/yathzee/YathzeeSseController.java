package com.boardgame.controller.yathzee;

import com.boardgame.exceptions.yathzee.YathzeeGameNotFoundException;
import com.boardgame.exceptions.yathzee.YathzeePlayerNotFoundException;
import com.boardgame.service.yathzee.YathzeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@AllArgsConstructor
public class YathzeeSseController {

    private final YathzeeService yathzeeService;

    @GetMapping(value = "/yathzee/{gameId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGame(@PathVariable Long gameId) throws YathzeeGameNotFoundException,
            YathzeePlayerNotFoundException {

        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        return yathzeeService.createSseEmitterForGame(gameId, username);
    }
}
