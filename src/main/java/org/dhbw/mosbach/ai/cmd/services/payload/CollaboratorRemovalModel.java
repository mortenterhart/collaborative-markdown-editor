package org.dhbw.mosbach.ai.cmd.services.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 6694964
 * @version 1.2
 */
public class CollaboratorRemovalModel implements Payload {

    @JsonProperty(value = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @JsonProperty(value = PayloadParameters.COLLABORATOR_ID, required = true)
    private int collaboratorId;

    @JsonCreator
    public CollaboratorRemovalModel(@JsonProperty(PayloadParameters.DOCUMENT_ID) int documentId,
                                    @JsonProperty(PayloadParameters.COLLABORATOR_ID) int collaboratorId) {
        this.documentId = documentId;
        this.collaboratorId = collaboratorId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public int getCollaboratorId() {
        return collaboratorId;
    }
}
