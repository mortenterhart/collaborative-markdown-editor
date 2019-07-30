package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class CollaboratorInsertionModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @JsonProperty(value = PayloadParameters.COLLABORATOR_USERNAME, required = true)
    private String collaboratorUsername;

    @JsonCreator
    public CollaboratorInsertionModel(@JsonProperty(PayloadParameters.DOCUMENT_ID) int documentId,
                                      @JsonProperty(PayloadParameters.COLLABORATOR_USERNAME) String collaboratorUsername) {
        this.documentId = documentId;
        this.collaboratorUsername = collaboratorUsername;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getCollaboratorUsername() {
        return collaboratorUsername;
    }
}
