package org.dhbw.mosbach.ai.cmd.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testing the Hashing class
 * 
 * @author 3040018
 */
public class TestHashing {
	
	private Hashing hashing;
	
	@BeforeEach
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
}