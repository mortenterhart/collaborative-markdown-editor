package org.dhbw.mosbach.ai.cmd.services.payload;

import org.dhbw.mosbach.ai.cmd.services.FormParameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterModel {

    @XmlElement(name = FormParameters.USERNAME)
    private String username;

    @XmlElement(name = FormParameters.EMAIL)
    private String email;

    @XmlElement(name = FormParameters.PASSWORD)
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
