package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class DocumentRemovalModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    public DocumentRemovalModel(int documentId) {
        this.documentId = documentId;
    }

    public int getDocumentId() {
        return documentId;
    }
}
