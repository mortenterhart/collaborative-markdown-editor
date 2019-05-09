package org.dhbw.mosbach.ai.cmd.services.response;

import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.History;

import java.util.List;

public class DocumentListModel {

    private String icon;
    private Doc document;
    private List<History> history;
    private List<Collaborator> collaborators;

    public DocumentListModel(String icon, Doc document, List<History> history, List<Collaborator> collaborators) {
        this.icon = icon;
        this.document = document;
        this.document.getCuser().setPassword("");
        this.document.getUuser().setPassword("");
        this.history = history;
        this.collaborators = collaborators;
    }

    public String getIcon() {
        return icon;
    }

    public Doc getDocument() {
        return document;
    }

    public List<History> getHistory() {
        return history;
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }
}
