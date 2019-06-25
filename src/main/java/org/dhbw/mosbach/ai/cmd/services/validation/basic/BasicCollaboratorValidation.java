package org.dhbw.mosbach.ai.cmd.services.validation.basic;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * The {@code BasicCollaboratorValidation} provides a basic validation technique for
 * operations requiring access to collaborator models. The validation methods include
 * the check for the existence of single collaborators, either by collaborator id or
 * by applying the user and document objects, the check for equality of a user to a
 * collaborator referencing a user as well as a check to find out whether a collaborator
 * belongs to a certain document.
 *
 * Considering that loading collaborators from the database is an expensive operation
 * the loaded collaborator is cached within the {@code foundCollaborator} attribute.
 * This way the service calling validation methods contained in this class does not
 * need to load the collaborator again, but can retrieve the cached collaborator via
 * the getter method {@link BasicCollaboratorValidation#getFoundCollaborator()}.
 * Accordingly, there is no duplication of database operations increasing performance.
 *
 * If the cached collaborator is accessed via the mentioned getter method the cache
 * attribute is set back to {@code null} to clear the cache for next checks and to
 * signalize other services using these validation methods that there has been no
 * collaborator yet that was loaded from the database.
 *
 * @author 6694964
 * @version 1.1
 *
 * @see CollaboratorDao
 * @see org.dhbw.mosbach.ai.cmd.services.CollaboratorService
 */
@RequestScoped
public class BasicCollaboratorValidation {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(BasicCollaboratorValidation.class);

    /**
     * Injected field for loading collaborators from the database
     */
    @Inject
    private CollaboratorDao collaboratorDao;

    /**
     * Cached instance of the last loaded collaborator to be conveyed by
     * a service in order to reduce database operations. Once accessed,
     * this collaborator attribute is cleared back to {@code null}.
     */
    private Collaborator foundCollaborator;

    /**
     * Examines if the collaborator with the applied collaborator id exists
     * in the database by using database lookup functionalities. If the
     * referenced collaborator exists the loaded collaborator is cached within
     * the {@code foundCollaborator} attribute and a successful validation
     * result is returned.
     *
     * If the collaborator pointed to by the collaborator id does not exist
     * the method returns an unsuccessful validation result with a {@code
     * 400 Bad Request} response.
     *
     * @param collaboratorId the id of the collaborator to be checked for existence
     * @return a successful validation result if the collaborator exists, otherwise
     * an unsuccessful validation result.
     */
    @NotNull
    public ValidationResult checkCollaboratorExists(int collaboratorId) {
        Collaborator collaborator = collaboratorDao.getCollaborator(collaboratorId);
        if (collaborator == null) {
            return ValidationResult.response(new BadRequest("Collaborator '%d' does not exist.", collaboratorId));
        }

        foundCollaborator = collaborator;
        return ValidationResult.success("Collaborator '%d' exists.", collaboratorId);
    }

    /**
     * Examines if the collaborator for the applied user and document exists
     * in the database by using database lookup functionalities. If the
     * referenced collaborator exists the loaded collaborator is cached within
     * the {@code foundCollaborator} attribute and a successful validation
     * result is returned.
     *
     * If the collaborator pointed to by the user and document does not exist
     * the method returns an unsuccessful validation result with a {@code
     * 400 Bad Request} response.
     *
     * @param user     the user referenced by the collaborator
     * @param document the document that the collaborator may belong to
     * @return a successful validation result if the collaborator exists, otherwise
     * an unsuccessful validation result.
     */
    @NotNull
    public ValidationResult checkCollaboratorExists(User user, Doc document) {
        Collaborator collaborator = collaboratorDao.getCollaborator(user, document);
        if (collaborator == null) {
            return ValidationResult.response(new BadRequest("Collaborator '%s' does not exist for the document '%d'.", user.getName(), document.getId()));
        }

        foundCollaborator = collaborator;
        return ValidationResult.success("Collaborator '%s' exists for document '%d'.", user.getName(), document.getId());
    }

    /**
     * Examines if the user is equal to the user referenced by a specific
     * collaborator. A collaborator always points to a certain user who
     * is then allowed to edit on a specific document
     *
     * @param user         the user
     * @param collaborator the collaborator that is checked for equality
     * @return a successful validation result if the user is equal to the
     * collaborator, otherwise an unsuccessful validation result.
     */
    @NotNull
    public ValidationResult checkUserEqualsCollaborator(User user, Collaborator collaborator) {
        if (user.getId() != collaborator.getUser().getId()) {
            return ValidationResult.response(new BadRequest("Current user is not this collaborator"));
        }

        return ValidationResult.success("Current user is equal to this collaborator");
    }

    /**
     * Examines if a collaborator belongs to the membership of a specific document pointed
     * to by the document id. If the collaborator belongs to the document this method
     * returns a successful validation result, otherwise an unsuccessful one with
     * a response of {@code 400 Bad Request}.
     *
     * @param collaborator the collaborator
     * @param documentId the document to be checked for membership
     * @return a successful validation result if the collaborator belongs to the document,
     * otherwise an unsuccessful validation result.
     */
    @NotNull
    public ValidationResult checkCollaboratorBelongsToDocument(Collaborator collaborator, int documentId) {
        if (documentId != collaborator.getDoc().getId()) {
            return ValidationResult.response(new BadRequest("Collaborator '%s' does not belong to document '%d'", collaborator.getUser().getName(), documentId));
        }

        return ValidationResult.success("Collaborator '%s' is attached to document '%d'", collaborator.getUser().getName(), documentId);
    }

    /**
     * Retrieves the cached instance of the found collaborator loaded by the methods
     * checking the existence of a collaborator. If one of those methods has found
     * a corresponding collaborator this getter method returns the instance for this
     * collaborator and clears the {@code foundCollaborator} attribute back to null.
     * The method returns null in case no collaborator was found or the method was
     * not executed before.
     *
     * @return the cached instance of the collaborator or null if the collaborator
     * was not found or the method for checking the existence of a collaborator was
     * not executed previously.
     */
    public Collaborator getFoundCollaborator() {
        Collaborator collaborator = foundCollaborator;
        foundCollaborator = null;
        return collaborator;
    }
}
