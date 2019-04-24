package org.dhbw.mosbach.ai.cmd.services.validation;

public class ValidationResult {

    private boolean valid;
    private String errorMessage;

    private ValidationResult(boolean valid) {
        this.valid = valid;
    }

    public ValidationResult(String errorMessage) {
        this.valid = false;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult newValid() {
        return new ValidationResult(true);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
