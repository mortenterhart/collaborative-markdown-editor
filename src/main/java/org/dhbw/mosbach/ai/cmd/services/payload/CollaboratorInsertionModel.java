package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CollaboratorInsertionModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @JsonProperty(value = PayloadParameters.COLLABORATOR_USERNAME, required = true)
    private String collaboratorName;

    public int getDocumentId() {
        return documentId;
    }

    public String getCollaboratorName() {
        return collaboratorName;
    }
}
