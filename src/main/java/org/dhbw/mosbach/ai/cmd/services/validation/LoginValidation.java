package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class LoginValidation implements ModelValidation<LoginModel> {

    @Inject
    private UserValidation userValidation;

    @Override
    public ValidationResult validate(LoginModel payload) {
        if (userValidation.checkCredentialsCorrect(payload.getUsername(), payload.getPassword()).isValid()) {
            return ValidationResult.newValid();
        }
        return null;
    }
}
