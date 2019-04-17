package org.dhbw.mosbach.ai.cmd.response;

import javax.ws.rs.core.Response;

public class Forbidden extends ResponseObject {

    public Forbidden() {
        super(Response.Status.FORBIDDEN);
    }
}
