package org.dhbw.mosbach.ai.cmd.services.payload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollaboratorRemovalModel implements Payload {

    @XmlElement(name = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @XmlElement(name = PayloadParameters.COLLABORATOR_ID, required = true)
    private int collaboratorId;

    public int getDocumentId() {
        return documentId;
    }

    public int getCollaboratorId() {
        return collaboratorId;
    }
}
