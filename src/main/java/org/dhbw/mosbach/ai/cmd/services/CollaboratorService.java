package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.response.Success;
import org.dhbw.mosbach.ai.cmd.services.response.Unauthorized;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.collaborator.CollaboratorInsertionValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.collaborator.CollaboratorRemovalValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.collaborator.CollaboratorValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.dhbw.mosbach.ai.cmd.util.HasAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The {@code CollaboratorService} provides a REST compliant endpoint implementation
 * for creation and removal of collaborators. A collaborator is a user that is
 * contributing to a document owned by another user. The services provided within this
 * endpoint allow the creation of new collaborations for a document and the removal
 * of a certain collaborator from a document.
 *
 * All incoming requests are accurately validated by self-reliant validation functions
 * residing in the package {@link org.dhbw.mosbach.ai.cmd.services.validation.collaborator}
 * and cause a corresponding response to be sent back to the client. Request and response
 * payloads are serialized and deserialized using JAX-RS annotations and their implementations
 * and are provided using special payload and response models. Based on the applied request
 * and the service conditions, the endpoint may return one of the following status codes:
 *
 * <ul>
 * <li>{@code 200 OK}: The request was processed successfully and the desired operation
 * was done.</li>
 * <li>{@code 400 Bad Request}: The request contained invalid fields or some conditions were
 * not met. The operation was aborted.</li>
 * <li>{@code 401 Unauthorized}: The client is not authorized to perform some operation
 * because he is not authenticated. He has to login first before proceeding.</li>
 * <li>{@code 403 Forbidden}: The client is not permitted to access a specific document.</li>
 * </ul>
 *
 * Both request and response are provided as JSON formatted fields.
 *
 * @author 6694964
 * @version 1.3
 * @see RestService
 * @see CollaboratorValidation
 * @see CollaboratorInsertionValidation
 * @see CollaboratorRemovalValidation
 */
@RequestScoped
@Path(ServiceEndpoints.PATH_COLLABORATOR)
public class CollaboratorService implements RestService {

    /**
     * Private logger instance for logging service operations
     */
    private static final Logger log = LoggerFactory.getLogger(CollaboratorService.class);

    /*
     * Injected fields for collaborator creation and removal in the database
     * and request validation
     */
    @Inject
    private CollaboratorDao collaboratorDao;

    @Inject
    private CollaboratorInsertionValidation collaboratorInsertionValidation;

    @Inject
    private CollaboratorRemovalValidation collaboratorRemovalValidation;

    /**
     * The session utility is used to enable and simplify access to the user session.
     */
    @Inject
    private SessionUtil sessionUtil;

    /**
     * Creates a new collaborator for a specific document by requiring the document id and the
     * username of the collaborator. The service investigates if the referenced document and user
     * exist and whether the requesting user has the right required to add a collaborator. That is
     * because only the owner is permitted to add new collaborators to his document. Moreover, the
     * owner cannot be added as collaborator himself.
     *
     * This operation can only be performed if the user is authenticated. An invocation without
     * valid session will result in {@code 401 Unauthorized}.
     *
     * @param model the request model containing the document id and the collaborator username
     * @return a {@code 200 OK} response if the collaborator could be added, otherwise
     * {@code 400 Bad Request} if the request contained invalid fields or the user has no
     * appropriate rights for this operation.
     */
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response addCollaborator(@NotNull CollaboratorInsertionModel model) {
        if (!sessionUtil.isLoggedIn()) {
            return new Unauthorized("You have to login to be able to add a collaborator.").buildResponse();
        }

        final ValidationResult collaboratorInsertionCheck = collaboratorInsertionValidation.validate(model);
        if (collaboratorInsertionCheck.isInvalid()) {
            return collaboratorInsertionCheck.buildResponse();
        }

        int documentId = model.getDocumentId();
        String collaboratorUsername = model.getCollaboratorName();

        Doc document = collaboratorInsertionValidation.getDocument();
        User collaborator = collaboratorInsertionValidation.getCollaborator();

        Collaborator newCollaborator = new Collaborator();
        newCollaborator.setDoc(document);
        newCollaborator.setUser(collaborator);
        newCollaborator.setHasAccess(HasAccess.Y);

        collaboratorDao.createCollaborator(newCollaborator);
        log.info("Created collaborator '{}' for document '{}'", collaboratorUsername, document.getName());

        return new Success("The collaborator '%s' was successfully added to your document.", collaboratorUsername).buildResponse();
    }

    /**
     * Removes a previously added collaborator from a document by means of indicating the
     * document id and collaborator id in the request. The service checks whether the document
     * and the collaborator referenced by their corresponding ids exist and belong together.
     * Moreover, the requesting user needs to be the owner to be able to remove a collaborator
     * from his document. Thus, it only authorizes the owner to remove collaborators from his
     * documents, but not from others.
     *
     * This operation can only be performed if the user is authenticated. An invocation without
     * valid session will result in {@code 401 Unauthorized}.
     *
     * @param model the request model containing the document id and collaborator id
     * @return a {@code 200 OK} response if the specified collaborator could be removed, otherwise
     * {@code 400 Bad Request} if the request contained invalid fields or the requesting user
     * has not the appropriate rights for this operation.
     */
    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response removeCollaborator(@NotNull CollaboratorRemovalModel model) {
        if (!sessionUtil.isLoggedIn()) {
            return new Unauthorized("You have to login to be able to remove a collaborator.").buildResponse();
        }

        final ValidationResult collaboratorRemovalCheck = collaboratorRemovalValidation.validate(model);
        if (collaboratorRemovalCheck.isInvalid()) {
            return collaboratorRemovalCheck.buildResponse();
        }

        Collaborator collaborator = collaboratorRemovalValidation.getCollaborator();

        collaboratorDao.removeCollaborator(collaborator);

        return new Success("The collaborator '%s' was successfully removed from your document.", collaborator.getUser().getName()).buildResponse();
    }
}
