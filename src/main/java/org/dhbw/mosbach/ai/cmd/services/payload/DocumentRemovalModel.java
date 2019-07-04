package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class DocumentRemovalModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @JsonCreator
    public DocumentRemovalModel(@JsonProperty(PayloadParameters.DOCUMENT_ID) int documentId) {
        this.documentId = documentId;
    }

    public int getDocumentId() {
        return documentId;
    }
}
