package org.dhbw.mosbach.ai.cmd.services.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.Response;

/**
 * @author 6694964
 * @version 1.1
 */
public class Status {

    @JsonProperty(value = ResponseParameters.STATUS_CODE, required = true)
    private final int code;

    @JsonProperty(value = ResponseParameters.STATUS_DESCRIPTION, required = true)
    private final String description;

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
