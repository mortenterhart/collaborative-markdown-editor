package org.dhbw.mosbach.ai.cmd.services.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhbw.mosbach.ai.cmd.model.User;

public class LoginUserResponse extends Success {

    @JsonProperty(value = ResponseParameters.USER, required = true)
    private final User user;

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
