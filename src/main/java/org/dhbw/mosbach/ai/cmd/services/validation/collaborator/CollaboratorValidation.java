package org.dhbw.mosbach.ai.cmd.services.validation.collaborator;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CollaboratorValidation {

    @Inject
    private CollaboratorDao collaboratorDao;

    private Collaborator foundCollaborator;

    public ValidationResult checkCollaboratorExists(User user, Doc document) {
        Collaborator collaborator = collaboratorDao.getCollaborator(user, document);
        if (collaborator == null) {
            return ValidationResult.response(new BadRequest("Collaborator '%s' does not exist for the document '%d'.", user.getName(), document.getId()));
        }

        foundCollaborator = collaborator;
        return ValidationResult.success("Collaborator '%s' exists for document '%d'.", user.getName(), document.getId());
    }

    public ValidationResult checkCollaboratorExists(int collaboratorId) {
        Collaborator collaborator = collaboratorDao.getCollaborator(collaboratorId);
        if (collaborator == null) {
            return ValidationResult.response(new BadRequest("Collaborator '%d' does not exist.", collaboratorId));
        }

        foundCollaborator = collaborator;
        return ValidationResult.success("Collaborator '%d' exists.", collaboratorId);
    }

    public ValidationResult checkUserEqualsCollaborator(User currentUser, Collaborator collaborator) {
        if (currentUser.getId() != collaborator.getUser().getId()) {
            return ValidationResult.response(new BadRequest("Current user is not this collaborator"));
        }

        return ValidationResult.success("Current user is equal to this collaborator");
    }

    public ValidationResult checkCollaboratorBelongsToDocument(Collaborator collaborator, int documentId) {
        if (documentId != collaborator.getDoc().getId()) {
            return ValidationResult.response(new BadRequest("Collaborator '%s' does not belong to document '%d'", collaborator.getUser().getName(), documentId));
        }

        return ValidationResult.success("Collaborator '%s' is attached to document '%d'", collaborator.getUser().getName(), documentId);
    }

    public Collaborator getFoundCollaborator() {
        Collaborator collaborator = foundCollaborator;
        foundCollaborator = null;
        return collaborator;
    }
}
