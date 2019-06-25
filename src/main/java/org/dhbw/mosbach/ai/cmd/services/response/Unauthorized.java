package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;

public class Unauthorized extends ResponseObject {

    public Unauthorized(String message) {
        super(Response.Status.UNAUTHORIZED, message);
    }
}
