package org.dhbw.mosbach.ai.cmd.response;

import javax.ws.rs.core.Response;

public class ResponseObject {

    private final Status status;
    private final String message;

    public ResponseObject(Response.Status status, String message) {
        this.status = new Status(status);
        this.message = message;
    }

    public Response buildResponse() {
        return Response.status(status.getCode()).entity(this).build();
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
