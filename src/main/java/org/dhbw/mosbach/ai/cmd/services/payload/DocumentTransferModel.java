package org.dhbw.mosbach.ai.cmd.services.payload;

import org.dhbw.mosbach.ai.cmd.services.JsonParameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DocumentTransferModel {

    @XmlElement(name = JsonParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @XmlElement(name = JsonParameters.NEW_OWNER_NAME, required = true)
    private String newOwnerName;

    public int getDocumentId() {
        return documentId;
    }

    public String getNewOwnerName() {
        return newOwnerName;
    }
}
