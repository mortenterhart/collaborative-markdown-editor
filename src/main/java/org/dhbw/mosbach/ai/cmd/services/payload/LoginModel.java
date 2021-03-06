package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class LoginModel implements Payload {

    @JsonProperty(value = PayloadParameters.USERNAME, required = true)
    private String username;

    @JsonProperty(value = PayloadParameters.PASSWORD, required = true)
    private String password;

    @JsonCreator
    public LoginModel(@JsonProperty(PayloadParameters.USERNAME) String username,
                      @JsonProperty(PayloadParameters.PASSWORD) String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
