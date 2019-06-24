package org.dhbw.mosbach.ai.cmd.services.response.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.services.response.ResponseParameters;

import java.util.List;

public class DocumentListEntity {

    @JsonProperty(value = ResponseParameters.DOCUMENT_ICON, required = true)
    private String icon;

    @JsonProperty(value = ResponseParameters.DOCUMENT, required = true)
    private Doc document;

    @JsonProperty(value = ResponseParameters.COLLABORATOR_LIST, required = true)
    private List<Collaborator> collaborators;

    public DocumentListEntity(DocumentIcon icon, Doc document, List<Collaborator> collaborators) {
        this.icon = icon.getIdentifier();
        this.document = document;
        this.collaborators = collaborators;
    }

    public String getIcon() {
        return icon;
    }

    public Doc getDocument() {
        return document;
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }
}
