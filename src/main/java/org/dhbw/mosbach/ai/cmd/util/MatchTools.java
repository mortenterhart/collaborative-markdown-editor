package org.dhbw.mosbach.ai.cmd.util;

import java.util.Objects;

public final class MatchTools {

    public static String findDisparateMatches(String regex, String potion) {
        return Objects.requireNonNull(potion).replaceAll(regex, "");
    }
}
