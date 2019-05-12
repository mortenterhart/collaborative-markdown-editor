package org.dhbw.mosbach.ai.cmd.services.validation;

import org.dhbw.mosbach.ai.cmd.response.BadRequest;

import javax.validation.constraints.NotNull;

public class BasicFieldValidation {

    @NotNull
    public ValidationResult checkSpecified(@NotNull String fieldName, String fieldValue) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            return new ValidationResult(new BadRequest("%s was not specified", capitalize(fieldName)));
        }

        return ValidationResult.success(String.format("Field '%s' has been specified", fieldName));
    }

    @NotNull
    private String capitalize(String field) {
        if (field == null || field.length() == 0) {
            return field;
        }

        return Character.toUpperCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) : "");
    }
}
