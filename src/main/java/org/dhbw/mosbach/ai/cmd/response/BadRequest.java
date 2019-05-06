package org.dhbw.mosbach.ai.cmd.response;

import javax.ws.rs.core.Response;

public class BadRequest extends ResponseObject {

    public BadRequest(String message) {
        super(Response.Status.BAD_REQUEST, message);
    }
}
