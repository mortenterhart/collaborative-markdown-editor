package org.dhbw.mosbach.ai.cmd.services.validation.authentication;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class LoginValidation implements ModelValidation<LoginModel> {

    @Inject
    private BasicUserValidation basicUserValidation;

    @Inject
    private Hashing hashing;

    @Inject
    private BasicFieldValidation fieldValidation;

    /**
     * Checks the passed payload of the respective login model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for specification of username and password</li>
     * <li>check for the existence of the user designated by the username</li>
     * <li>check for correct password and matching to the user.</li>
     * </ul>
     *
     * The {@code validate} method follows the principle of error handling descending from
     * the Go programming language. Accordingly, a potential error in the validation is
     * returned as an unsuccessful {@link ValidationResult} using a sufficient error message
     * rather than throwing an exception which requires additional mappers in the services
     * to handle those exceptions. A successful validation is returned in the same manner
     * as a successful validation result.
     *
     * In any case, the method should accept a non-null payload and return a non-null
     * validation result. In case the payload should be {@code null} this method should
     * answer with an unsuccessful validation result containing an internal server error
     * response.
     *
     * @param model the provided non-null login model from the login service
     * @return a non-null validation result indicating if the login validation was
     * successful or not
     */
    @Override
    @NotNull
    public ValidationResult validate(@NotNull LoginModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("LoginModel is null"));
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
        final ValidationResult userExistenceCheck = basicUserValidation.checkUserExists(username);
        if (userExistenceCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("Invalid username or password"));
        }

        User user = basicUserValidation.getFoundUser();

        if (!hashing.checkPassword(password, user.getPassword())) {
            return ValidationResult.response(new BadRequest("Invalid username or password"));
        }

        return ValidationResult.success("Authentication successful");
    }
}
