package org.dhbw.mosbach.ai.cmd.testconfig;

import org.dhbw.mosbach.ai.cmd.model.User;

public class TestUser extends User {

    private static final long serialVersionUID = 5846623425879886960L;

    public TestUser(String username, String email, String password) {
        this.setName(username);
        this.setMail(email);
        this.setPassword(password);
    }
}
