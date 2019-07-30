package org.dhbw.mosbach.ai.cmd.util;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Utility enum to set if a collaborator has access to a doc
 *
 * @author spa102716
 */
public enum HasAccess {

    Y("Y"),
    N("N");

    private String hasAccessString;

    private HasAccess(String hasAccessString) {
        this.hasAccessString = hasAccessString;
    }

    public String getHasAccessString() {
        return this.hasAccessString;
    }

    @JsonValue
    public boolean hasAccess() {
        return this.equals(Y);
    }
}
