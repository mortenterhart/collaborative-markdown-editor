package org.dhbw.mosbach.ai.cmd.services.validation.authentication;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;

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
    public ValidationResult validate(@NotNull LoginModel model) {
        if (model == null) {
            return new ValidationResult(new InternalServerError("LoginModel is null"));
        }

        final String username = model.getUsername();
        final String password = model.getPassword();

        final ValidationResult usernameCheck = fieldValidation.checkSpecified(PayloadParameters.USERNAME, username);
        if (usernameCheck.isInvalid()) {
            return usernameCheck;
        }

        final ValidationResult passwordCheck = fieldValidation.checkSpecified(PayloadParameters.PASSWORD, password);
        if (passwordCheck.isInvalid()) {
            return passwordCheck;
        }

        return checkCredentialsCorrect(username, password);
    }

    @NotNull
    private ValidationResult checkCredentialsCorrect(String username, String password) {
        User user = userDao.getUserByName(username);
        if (user == null) {
            return ValidationResult.response(new BadRequest("Invalid username or password"));
        }

        if (!hashing.checkPassword(password, user.getPassword())) {
            return ValidationResult.response(new BadRequest("Invalid username or password"));
        }

        return ValidationResult.success("Authentication successful");
    }
}
