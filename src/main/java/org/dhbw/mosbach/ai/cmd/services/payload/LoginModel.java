package org.dhbw.mosbach.ai.cmd.services.payload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginModel implements Payload {

    @XmlElement(name = PayloadParameters.USERNAME, required = true)
    private String username;

    @XmlElement(name = PayloadParameters.PASSWORD, required = true)
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
