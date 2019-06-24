package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentTransferModel;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.UserValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.collaborator.CollaboratorValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class DocumentTransferValidation implements ModelValidation<DocumentTransferModel> {

    @Inject
    private BasicFieldValidation basicFieldValidation;

    @Inject
    private DocumentValidation documentValidation;

    @Inject
    private UserValidation userValidation;

    @Inject
    private CollaboratorValidation collaboratorValidation;

    @Inject
    private SessionUtil sessionUtil;

    private Doc foundDocument;

    private User newOwner;

    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentTransferModel model) {
        int documentId = model.getDocumentId();
        String newOwnerName = model.getNewOwnerName();

        final ValidationResult ownerNameSpecifiedCheck = basicFieldValidation.checkSpecified(PayloadParameters.NEW_OWNER_NAME, newOwnerName);
        if (ownerNameSpecifiedCheck.isInvalid()) {
            return ownerNameSpecifiedCheck;
        }

        final ValidationResult documentExistenceCheck = documentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        foundDocument = documentValidation.getFoundDocument();

        User currentUser = sessionUtil.getUser();

        final ValidationResult ownerCheck = documentValidation.checkUserIsDocumentOwner(foundDocument, currentUser);
        if (ownerCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document can transfer his ownership."));
        }

        final ValidationResult userExistenceCheck = userValidation.checkUserExists(newOwnerName);
        if (userExistenceCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("User '%s' does not exist. Please choose a valid username.", newOwnerName));
        }

        newOwner = userValidation.getFoundUser();

        final ValidationResult documentCollaboratorCheck = collaboratorValidation.checkCollaboratorExists(newOwner, foundDocument);
        if (documentCollaboratorCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("User '%s' is not a collaborator. Ownership may be only transferred to a document collaborator.", newOwnerName));
        }

        return ValidationResult.success("Document ownership may be transferred.");
    }

    public Doc getFoundDocument() {
        return foundDocument;
    }

    public User getNewOwner() {
        return newOwner;
    }
}
