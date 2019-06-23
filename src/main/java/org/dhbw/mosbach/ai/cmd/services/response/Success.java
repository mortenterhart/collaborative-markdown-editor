package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Success extends ResponseObject {

    public Success(String message) {
        super(Response.Status.OK, message);
    }

    public Success(String format, Object... args) {
        this(String.format(format, args));
    }
}
