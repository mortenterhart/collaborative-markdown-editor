package org.dhbw.mosbach.ai.cmd.services.response;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

/**
 * @author 6694964
 * @version 1.1
 */
public class BadRequest extends ResponseObject {

    public BadRequest(String message) {
        super(Response.Status.BAD_REQUEST, message);
    }

    public BadRequest(@NotNull String format, Object... args) {
        this(String.format(format, args));
    }
}
