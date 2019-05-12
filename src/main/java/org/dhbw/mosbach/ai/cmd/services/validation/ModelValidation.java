package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.payload.Payload;

import javax.validation.constraints.NotNull;

public interface ModelValidation<T extends Payload> {

    @NotNull
    public ValidationResult validate(T payload);
}
