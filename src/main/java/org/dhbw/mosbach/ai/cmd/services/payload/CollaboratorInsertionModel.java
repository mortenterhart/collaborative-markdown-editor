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
    private String collaboratorName;

    @JsonCreator
    public CollaboratorInsertionModel(@JsonProperty(PayloadParameters.DOCUMENT_ID) int documentId,
                                      @JsonProperty(PayloadParameters.COLLABORATOR_USERNAME) String collaboratorName) {
        this.documentId = documentId;
        this.collaboratorName = collaboratorName;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getCollaboratorName() {
        return collaboratorName;
    }
}
