package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CollaboratorRemovalModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @JsonProperty(value = PayloadParameters.COLLABORATOR_ID, required = true)
    private int collaboratorId;

    public int getDocumentId() {
        return documentId;
    }

    public int getCollaboratorId() {
        return collaboratorId;
    }
}
