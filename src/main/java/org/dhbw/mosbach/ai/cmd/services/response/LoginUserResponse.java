package org.dhbw.mosbach.ai.cmd.services.response;

import org.dhbw.mosbach.ai.cmd.model.User;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginUserResponse extends Success {

    @XmlElement(name = ResponseParameters.USER, required = true)
    private User user;

    public LoginUserResponse(User user, String message) {
        super(message);
        this.user = user;
    }

    public LoginUserResponse(User user, String format, Object... args) {
        super(format, args);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
