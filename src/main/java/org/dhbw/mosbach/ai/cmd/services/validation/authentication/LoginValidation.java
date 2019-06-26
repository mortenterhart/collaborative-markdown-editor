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
 * Considering that loading a user from the database is an expensive operation the loaded
 * user is cached within the {@code foundUser} attribute. This way the service calling
 * validation methods contained in this class does not need to load the user again, but
 * can fetch the cached user via the getter method {@link LoginValidation#getFoundUser}.
 * Accordingly, there is no duplication of database operations increasing performance.
 *
 * If the cached user is accessed via the mentioned getter method the cache attribute is
 * set back to {@code null} to clear the cache for next checks and to signalize other
 * services using these validation methods that there has been no user yet that was loaded
 * from the database.
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
     * Cached instance of the last loaded user object from the database in order to
     * reduce database operations and increase request processing performance. Once
     * accessed, this user attribute is cleared back to {@code null}.
     */
    private User foundUser;

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
     * If the user exists it is temporarily stored inside the {@code foundUser} attribute
     * and can be fetched by a service consuming this login validation using the getter
     * method {@link LoginValidation#getFoundUser()}. After that the method checks further
     * if the supplied password matches the one stored in database.
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

        foundUser = basicUserValidation.getFoundUser();

        if (!hashing.checkPassword(password, foundUser.getPassword())) {
            return ValidationResult.response(new BadRequest("Invalid username or password"));
        }

        return ValidationResult.success("Authentication successful");
    }

    /**
     * Retrieves the cached instance of the loaded user from the database after checking
     * for the existence of the user designated by the applied username in {@link
     * LoginValidation#checkCredentialsCorrect(String, String)}. If the method has found
     * a concrete user this instance is returned. If no user was found or the method was
     * not invoked before this one the result will be {@code null}.
     *
     * @return the cached user if the login validation has found one or {@code null} if
     * no user was found or the validation was not invoked previously.
     */
    public User getFoundUser() {
        User user = foundUser;
        foundUser = null;
        return user;
    }
}
