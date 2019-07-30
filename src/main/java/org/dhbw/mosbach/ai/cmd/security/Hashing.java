package org.dhbw.mosbach.ai.cmd.security;

import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility to hash passwords using the BCrypt algorithm
 *
 * @author 3040018
 */
public class Hashing {

    private static final Logger log = LoggerFactory.getLogger(Hashing.class);

    /**
     * Hash a password with the BCrypt algorithm
     *
     * @param password Given password
     * @return The hashed password
     */
    public String hashPassword(String password) {
        return password != null && !password.isEmpty() ?
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
        return password != null && !password.isEmpty() &&
                hash != null && !hash.isEmpty() &&
                BCrypt.checkpw(password, hash);
    }

    /**
     * Hash the content of a doc for comparison purposes.
     * SHA-1 is used since it does not have to be cryptographically secure
     * but instead needs to be quite fast.
     *
     * @param content Given content
     * @return A hex representation of the SHA-1 hash of the content, if the
     * given String is null or empty it returns the SHA-1 hash for an empty input
     */
    public String hashDocContent(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance(CmdConfig.HASH_DOC_CONTENT);

            if (content != null && !content.isEmpty()) {
                return new BigInteger(1, md.digest(content.getBytes(StandardCharsets.UTF_8))).toString(16);
            } else {
                // SHA-1 hash for input of ""
                return "da39a3ee5e6b4b0d3255bfef95601890afd80709";
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
