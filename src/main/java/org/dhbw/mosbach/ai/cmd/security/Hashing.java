package org.dhbw.mosbach.ai.cmd.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to hash passwords using the BCrypt algorithm
 *
 * @author 3040018
 */
public class Hashing {

    /**
     * Hash a password with the BCrypt algorithm
     *
     * @param password Given password
     * @return The hashed password
     */
    public String hashPassword(String password) {
        return (password != null && !password.isEmpty()) ?
            BCrypt.hashpw(password, BCrypt.gensalt(12)) : null;
    }

    /**
     * Check if a password matches the hash from the database
     *
     * @param password Given password
     * @param hash     Given password hash
     * @return True, if the password matches the hashed password, false otherwise
     */
    public boolean checkPassword(String password, String hash) {
        return (password != null && !password.isEmpty()) &&
               (hash != null && !hash.isEmpty()) ?
            BCrypt.checkpw(password, hash) : false;
    }
}
