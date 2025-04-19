package com.boardgame.dto.platform;

import com.boardgame.validator.platform.PasswordValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username is required.")
    private String username;

    @PasswordValidator
    private String password;
}
