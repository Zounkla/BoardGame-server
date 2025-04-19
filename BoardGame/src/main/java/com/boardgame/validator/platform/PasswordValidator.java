package com.boardgame.validator.platform;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidator {
    String message() default "Password must contain at least one uppercase letter, one lowercase letter, one digit" +
            ", one special character and at least 8 characters.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
