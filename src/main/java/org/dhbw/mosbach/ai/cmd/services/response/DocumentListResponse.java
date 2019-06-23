package org.dhbw.mosbach.ai.cmd.services.response;

import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentListEntity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class DocumentListResponse extends Success {

    @XmlElement(name = ResponseParameters.DOCUMENT_LIST, required = true)
    private List<DocumentListEntity> documents;

    public DocumentListResponse(List<DocumentListEntity> documents, String message) {
        super(message);
        this.documents = documents;
    }

    public DocumentListResponse(List<DocumentListEntity> documents, String format, Object... args) {
        super(format, args);
        this.documents = documents;
    }

    public List<DocumentListEntity> getDocuments() {
        return documents;
    }
}
