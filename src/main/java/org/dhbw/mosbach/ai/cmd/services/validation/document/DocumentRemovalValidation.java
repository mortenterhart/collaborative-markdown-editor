package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class DocumentRemovalValidation implements ModelValidation<DocumentRemovalModel> {

    @Inject
    private DocumentValidation documentValidation;

    @Inject
    private SessionUtil sessionUtil;

    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentRemovalModel model) {
        int documentId = model.getDocumentId();

        final ValidationResult documentExistenceCheck = documentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        Doc document = documentValidation.getFoundDocument();
        User currentUser = sessionUtil.getUser();

        final ValidationResult ownerCheck = documentValidation.checkUserIsDocumentOwner(document, currentUser);
        if (ownerCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document may remove it."));
        }

        return ValidationResult.success("Document may be removed");
    }
}
