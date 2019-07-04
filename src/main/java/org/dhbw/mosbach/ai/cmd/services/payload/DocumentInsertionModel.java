package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class DocumentInsertionModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_NAME, required = true)
    private String documentName;

    @JsonCreator
    public DocumentInsertionModel(@JsonProperty(PayloadParameters.DOCUMENT_NAME) String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }
}
