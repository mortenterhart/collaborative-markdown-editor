package org.dhbw.mosbach.ai.cmd.services.response.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.services.response.ResponseParameters;

import java.util.List;

/**
 * @author 6694964
 * @version 1.3
 */
public class DocumentListEntity {

    @JsonProperty(value = ResponseParameters.DOCUMENT_ICON, required = true)
    private final DocumentIcon icon;

    @JsonProperty(value = ResponseParameters.DOCUMENT, required = true)
    private final Doc document;

    @JsonProperty(value = ResponseParameters.COLLABORATOR_LIST, required = true)
    private final List<Collaborator> collaborators;

    public DocumentListEntity(DocumentIcon icon, Doc document, List<Collaborator> collaborators) {
        this.icon = icon;
        this.document = document;
        this.collaborators = collaborators;
    }

    public DocumentIcon getIcon() {
        return icon;
    }

    public Doc getDocument() {
        return document;
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }
}
