package org.dhbw.mosbach.ai.cmd.services.helper;

public class TestUser {

    private String username;
    private String password;

    public TestUser(String username, String password) {
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
