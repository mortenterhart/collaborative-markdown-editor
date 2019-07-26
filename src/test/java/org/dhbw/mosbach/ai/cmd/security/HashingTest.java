package org.dhbw.mosbach.ai.cmd.security;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing the Hashing class
 *
 * @author 3040018
 */
public class HashingTest {

    private Hashing hashing;

    @Before
    public void init() {
        hashing = new Hashing();
    }

    @Test
    public void testHashPassword() {
        String password = "blabla12";
        String hash = hashing.hashPassword(password);

        String password1 = "blabla12";
        String hash1 = hashing.hashPassword(password1);

        // Make sure the passwords are different due to salting the hash
        assertNotEquals(hash, hash1);
    }

    @Test
    public void testCheckPassword() {
        String correctPassword = "test2";
        String wrongPassword = "testtest";
        String hash = "$2a$12$Q7pgb8v0v3Oj0ZmbvFoU9uRulY0BNMZMjfnZLo9ofpGdIq4f7oVX2";

        assertTrue(hashing.checkPassword(correctPassword, hash));
        assertFalse(hashing.checkPassword(wrongPassword, hash));
    }

    @Test
    public void testHashDocContent() {
        String content = "test content 123 !öäüß";
        String hash = hashing.hashDocContent(content);
        String expectedHash = "d2a43c8e0de0fe56ec69d70f3540682ab30684c1";

        assertEquals(expectedHash, hash);
    }

    @Test
    public void testHashDocEmptyContent() {

        String emptyContent = "";
        String nullContent = null;
        String expectedHash = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

        String hashEmptyContent = hashing.hashDocContent(emptyContent);
        String hashNullContent = hashing.hashDocContent(nullContent);

        assertEquals(expectedHash, hashEmptyContent);
        assertEquals(expectedHash, hashNullContent);
    }
}
