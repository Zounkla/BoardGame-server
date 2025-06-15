package com.boardgame.controller.tictactoe;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.boardgame.dto.tictactoe.TictactoeGameDTO;
import com.boardgame.mapper.tictactoe.TictactoeMapper;
import com.boardgame.service.tictactoe.TictactoeService;

@RestController
@RequestMapping("/tictactoe")
@AllArgsConstructor
public class TictactoeController {

    private final TictactoeMapper tictactoeMapper;
    private final TictactoeService tictactoeService;


}
