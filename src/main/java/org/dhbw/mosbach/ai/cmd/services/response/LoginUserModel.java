package org.dhbw.mosbach.ai.cmd.services.response;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.ResponseObject;
import org.dhbw.mosbach.ai.cmd.response.Status;

import javax.ws.rs.core.Response;

public class LoginUserModel {

    private Status status;
    private String message;
    private User user;

    public LoginUserModel(ResponseObject response, User user) {
        this.status = response.getStatus();
        this.message = response.getMessage();
        this.user = user;
    }

    public Response buildResponse() {
        return Response.status(status.getCode()).entity(this).build();
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
