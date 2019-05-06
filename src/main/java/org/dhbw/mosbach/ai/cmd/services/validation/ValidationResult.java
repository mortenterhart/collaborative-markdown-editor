package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.response.ResponseObject;

import javax.validation.constraints.NotNull;

public class ValidationResult {

    private boolean valid;
    private String errorMessage;
    private ResponseObject response;

    private ValidationResult(boolean valid) {
        this.valid = valid;
    }

    public ValidationResult(@NotNull ResponseObject response) {
        this.valid = false;
        this.errorMessage = response.getMessage();
        this.response = response;
    }

    public static ValidationResult newValid() {
        return new ValidationResult(true);
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isInvalid() {
        return !valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ResponseObject getResponse() {
        return response;
    }
}
