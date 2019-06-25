package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.payload.Payload;

import javax.validation.constraints.NotNull;

/**
 * The {@code ModelValidation} is the base contract for all classes offering validation
 * functionalities to the REST endpoint services. It defines a method for validating
 * the payload of an incoming API request for data integrity, coherence and consistency.
 *
 * The implementing method returns a {@link ValidationResult} which contains the result
 * of the validation by specifying the status and an explanatory message about the
 * circumstances if and why the validation succeeded or failed. The message is then
 * redirected to the user via the service response who can correct his request according
 * to these requirements.
 *
 * The type parameter {@link T} represents the model class which is utilized by the
 * JAX-RS library to carry the request payload within its attributes. The service passes
 * the payload as is to the validation method in order to investigate its contents before
 * using the values provided in the request.
 *
 * @param <T> the model type used as payload to the service
 *
 * @author 6694964
 * @version 1.1
 *
 * @see ValidationResult
 * @see Payload
 */
public interface ModelValidation<T extends Payload> {

    /**
     * Checks the passed payload of the respective request model type for validity. The
     * validation may include examinations for existence of resources such as documents
     * and users, the presence of privileges to access a resource or perform a specific
     * operation and the check for certain constraints required for this operation.
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
     * @param payload the provided non-null payload to the service
     * @return a non-null validation result indicating if the validation was successful
     * or not
     */
    @NotNull
    public ValidationResult validate(@NotNull T payload);
}
