package org.dhbw.mosbach.ai.cmd.services.payload;

import org.dhbw.mosbach.ai.cmd.test.config.TestUser;

public class TestLoginModel extends LoginModel {

    public TestLoginModel(TestUser user) {
        super(user.getName(), user.getPassword());
    }
}
