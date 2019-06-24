package org.dhbw.mosbach.ai.cmd.services.validation.collaborator;

import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class CollaboratorRemovalValidation implements ModelValidation<CollaboratorRemovalModel> {

    @Inject
    private SessionUtil sessionUtil;

    @Inject
    private DocumentValidation documentValidation;

    @Inject
    private CollaboratorValidation collaboratorValidation;

    private Collaborator collaborator;

    @Override
    @NotNull
    public ValidationResult validate(@NotNull CollaboratorRemovalModel model) {
        User currentUser = sessionUtil.getUser();

        int documentId = model.getDocumentId();
        int collaboratorId = model.getCollaboratorId();

        final ValidationResult documentExistenceCheck = documentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        Doc document = documentValidation.getFoundDocument();

        final ValidationResult collaboratorExistenceCheck = collaboratorValidation.checkCollaboratorExists(collaboratorId);
        if (collaboratorExistenceCheck.isInvalid()) {
            return collaboratorExistenceCheck;
        }

        collaborator = collaboratorValidation.getFoundCollaborator();

        final ValidationResult documentBelongingCheck = collaboratorValidation.checkCollaboratorBelongsToDocument(collaborator, documentId);
        if (documentBelongingCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("Collaborator '%s' does not belong to this document and thus cannot be removed.", collaborator.getUser().getName()));
        }

        // The document owner may remove all collaborators and single
        // users can unsubscribe from a document so that their collaboration
        // is removed as well.
        final ValidationResult ownerCheck = documentValidation.checkUserIsDocumentOwner(document, currentUser);
        final ValidationResult userEqualityCheck = collaboratorValidation.checkUserEqualsCollaborator(currentUser, collaborator);
        if (ownerCheck.isInvalid() && userEqualityCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document may remove collaborators. You can only unsubscribe yourself."));
        }

        return ValidationResult.success("Collaborator may be removed");
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }
}
