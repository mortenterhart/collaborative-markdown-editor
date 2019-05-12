package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.JsonParameters;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.regex.Pattern;

@RequestScoped
public class RegisterValidation implements ModelValidation<RegisterModel> {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern CONTAINS_UPPERCASE_LETTERS = Pattern.compile(".*\\p{javaUpperCase}.*");
    private static final Pattern CONTAINS_LOWERCASE_LETTERS = Pattern.compile(".*\\p{javaLowerCase}.*");
    private static final Pattern CONTAINS_DIGITS = Pattern.compile(".*\\p{javaDigit}.*");

    private static final Pattern VALID_EMAIL_FORMAT = Pattern.compile(
        "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    @Inject
    private UserDao userDao;

    private BasicFieldValidation fieldValidation = new BasicFieldValidation();

    public ValidationResult validate(RegisterModel model) {
        if (model == null) {
            return new ValidationResult(new InternalServerError("Register model is null"));
        }

        String username = model.getUsername();
        String email = model.getEmail();
        String password = model.getPassword();

        ValidationResult usernameCheck = checkUsernameExists(username);
        if (usernameCheck.isInvalid()) {
            return usernameCheck;
        }

        ValidationResult emailCheck = validateEmailSyntax(email);
        if (emailCheck.isInvalid()) {
            return emailCheck;
        }

        ValidationResult passwordCheck = checkPasswordConstraints(password);
        if (passwordCheck.isInvalid()) {
            return passwordCheck;
        }

        return ValidationResult.success("Registration was successful");
    }

    private ValidationResult checkUsernameExists(String username) {
        ValidationResult specifiedCheck = fieldValidation.checkSpecified(JsonParameters.USERNAME, username);
        if (specifiedCheck.isInvalid()) {
            return specifiedCheck;
        }

        if (userDao.getUserByName(username) != null) {
            return new ValidationResult(new BadRequest("Username '%s' is already registered", username));
        }

        return ValidationResult.success("Username is not registered yet");
    }

    private ValidationResult checkPasswordConstraints(String password) {
        ValidationResult specifiedCheck = fieldValidation.checkSpecified(JsonParameters.PASSWORD, password);
        if (specifiedCheck.isInvalid()) {
            return specifiedCheck;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return new ValidationResult(new BadRequest("Password requires at least %d characters", MIN_PASSWORD_LENGTH));
        }

        if (!CONTAINS_UPPERCASE_LETTERS.matcher(password).matches()) {
            return new ValidationResult(new BadRequest("Password must contain uppercase letters"));
        }

        if (!CONTAINS_LOWERCASE_LETTERS.matcher(password).matches()) {
            return new ValidationResult(new BadRequest("Password must contain lowercase letters"));
        }

        if (!CONTAINS_DIGITS.matcher(password).matches()) {
            return new ValidationResult(new BadRequest("Password must contain digits"));
        }

        return ValidationResult.success("Password constraints match");
    }

    private ValidationResult validateEmailSyntax(String email) {
        ValidationResult specifiedCheck = fieldValidation.checkSpecified(JsonParameters.EMAIL, email);
        if (specifiedCheck.isInvalid()) {
            return specifiedCheck;
        }

        if (!VALID_EMAIL_FORMAT.matcher(email).matches()) {
            return new ValidationResult(new BadRequest("Invalid email syntax"));
        }

        return ValidationResult.success("Email syntax is valid");
    }
}

