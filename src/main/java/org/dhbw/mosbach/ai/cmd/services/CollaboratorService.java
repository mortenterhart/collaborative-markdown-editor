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
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.dhbw.mosbach.ai.cmd.util.HasAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path(ServiceEndpoints.PATH_COLLABORATOR)
public class CollaboratorService implements RestService {

    private static final Logger log = LoggerFactory.getLogger(CollaboratorService.class);

    @Inject
    private CollaboratorDao collaboratorDao;

    @Inject
    private CollaboratorInsertionValidation collaboratorInsertionValidation;

    @Inject
    private CollaboratorRemovalValidation collaboratorRemovalValidation;

    @Inject
    private SessionUtil sessionUtil;

    @Context
    private HttpServletRequest request;

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

        return new Success("The collaborator '%s' was successfully added to your document.", collaboratorUsername).buildResponse();
    }

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
