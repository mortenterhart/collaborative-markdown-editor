package org.dhbw.mosbach.ai.cmd.util;

/**
 * Utility enum to set if a collaborator has access to a doc
 *
 * @author spa102716
 */
public enum HasAccess {

    Y("Y"),
    N("N");

    private String hasAccess;

    private HasAccess(String hasAccess) {
        this.hasAccess = hasAccess;
    }

    public String getHasAccess() {
        return this.hasAccess;
    }
}
