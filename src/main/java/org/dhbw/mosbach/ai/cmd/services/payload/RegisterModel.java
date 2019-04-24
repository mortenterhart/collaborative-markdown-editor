package org.dhbw.mosbach.ai.cmd.services.payload;

import org.dhbw.mosbach.ai.cmd.services.JsonParameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterModel {

    @XmlElement(name = JsonParameters.USERNAME)
    private String username;

    @XmlElement(name = JsonParameters.EMAIL)
    private String email;

    @XmlElement(name = JsonParameters.PASSWORD)
    private String password;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
