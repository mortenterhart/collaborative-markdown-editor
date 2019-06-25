package org.dhbw.mosbach.ai.cmd.services.validation.authentication;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicUserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * The {@code LoginValidation} provides a validation for the login service defined in
 * the {@link org.dhbw.mosbach.ai.cmd.services.AuthenticationService} and examines the
 * passed login model in terms of value specification, user existence and username and
 * password contiguity.
 *
 * This validation uses the injection mechanism offered by CDI to get access to more
 * general validation classes such as {@link BasicUserValidation} to perform the login
 * validation correctly and to verify the applied username and password.
 *
 * @author 6694964
 * @version 1.1
 *
 * @see ModelValidation
 * @see BasicUserValidation
 * @see org.dhbw.mosbach.ai.cmd.services.AuthenticationService
 */
@RequestScoped
public class LoginValidation implements ModelValidation<LoginModel> {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(LoginValidation.class);

    /*
     * Injected fields for user and field validation and encryption
     * algorithm to check the input password against the stored one
     */

    @Inject
    private BasicUserValidation basicUserValidation;

    @Inject
    private BasicFieldValidation fieldValidation;

    @Inject
    private Hashing hashing;

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
     * @see ModelValidation#validate(Payload)
     * @see org.dhbw.mosbach.ai.cmd.services.AuthenticationService
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

    /**
     * Verifies the correctness of the supplied username and password by means of checking
     * for the existence of the user designated by the username and comparing the supplied
     * password to the one stored in the database. This method returns an unsuccessful
     * validation result if either the user does not exist or the password does not match
     * the correct one.
     *
     * Notably here is that the returned response is the same in both cases of user
     * non-existence or wrong applied password to prevent exposure of information about
     * existing or non-existing users.
     *
     * @param username the applied username
     * @param password the applied password
     * @return a successful validation result if username and password are correct, an
     * unsuccessful validation result otherwise.
     */
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
