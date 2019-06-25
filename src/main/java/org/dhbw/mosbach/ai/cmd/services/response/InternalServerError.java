package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;

public class InternalServerError extends ResponseObject {

    public InternalServerError(String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR, message);
    }
}
