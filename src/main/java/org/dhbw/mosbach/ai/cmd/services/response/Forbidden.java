package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

public class Forbidden extends ResponseObject {

    public Forbidden(String message) {
        super(Response.Status.FORBIDDEN, message);
    }
}
