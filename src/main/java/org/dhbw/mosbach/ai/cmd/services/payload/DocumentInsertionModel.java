package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentInsertionModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_NAME, required = true)
    private String name;

    public String getName() {
        return name;
    }
}
