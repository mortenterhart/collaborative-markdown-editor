package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.JsonParameters;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class LoginValidation implements ModelValidation<LoginModel> {

    @Inject
    private UserDao userDao;

    @Inject
    private Hashing hashing;

    @Inject
    private BasicFieldValidation fieldValidation;

    @Override
    @NotNull
    public ValidationResult validate(LoginModel model) {
        String username = model.getUsername();
        String password = model.getPassword();

        ValidationResult usernameCheck = fieldValidation.checkSpecified(JsonParameters.USERNAME, username);
        if (usernameCheck.isInvalid()) {
            return usernameCheck;
        }

        ValidationResult passwordCheck = fieldValidation.checkSpecified(JsonParameters.PASSWORD, password);
        if (passwordCheck.isInvalid()) {
            return passwordCheck;
        }

        return checkCredentialsCorrect(username, password);
    }

    @NotNull
    private ValidationResult checkCredentialsCorrect(String username, String password) {
        User user = userDao.getUserByName(username);
        if (user == null) {
            return new ValidationResult(new BadRequest("Invalid username or password"));
        }

        if (!hashing.checkPassword(password, user.getPassword())) {
            return new ValidationResult(new BadRequest("Invalid username or password"));
        }

        return ValidationResult.success("Authentication successful");
    }
}
