package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.services.payload.Payload;

public interface ModelValidation<T extends Payload> {

    public ValidationResult validate(T payload);
}
