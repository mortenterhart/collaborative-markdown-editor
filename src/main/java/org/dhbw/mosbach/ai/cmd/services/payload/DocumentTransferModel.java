package org.dhbw.mosbach.ai.cmd.services.payload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DocumentTransferModel implements Payload {

    @XmlElement(name = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    @XmlElement(name = PayloadParameters.NEW_OWNER_NAME, required = true)
    private String newOwnerName;

    public int getDocumentId() {
        return documentId;
    }

    public String getNewOwnerName() {
        return newOwnerName;
    }
}
