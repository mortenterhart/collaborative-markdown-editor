package org.dhbw.mosbach.ai.cmd.services.payload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DocumentRemovalModel implements Payload {

    @XmlElement(name = PayloadParameters.DOCUMENT_ID, required = true)
    private int documentId;

    public int getDocumentId() {
        return documentId;
    }
}
