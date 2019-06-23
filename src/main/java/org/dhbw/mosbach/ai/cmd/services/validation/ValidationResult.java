package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.response.ResponseObject;
import org.dhbw.mosbach.ai.cmd.services.response.Success;

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

    public static ValidationResult success(String format, Object... args) {
        return success(String.format(format, args));
    }

    public static ValidationResult response(@NotNull ResponseObject response) {
        return new ValidationResult(response);
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

    public Response buildResponse() {
        return response.buildResponse();
    }
}
