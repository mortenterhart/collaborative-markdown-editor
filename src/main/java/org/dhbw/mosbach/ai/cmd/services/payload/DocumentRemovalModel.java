package org.dhbw.mosbach.ai.cmd.services.payload;

import org.dhbw.mosbach.ai.cmd.services.JsonParameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DocumentRemovalModel {

    @XmlElement(name = JsonParameters.DOCUMENT_ID, required = true)
    private int documentId;

    public int getDocumentId() {
        return documentId;
    }
}
