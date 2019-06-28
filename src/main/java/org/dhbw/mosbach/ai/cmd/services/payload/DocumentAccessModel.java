package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class DocumentAccessModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    public DocumentAccessModel(int documentId) {
        this.documentId = documentId;
    }

    public int getDocumentId() {
        return documentId;
    }
}
