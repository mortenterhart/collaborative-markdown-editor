package org.dhbw.mosbach.ai.cmd.services.payload;

import org.dhbw.mosbach.ai.cmd.services.FormParameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginModel {

    @XmlElement(name = FormParameters.USERNAME)
    private String username;

    @XmlElement(name = FormParameters.PASSWORD)
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
