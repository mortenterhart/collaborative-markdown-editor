package org.dhbw.mosbach.ai.cmd.services.response;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Status {

    @XmlElement(name = ResponseParameters.STATUS_CODE, required = true)
    private int code;

    @XmlElement(name = ResponseParameters.STATUS_DESCRIPTION, required = true)
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
