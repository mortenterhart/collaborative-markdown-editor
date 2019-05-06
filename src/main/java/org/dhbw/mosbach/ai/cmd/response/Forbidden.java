package org.dhbw.mosbach.ai.cmd.response;

import javax.ws.rs.core.Response;

public class Forbidden extends ResponseObject {

    public Forbidden(String message) {
        super(Response.Status.FORBIDDEN, message);
    }
}
