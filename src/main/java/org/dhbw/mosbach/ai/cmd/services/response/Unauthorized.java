package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;

/**
 * @author 6694964
 * @version 1.1
 */
public class Unauthorized extends ResponseObject {

    public Unauthorized(String message) {
        super(Response.Status.UNAUTHORIZED, message);
    }
}
