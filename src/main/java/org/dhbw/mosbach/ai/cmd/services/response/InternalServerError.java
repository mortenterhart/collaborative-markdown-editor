package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;

/**
 * @author 6694964
 * @version 1.1
 */
public class InternalServerError extends ResponseObject {

    public InternalServerError(String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR, message);
    }
}
