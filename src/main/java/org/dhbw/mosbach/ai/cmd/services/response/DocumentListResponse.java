package org.dhbw.mosbach.ai.cmd.services.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentListEntity;

import java.util.List;

public class DocumentListResponse extends Success {

    @JsonProperty(value = ResponseParameters.DOCUMENT_LIST, required = true)
    private final List<DocumentListEntity> documents;

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
