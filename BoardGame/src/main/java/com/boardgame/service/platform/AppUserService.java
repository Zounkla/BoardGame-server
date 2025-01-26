package com.boardgame.service.platform;

import com.boardgame.config.JwtUtils;
import com.boardgame.dto.platform.AppUserDTO;
import com.boardgame.dto.platform.LoginRequest;
import com.boardgame.entity.platform.AppUser;
import com.boardgame.exceptions.platform.InvalidLoginException;
import com.boardgame.exceptions.platform.UserAlreadyRegisteredException;
import com.boardgame.repository.platform.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    public AppUser register(LoginRequest loginRequest) throws UserAlreadyRegisteredException {
        if (appUserRepository.findByUsername(loginRequest.getUsername()).isPresent()) {
            throw new UserAlreadyRegisteredException("User with this nickname already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(loginRequest.getUsername());
        user.setPassword(loginRequest.getPassword());
        user.setRoles(new HashSet<>());
        user.getRoles().add("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserRepository.save(user);
        return user;
    }

    public String login(LoginRequest loginRequest) throws InvalidLoginException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            return jwtUtils.generateJwtToken(authentication);

        } catch (AuthenticationException e) {
            throw new InvalidLoginException("Invalid username or password");
        }
    }

    public AppUserDTO toDTO(AppUser user) {
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setUsername(user.getUsername());
        appUserDTO.setRoles(user.getRoles());
        return appUserDTO;
    }
}
