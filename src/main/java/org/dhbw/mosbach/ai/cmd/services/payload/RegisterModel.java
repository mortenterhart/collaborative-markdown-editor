package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class RegisterModel implements Payload {

    @JsonProperty(value = PayloadParameters.USERNAME, required = true)
    private String username;

    @JsonProperty(value = PayloadParameters.EMAIL, required = true)
    private String email;

    @JsonProperty(value = PayloadParameters.PASSWORD, required = true)
    private String password;

    @JsonCreator
    public RegisterModel(@JsonProperty(PayloadParameters.USERNAME) String username,
                         @JsonProperty(PayloadParameters.EMAIL) String email,
                         @JsonProperty(PayloadParameters.PASSWORD) String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

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
