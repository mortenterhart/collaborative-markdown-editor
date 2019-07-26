package org.dhbw.mosbach.ai.cmd.test.helper;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {

    private static final String ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "^$*.[]{}()?-\"§!@#%&/\\,><':;|_~`";
    private static final String CHAR_SET = ALPHA_UPPER + ALPHA_LOWER + DIGITS + SYMBOLS;

    private Random random = new SecureRandom();

    public String generateSecurePassword(int length) {
        // Only allow password lengths that are at least 8
        if (length < 8) {
            throw new IllegalArgumentException("length has to be at least 8, but is " + length);
        }

        // Use at least one uppercase and lowercase character, one digit
        // and one symbol in the password
        char[] password = new char[length];
        password[0] = ALPHA_UPPER.charAt(random.nextInt(ALPHA_UPPER.length()));
        password[1] = ALPHA_LOWER.charAt(random.nextInt(ALPHA_LOWER.length()));
        password[2] = DIGITS.charAt(random.nextInt(DIGITS.length()));
        password[3] = SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));

        // Fill the last slots with random characters from the
        // whole character set
        for (int i = 4; i < length; i++) {
            password[i] = CHAR_SET.charAt(random.nextInt(CHAR_SET.length()));
        }

        // Shuffle each character
        for (int i = 0; i < password.length; i++) {
            int pos = random.nextInt(password.length);
            char temp = password[i];
            password[i] = password[pos];
            password[pos] = temp;
        }

        return new String(password);
    }
}
