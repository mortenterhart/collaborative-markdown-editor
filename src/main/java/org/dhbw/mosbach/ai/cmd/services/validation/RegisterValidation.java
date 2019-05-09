package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class RegisterValidation implements ModelValidation<RegisterModel> {

    private UserValidation userValidation = new UserValidation();

    public ValidationResult validate(RegisterModel model) {
        if (model == null) {
            return new ValidationResult(new InternalServerError("Register model is null"));
        }

        String username = model.getUsername();
        String email = model.getEmail();
        String password = model.getPassword();

        return ValidationResult.newValid();
    }
}

