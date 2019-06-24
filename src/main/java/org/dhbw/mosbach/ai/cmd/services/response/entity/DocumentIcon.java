package org.dhbw.mosbach.ai.cmd.services.response.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentIcon {

    PERSON("person"),
    GROUP("group");

    private String identifier;

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
