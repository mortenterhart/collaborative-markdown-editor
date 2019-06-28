package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class DocumentInsertionModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_NAME, required = true)
    private String name;

    public DocumentInsertionModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
