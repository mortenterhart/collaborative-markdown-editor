package org.dhbw.mosbach.ai.cmd.services.payload;

import org.dhbw.mosbach.ai.cmd.services.JsonParameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollaboratorRemovalModel implements Payload {

    @XmlElement(name = JsonParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @XmlElement(name = JsonParameters.COLLABORATOR_ID, required = true)
    private int collaboratorId;

    public int getDocumentId() {
        return documentId;
    }

    public int getCollaboratorId() {
        return collaboratorId;
    }
}
