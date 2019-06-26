package org.dhbw.mosbach.ai.cmd.services.validation.authentication;

import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;
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
import java.util.regex.Pattern;

/**
 * The {@code RegisterValidation} offers a validation for the registration model by
 * implementing the {@link ModelValidation} interface. Hence, a registering request
 * is validated by some steps of checks for sufficient specification of fields,
 * various formatting rules to comply with syntax requirements and email validity,
 * non-existence of the applied username to disallow duplication of users and
 * security constraints for the password of a new user.
 *
 * This validation uses the injection mechanism offered by CDI to get access to more
 * general validation classes such as {@link BasicUserValidation} to perform the
 * registration validation correctly and to verify the applied fields.
 *
 * @author 6694964
 * @version 1.1
 *
 * @see ModelValidation
 * @see BasicUserValidation
 * @see org.dhbw.mosbach.ai.cmd.services.AuthenticationService
 */
@RequestScoped
public class RegisterValidation implements ModelValidation<RegisterModel> {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(RegisterValidation.class);

    /**
     * Minimal length for the password of a new user
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Regular expression checking for the existence of uppercase letters in
     * the password
     */
    private static final Pattern CONTAINS_UPPERCASE_LETTERS = Pattern.compile(".*\\p{javaUpperCase}.*");

    /**
     * Regular expression checking for the existence of lowercase letters in
     * the password
     */
    private static final Pattern CONTAINS_LOWERCASE_LETTERS = Pattern.compile(".*\\p{javaLowerCase}.*");

    /**
     * Regular expression checking for the existence of digits in the password
     */
    private static final Pattern CONTAINS_DIGITS = Pattern.compile(".*\\p{javaDigit}.*");

    /**
     * Regular expression defining the required conform syntax for a email address
     */
    private static final Pattern VALID_EMAIL_FORMAT = Pattern.compile(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    /**
     * Regular expression determining the required conform syntax for a username
     */
    private static final Pattern USERNAME_FORMAT = Pattern.compile("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$");

    /*
     * Injected fields for basic user and field validation
     */
    @Inject
    private BasicUserValidation basicUserValidation;

    @Inject
    private BasicFieldValidation fieldValidation;

    /**
     * Checks the passed payload of the respective registration model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for the specification of username, email address and password</li>
     * <li>check for the correct formatting of the username</li>
     * <li>check if the username is already registered</li>
     * <li>check for the conformity of the email address syntax</li>
     * <li>check for password security constraints (at least 8 characters containing
     * uppercase and lowercase letters and digits).</li>
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
     * @param model the provided non-null registration model from the register service
     * @return a non-null validation result indicating if the registration validation was
     * successful or not
     * @see ModelValidation#validate(Payload)
     * @see org.dhbw.mosbach.ai.cmd.services.AuthenticationService
     */
    @NotNull
    public ValidationResult validate(@NotNull RegisterModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("RegisterModel is null"));
        }

        final String username = model.getUsername();
        final String email = model.getEmail();
        final String password = model.getPassword();

        final ValidationResult usernameCheck = checkUsernameConstraints(username);
        if (usernameCheck.isInvalid()) {
            return usernameCheck;
        }

        final ValidationResult emailCheck = validateEmailSyntax(email);
        if (emailCheck.isInvalid()) {
            return emailCheck;
        }

        final ValidationResult passwordCheck = checkPasswordConstraints(password);
        if (passwordCheck.isInvalid()) {
            return passwordCheck;
        }

        return ValidationResult.success("Registration was successful");
    }

    /**
     * Verifies that the username parameter follows the formatting requirements and that the
     * username is not already registered. The method returns an unsuccessful validation result
     * if the username parameter was not specified, does not conform to the required format or
     * is already registered. In these cases the validation returns a {@code 400 Bad Request}
     * response with a corresponding message explaining the issue to the client.
     *
     * @param username the username to check for
     * @return a successful validation result if the username constraints match, otherwise an
     * unsuccessful validation result.
     */
    @NotNull
    private ValidationResult checkUsernameConstraints(String username) {
        final ValidationResult specifiedCheck = fieldValidation.checkSpecified(PayloadParameters.USERNAME, username);
        if (specifiedCheck.isInvalid()) {
            return specifiedCheck;
        }

        if (!USERNAME_FORMAT.matcher(username).matches()) {
            return ValidationResult.response(new BadRequest("Username has invalid formatting. Allowed characters include: A-Z a-z 0-9 Space _ -"));
        }

        final ValidationResult userExistenceCheck = basicUserValidation.checkUserExists(username);
        if (userExistenceCheck.isValid()) {
            return ValidationResult.response(new BadRequest("Username '%s' is already registered", username));
        }

        return ValidationResult.success("Username is not registered yet");
    }

    /**
     * Verifies that the supplied email parameter was specified and conforms to the valid email
     * syntax denoted by the regular expressions above. If the parameter was not specified or
     * if the email syntax is invalid an unsuccessful validation result containing a {@code
     * 400 Bad Request} is returned.
     *
     * @param email the applied email parameter
     * @return a successful validation result if the email syntax is valid, otherwise an unsuccessful
     * validation result.
     */
    @NotNull
    private ValidationResult validateEmailSyntax(String email) {
        final ValidationResult specifiedCheck = fieldValidation.checkSpecified(PayloadParameters.EMAIL, email);
        if (specifiedCheck.isInvalid()) {
            return specifiedCheck;
        }

        if (!VALID_EMAIL_FORMAT.matcher(email).matches()) {
            return ValidationResult.response(new BadRequest("Invalid email syntax"));
        }

        return ValidationResult.success("Email syntax is valid");
    }

    /**
     * Checks the supplied password against some security considerations in order to improve the
     * password reliability and security. The password must consist of at least 8 characters
     * containing at least one uppercase and lowercase letter and a digit. If one of these
     * checks fails an unsuccessful result is returned and the user needs to apply another
     * password.
     *
     * @param password the applied password
     * @return a successful validation result if the password matches all constraints, otherwise
     * an unsuccessful validation result.
     */
    @NotNull
    private ValidationResult checkPasswordConstraints(String password) {
        final ValidationResult specifiedCheck = fieldValidation.checkSpecified(PayloadParameters.PASSWORD, password);
        if (specifiedCheck.isInvalid()) {
            return specifiedCheck;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return ValidationResult.response(new BadRequest("Password requires at least %d characters", MIN_PASSWORD_LENGTH));
        }

        if (!CONTAINS_UPPERCASE_LETTERS.matcher(password).matches()) {
            return ValidationResult.response(new BadRequest("Password must contain uppercase letters"));
        }

        if (!CONTAINS_LOWERCASE_LETTERS.matcher(password).matches()) {
            return ValidationResult.response(new BadRequest("Password must contain lowercase letters"));
        }

        if (!CONTAINS_DIGITS.matcher(password).matches()) {
            return ValidationResult.response(new BadRequest("Password must contain digits"));
        }

        return ValidationResult.success("Password constraints match");
    }
}

