package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.response.ResponseObject;
import org.dhbw.mosbach.ai.cmd.response.Success;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

public final class ValidationResult {

    private boolean valid;
    private ResponseObject response;

    public ValidationResult(@NotNull ResponseObject response) {
        // If the response status is 200 OK, the validation
        // is successful.
        this.valid = response.getStatus().getCode() == Response.Status.OK.getStatusCode();
        this.response = response;
    }

    public static ValidationResult success(String message) {
        return new ValidationResult(new Success(message));
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isInvalid() {
        return !valid;
    }

    public ResponseObject getResponse() {
        return response;
    }
}
