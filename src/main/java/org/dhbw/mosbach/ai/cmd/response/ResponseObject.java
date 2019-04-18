package org.dhbw.mosbach.ai.cmd.response;

import javax.ws.rs.core.Response;

public class ResponseObject {

    private Status status;
    private String message;

    public ResponseObject(Response.Status status) {
        this.status = new Status(status);
    }

    public Response create(String message) {
        this.message = message;
        return Response.status(status.getCode()).entity(this).build();
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
