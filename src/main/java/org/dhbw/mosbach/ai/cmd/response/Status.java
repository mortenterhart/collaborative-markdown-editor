package org.dhbw.mosbach.ai.cmd.response;

import javax.ws.rs.core.Response;

public class Status {

    private int code;
    private String description;

    public Status(Response.Status status) {
        this.code = status.getStatusCode();
        this.description = status.getReasonPhrase();
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
