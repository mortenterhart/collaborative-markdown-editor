package org.dhbw.mosbach.ai.cmd.services.validation.basic;

import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.NotNull;

/**
 * @author 6694964
 * @version 1.1
 */
@RequestScoped
public class BasicFieldValidation {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(BasicFieldValidation.class);

    @NotNull
    public ValidationResult checkSpecified(@NotNull String fieldName, String fieldValue) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            return ValidationResult.response(new BadRequest("%s was not specified", capitalize(fieldName)));
        }

        return ValidationResult.success("Field '%s' has been specified", fieldName);
    }

    private String capitalize(String field) {
        if (field == null || field.length() == 0) {
            return field;
        }

        return Character.toUpperCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) : "");
    }
}
