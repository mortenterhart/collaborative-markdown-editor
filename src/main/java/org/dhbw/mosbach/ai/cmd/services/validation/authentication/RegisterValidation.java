package org.dhbw.mosbach.ai.cmd.services.validation.authentication;

import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

@RequestScoped
public class RegisterValidation implements ModelValidation<RegisterModel> {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern CONTAINS_UPPERCASE_LETTERS = Pattern.compile(".*\\p{javaUpperCase}.*");
    private static final Pattern CONTAINS_LOWERCASE_LETTERS = Pattern.compile(".*\\p{javaLowerCase}.*");
    private static final Pattern CONTAINS_DIGITS = Pattern.compile(".*\\p{javaDigit}.*");

    private static final Pattern VALID_EMAIL_FORMAT = Pattern.compile(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    private static final Pattern USERNAME_FORMAT = Pattern.compile("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$");

    @Inject
    private UserValidation userValidation;

    private BasicFieldValidation fieldValidation = new BasicFieldValidation();

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

    @NotNull
    private ValidationResult checkUsernameConstraints(String username) {
        final ValidationResult specifiedCheck = fieldValidation.checkSpecified(PayloadParameters.USERNAME, username);
        if (specifiedCheck.isInvalid()) {
            return specifiedCheck;
        }

        if (!USERNAME_FORMAT.matcher(username).matches()) {
            return ValidationResult.response(new BadRequest("Username has invalid formatting. Allowed characters include: A-Z a-z 0-9 Space _ -"));
        }

        final ValidationResult userExistenceCheck = userValidation.checkUserExists(username);
        if (userExistenceCheck.isValid()) {
            return ValidationResult.response(new BadRequest("Username '%s' is already registered", username));
        }

        return ValidationResult.success("Username is not registered yet");
    }

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
}

