package org.dhbw.mosbach.ai.cmd.security;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
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
    
    /**
     * Hash the content of a doc for comparison purposes.
     * SHA-1 is used since it does not have to be cryptographically secure
     * but instead needs to be quite fast.
     * @param content Given content
     * @return A hex representation of the SHA-1 hash of the content
     */
    public String hashDocContent(String content) {
    	
    	try {
    		MessageDigest md = MessageDigest.getInstance(CmdConfig.HASH_DOC_CONTENT);

    		return new BigInteger(1, md.digest(content.getBytes())).toString(16);    		
    	} catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
}
