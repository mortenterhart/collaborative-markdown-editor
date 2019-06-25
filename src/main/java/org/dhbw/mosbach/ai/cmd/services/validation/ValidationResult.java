package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
import org.dhbw.mosbach.ai.cmd.services.response.ResponseObject;
import org.dhbw.mosbach.ai.cmd.services.response.Success;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

/**
 * The {@code ValidationResult} is an immutable object to be returned from a validation
 * method utilized through {@link ModelValidation#validate(Payload)}. The validation may
 * check the existence of resources such as documents or users, the retention of rights
 * and privileges to perform certain operations or other constraints to be matched.
 *
 * As a result, a new {@code ValidationResult} is returned consisting of a boolean value
 * indicating the validity of an API request and a concrete response object that can be
 * directly built into a valid JAX-RS response to be returned from the service.
 *
 * This implementation follows the example of the error handling principle of the Go
 * programming language. Instead of throwing an exception on internal server error or
 * invalid request the validation result is returned as a resulting object with a
 * status and a message that are contained in the response object. This way there is
 * no need for additional exception mappers handling those error states, but the error
 * is returned to the user as a response status and message.
 *
 * This class offers some static factory functions which allow creation of new instances
 * since the constructor is set to {@code private}. Using these, a validation method can
 * utilize either a successful validation result with a {@code 200 OK} response status
 * or create a failure response with a custom status after an unsuccessful validation.
 *
 * @author 6694964
 * @version 1.1
 * @see ModelValidation
 * @see Payload
 */
public final class ValidationResult {

    /**
     * The result of the validation whether is was successful or not
     */
    private final boolean valid;

    /**
     * An immutable response object containing the status and the message of a successful
     * or unsuccessful validation to be returned by a service
     */
    private final ResponseObject response;

    /**
     * Private constructor for a validation result not to allow own instantiation. The
     * validity of the request is examined through the status of the given response. If
     * the status is {@code 200 OK} the validation is considered successful, otherwise
     * not.
     *
     * @param response the response object containing the status and the message of the
     *                 validation
     */
    private ValidationResult(@NotNull ResponseObject response) {
        // If the response status is 200 OK, the validation
        // is successful.
        this.valid = response.getStatus().getCode() == Response.Status.OK.getStatusCode();
        this.response = response;
    }

    /**
     * Factory function to create a successful validation result with a given message
     * inside after a successful validation.
     *
     * @param message the message for the response
     * @return a successful validation result with the message
     */
    public static ValidationResult success(String message) {
        return new ValidationResult(new Success(message));
    }

    /**
     * Factory function to create a successful validation result with a given format
     * string and its corresponding arguments building the response message after a
     * successful validation.
     *
     * @param format the format string containing placeholders
     * @param args   the arguments for the placeholders
     * @return a successful validation result with the message
     */
    public static ValidationResult success(String format, Object... args) {
        return success(String.format(format, args));
    }

    /**
     * Factory function to create a validation result with a custom response containing
     * the status and the message. Exclusively used by responses other than {@code 200 OK}
     * for failed validations.
     *
     * @param response the custom response object
     * @return a custom validation result with the given response
     */
    public static ValidationResult response(@NotNull ResponseObject response) {
        return new ValidationResult(response);
    }

    /**
     * Checks if the validation result is successful.
     *
     * @return true if the validation was successful, otherwise false.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Checks if the validation result is unsuccessful.
     *
     * @return true if the validation was unsuccessful, otherwise false.
     */
    public boolean isInvalid() {
        return !valid;
    }

    /**
     * Returns the response object contained in this validation result.
     *
     * @return the response object.
     */
    public ResponseObject getResponse() {
        return response;
    }

    /**
     * Convenient method directly creating the JAX-RS response that can be returned from
     * a service if the validation has failed.
     *
     * @return the concrete JAX-RS response
     */
    public Response buildResponse() {
        return response.buildResponse();
    }
}
