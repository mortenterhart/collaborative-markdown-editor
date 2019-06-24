package org.dhbw.mosbach.ai.cmd.services.validation.collaborator;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.UserValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class CollaboratorInsertionValidation implements ModelValidation<CollaboratorInsertionModel> {

    @Inject
    private DocDao docDao;

    @Inject
    private CollaboratorDao collaboratorDao;

    @Inject
    private UserDao userDao;

    @Inject
    private BasicFieldValidation basicFieldValidation;

    @Inject
    private DocumentValidation documentValidation;

    @Inject
    private CollaboratorValidation collaboratorValidation;

    @Inject
    private UserValidation userValidation;

    @Inject
    private SessionUtil sessionUtil;

    private Doc document;
    private User collaborator;

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

        final ValidationResult documentExistenceCheck = documentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        document = documentValidation.getFoundDocument();

        final ValidationResult userExistenceCheck = userValidation.checkUserExists(collaboratorUsername);
        if (userExistenceCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("User '%s' does not exist. Please choose a valid username.", collaboratorUsername));
        }

        collaborator = userValidation.getFoundUser();

        final ValidationResult ownerCheck = documentValidation.checkUserIsDocumentOwner(document, currentUser);
        if (ownerCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document may add collaborators."));
        }

        if (collaborator.getId() == currentUser.getId()) {
            return new ValidationResult(new BadRequest("The owner cannot be added as collaborator."));
        }

        final ValidationResult collaboratorExistenceCheck = collaboratorValidation.checkCollaboratorExists(collaborator, document);
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
