package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class DocumentTransferModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @JsonProperty(value = PayloadParameters.NEW_OWNER_NAME, required = true)
    private String newOwnerName;

    @JsonCreator
    public DocumentTransferModel(@JsonProperty(PayloadParameters.DOCUMENT_ID) int documentId,
                                 @JsonProperty(PayloadParameters.NEW_OWNER_NAME) String newOwnerName) {
        this.documentId = documentId;
        this.newOwnerName = newOwnerName;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getNewOwnerName() {
        return newOwnerName;
    }
}
