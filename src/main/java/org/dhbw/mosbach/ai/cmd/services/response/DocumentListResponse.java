package org.dhbw.mosbach.ai.cmd.services.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentListEntity;

import java.util.List;

/**
 * @author 6694964
 * @version 1.3
 */
public class DocumentListResponse extends Success {

    @JsonProperty(value = ResponseParameters.DOCUMENT_LIST, required = true)
    private final List<DocumentListEntity> documents;

    @JsonCreator
    public DocumentListResponse(@JsonProperty(ResponseParameters.DOCUMENT_LIST) List<DocumentListEntity> documents,
                                @JsonProperty(ResponseParameters.MESSAGE) String message) {
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
