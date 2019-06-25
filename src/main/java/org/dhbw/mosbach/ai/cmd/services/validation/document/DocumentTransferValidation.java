package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentTransferModel;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.BasicUserValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.collaborator.BasicCollaboratorValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class DocumentTransferValidation implements ModelValidation<DocumentTransferModel> {

    @Inject
    private BasicFieldValidation basicFieldValidation;

    @Inject
    private BasicDocumentValidation basicDocumentValidation;

    @Inject
    private BasicUserValidation basicUserValidation;

    @Inject
    private BasicCollaboratorValidation basicCollaboratorValidation;

    @Inject
    private SessionUtil sessionUtil;

    private Doc foundDocument;

    private User newOwner;

    /**
     * Checks the passed payload of the respective document transfer model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for specification of the name of the new owner</li>
     * <li>check for existence of documented pointed to by the document id</li>
     * <li>check if the current user is the owner of the document</li>
     * <li>check for the existence of the new owner designated by the new owner name</li>
     * <li>check if the new owner is an active collaborator of the document.</li>
     * </ul>
     *
     * The {@code validate} method follows the principle of error handling descending from
     * the Go programming language. Accordingly, a potential error in the validation is
     * returned as an unsuccessful {@link ValidationResult} using a sufficient error message
     * rather than throwing an exception which requires additional mappers in the services
     * to handle those exceptions. A successful validation is returned in the same manner
     * as a successful validation result.
     *
     * In any case, the method should accept a non-null payload and return a non-null
     * validation result. In case the payload should be {@code null} this method should
     * answer with an unsuccessful validation result containing an internal server error
     * response.
     *
     * @param model the provided non-null document insertion model from the document
     *              service
     * @return a non-null validation result indicating if the document insertion
     * validation was successful or not
     */
    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentTransferModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("DocumentTransferModel is null"));
        }

        final int documentId = model.getDocumentId();
        final String newOwnerName = model.getNewOwnerName();

        final ValidationResult ownerNameSpecifiedCheck = basicFieldValidation.checkSpecified(PayloadParameters.NEW_OWNER_NAME, newOwnerName);
        if (ownerNameSpecifiedCheck.isInvalid()) {
            return ownerNameSpecifiedCheck;
        }

        final ValidationResult documentExistenceCheck = basicDocumentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        foundDocument = basicDocumentValidation.getFoundDocument();

        User currentUser = sessionUtil.getUser();

        final ValidationResult ownerCheck = basicDocumentValidation.checkUserIsDocumentOwner(foundDocument, currentUser);
        if (ownerCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document can transfer his ownership."));
        }

        final ValidationResult userExistenceCheck = basicUserValidation.checkUserExists(newOwnerName);
        if (userExistenceCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("User '%s' does not exist. Please choose a valid username.", newOwnerName));
        }

        newOwner = basicUserValidation.getFoundUser();

        final ValidationResult documentCollaboratorCheck = basicCollaboratorValidation.checkCollaboratorExists(newOwner, foundDocument);
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
