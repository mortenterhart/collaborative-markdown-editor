package org.dhbw.mosbach.ai.cmd.services.validation.collaborator;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.BasicUserValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.document.BasicDocumentValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class CollaboratorInsertionValidation implements ModelValidation<CollaboratorInsertionModel> {

    @Inject
    private BasicFieldValidation basicFieldValidation;

    @Inject
    private BasicDocumentValidation basicDocumentValidation;

    @Inject
    private BasicCollaboratorValidation basicCollaboratorValidation;

    @Inject
    private BasicUserValidation basicUserValidation;

    @Inject
    private SessionUtil sessionUtil;

    private Doc document;
    private User collaborator;

    /**
     * Checks the passed payload of the respective collaborator insertion model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for specification of the username of the collaborator that is to be added</li>
     * <li>check for the existence of the document pointed to by the document id</li>
     * <li>check for the existence of the user designated by the collaborator username</li>
     * <li>check if the current user is the document owner</li>
     * <li>check if the collaborator to be added is not the same user as the document owner</li>
     * <li>check if the collaborator does not exist on the document yet.</li>
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
     * @param model the provided non-null collaborator insertion model from the collaborator
     *              service
     * @return a non-null validation result indicating if the collaborator insertion
     * validation was successful or not
     */
    @Override
    @NotNull
    public ValidationResult validate(@NotNull CollaboratorInsertionModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("CollaboratorInsertionModel is null"));
        }

        final int documentId = model.getDocumentId();
        final String collaboratorUsername = model.getCollaboratorName();

        ValidationResult collaboratorUsernameSpecifiedCheck = basicFieldValidation.checkSpecified(PayloadParameters.COLLABORATOR_USERNAME, collaboratorUsername);
        if (collaboratorUsernameSpecifiedCheck.isInvalid()) {
            return collaboratorUsernameSpecifiedCheck;
        }

        User currentUser = sessionUtil.getUser();

        final ValidationResult documentExistenceCheck = basicDocumentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        document = basicDocumentValidation.getFoundDocument();

        final ValidationResult userExistenceCheck = basicUserValidation.checkUserExists(collaboratorUsername);
        if (userExistenceCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("User '%s' does not exist. Please choose a valid username.", collaboratorUsername));
        }

        collaborator = basicUserValidation.getFoundUser();

        final ValidationResult ownerCheck = basicDocumentValidation.checkUserIsDocumentOwner(document, currentUser);
        if (ownerCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document may add collaborators."));
        }

        if (collaborator.getId() == currentUser.getId()) {
            return ValidationResult.response(new BadRequest("The owner cannot be added as collaborator."));
        }

        final ValidationResult collaboratorExistenceCheck = basicCollaboratorValidation.checkCollaboratorExists(collaborator, document);
        if (collaboratorExistenceCheck.isValid()) {
            return ValidationResult.response(new BadRequest("Collaborator '%s' was already added to this document.", collaboratorUsername));
        }

        return ValidationResult.success("Collaborator may be added to the document");
    }

    public Doc getDocument() {
        return document;
    }

    public User getCollaborator() {
        return collaborator;
    }
}
