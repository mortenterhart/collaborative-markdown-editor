package org.dhbw.mosbach.ai.cmd.services.payload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DocumentInsertionModel implements Payload {

    @XmlElement(name = PayloadParameters.DOCUMENT_NAME, required = true)
    private String name;

    public String getName() {
        return name;
    }
}
