package org.dhbw.mosbach.ai.cmd.services.validation;

public class UserValidation {

    public static ValidationResult validate(String username) {
        if (username == null || username.isEmpty()) {
            return new ValidationResult("");
        }

        return ValidationResult.newValid();
    }
}
