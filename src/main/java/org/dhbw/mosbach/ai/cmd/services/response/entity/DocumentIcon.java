package org.dhbw.mosbach.ai.cmd.services.response.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
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
    public String toString() {
        return identifier;
    }
}
