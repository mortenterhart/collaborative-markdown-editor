package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Unauthorized extends ResponseObject {

    public Unauthorized(String message) {
        super(Response.Status.UNAUTHORIZED, message);
    }
}
