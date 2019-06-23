package org.dhbw.mosbach.ai.cmd.services.response.entity;

import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.services.response.ResponseParameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class DocumentListEntity {

    @XmlElement(name = ResponseParameters.DOCUMENT_ICON, required = true)
    private String icon;

    @XmlElement(name = ResponseParameters.DOCUMENT, required = true)
    private Doc document;

    @XmlElement(name = ResponseParameters.COLLABORATOR_LIST, required = true)
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
