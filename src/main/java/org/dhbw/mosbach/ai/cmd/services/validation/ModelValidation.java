package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.payload.Payload;

import javax.validation.constraints.NotNull;

/**
 * The
 * @param <T>
 */
public interface ModelValidation<T extends Payload> {

    @NotNull
    public ValidationResult validate(@NotNull T payload);
}
