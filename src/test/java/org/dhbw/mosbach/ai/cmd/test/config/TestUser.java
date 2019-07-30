package org.dhbw.mosbach.ai.cmd.test.config;

import org.dhbw.mosbach.ai.cmd.model.User;

public class TestUser extends User implements Cloneable {

    private static final long serialVersionUID = 5846623425879886960L;

    TestUser(int userId, String username, String email, String password) {
        this.setId(userId);
        this.setName(username);
        this.setMail(email);
        this.setPassword(password);
    }

    @Override
    public TestUser clone() {
        try {
            return (TestUser) super.clone();
        } catch (CloneNotSupportedException exc) {
            exc.printStackTrace();
        }

        return null;
    }
}
