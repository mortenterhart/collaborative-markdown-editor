package org.dhbw.mosbach.ai.cmd.services.response.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author 6694964
 * @version 1.2
 */
public enum DocumentIcon {

    PERSON("person"),
    GROUP("group");

    private String identifier;

    @JsonCreator
    private DocumentIcon(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    @JsonValue
    public String toString() {
        return identifier;
    }
}
