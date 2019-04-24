package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;

public class RegisterValidation {

    public static ValidationResult validate(RegisterModel model) {
        if (model == null) {
            return new ValidationResult("Register model is null");
        }

        String username = model.getUsername();
        String email = model.getEmail();
        String password = model.getPassword();



        return ValidationResult.newValid();
    }
}
