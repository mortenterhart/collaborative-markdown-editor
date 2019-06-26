package org.dhbw.mosbach.ai.cmd.services.validation.collaborator;

import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicCollaboratorValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicDocumentValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * @author 6694964
 * @version 1.1
 */
@RequestScoped
public class CollaboratorRemovalValidation implements ModelValidation<CollaboratorRemovalModel> {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(CollaboratorRemovalValidation.class);

    @Inject
    private SessionUtil sessionUtil;

    @Inject
    private BasicDocumentValidation basicDocumentValidation;

    @Inject
    private BasicCollaboratorValidation basicCollaboratorValidation;

    /**
     * Cached instance of the found collaborator
     */
    private Collaborator collaborator;

    /**
     * Checks the passed payload of the respective collaborator removal model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for the existence of the document referenced by the document id</li>
     * <li>check for the existence of the collaborator referenced by the collaborator id</li>
     * <li>check if the collaborator belongs to the specified document</li>
     * <li>check if the current user is either the owner of the document or the collaborator
     * that should be removed (collaborators can unsubscribe from documents).</li>
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
     * @param model the provided non-null collaborator removal model from the collaborator
     *              service
     * @return a non-null validation result indicating if the collaborator removal
     * validation was successful or not
     * @see ModelValidation#validate(Payload)
     * @see org.dhbw.mosbach.ai.cmd.services.CollaboratorService
     */
    @Override
    @NotNull
    public ValidationResult validate(@NotNull CollaboratorRemovalModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("CollaboratorRemovalModel is null"));
        }

        final int documentId = model.getDocumentId();
        final int collaboratorId = model.getCollaboratorId();

        final ValidationResult documentExistenceCheck = basicDocumentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        Doc document = basicDocumentValidation.getFoundDocument();

        final ValidationResult collaboratorExistenceCheck = basicCollaboratorValidation.checkCollaboratorExists(collaboratorId);
        if (collaboratorExistenceCheck.isInvalid()) {
            return collaboratorExistenceCheck;
        }

        collaborator = basicCollaboratorValidation.getFoundCollaborator();

        final ValidationResult documentBelongingCheck = basicCollaboratorValidation.checkCollaboratorBelongsToDocument(collaborator, documentId);
        if (documentBelongingCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("Collaborator '%s' does not belong to this document and thus cannot be removed.", collaborator.getUser().getName()));
        }

        User currentUser = sessionUtil.getUser();

        // The document owner may remove all collaborators and single
        // users can unsubscribe from a document so that their collaboration
        // is removed as well.
        final ValidationResult ownerCheck = basicDocumentValidation.checkUserIsDocumentOwner(document, currentUser);
        final ValidationResult userEqualityCheck = basicCollaboratorValidation.checkUserEqualsCollaborator(currentUser, collaborator);
        if (ownerCheck.isInvalid() && userEqualityCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document may remove collaborators. You can only unsubscribe yourself."));
        }

        return ValidationResult.success("Collaborator may be removed");
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }
}
