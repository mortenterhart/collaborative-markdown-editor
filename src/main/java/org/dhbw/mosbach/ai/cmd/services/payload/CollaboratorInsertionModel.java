package org.dhbw.mosbach.ai.cmd.services.payload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollaboratorInsertionModel implements Payload {

    @XmlElement(name = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @XmlElement(name = PayloadParameters.COLLABORATOR_USERNAME, required = true)
    private String collaboratorName;

    public int getDocumentId() {
        return documentId;
    }

    public String getCollaboratorName() {
        return collaboratorName;
    }
}
