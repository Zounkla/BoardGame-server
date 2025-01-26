package com.boardgame.controller.platform;

import com.boardgame.dto.platform.AppUserDTO;
import com.boardgame.dto.platform.LoginRequest;
import com.boardgame.entity.platform.AppUser;
import com.boardgame.exceptions.platform.InvalidLoginException;
import com.boardgame.exceptions.platform.UserAlreadyRegisteredException;
import com.boardgame.service.platform.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserDTO> register(@RequestBody LoginRequest loginRequest) throws UserAlreadyRegisteredException {
        AppUser user = appUserService.register(loginRequest);
        return ResponseEntity.ok(appUserService.toDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) throws InvalidLoginException {
        String token = appUserService.login(loginRequest);
        return ResponseEntity.ok(token);
    }
}
