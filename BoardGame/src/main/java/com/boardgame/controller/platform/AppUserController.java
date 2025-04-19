package com.boardgame.controller.platform;

import com.boardgame.dto.platform.AppUserDTO;
import com.boardgame.dto.platform.LoginRequest;
import com.boardgame.entity.platform.AppUser;
import com.boardgame.exceptions.platform.InvalidCredentialsException;
import com.boardgame.exceptions.platform.UserAlreadyRegisteredException;
import com.boardgame.mapper.platform.AppUserMapper;
import com.boardgame.service.platform.AppUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AppUserController {

    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;

    @PostMapping("/register")
    public ResponseEntity<AppUserDTO> register(@Valid @RequestBody LoginRequest loginRequest) throws UserAlreadyRegisteredException {
        AppUser user = appUserService.register(loginRequest);
        return ResponseEntity.ok(appUserMapper.toDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) throws InvalidCredentialsException {
        String token = appUserService.login(loginRequest);
        return ResponseEntity.ok(token);
    }
}
