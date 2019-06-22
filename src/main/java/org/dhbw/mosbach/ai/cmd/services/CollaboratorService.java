package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.response.Success;
import org.dhbw.mosbach.ai.cmd.response.Unauthorized;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorRemovalModel;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.HasAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path(ServiceEndpoints.PATH_COLLABORATOR)
public class CollaboratorService implements RestService {

    private static final Logger log = LoggerFactory.getLogger(CollaboratorService.class);

    @Inject
    private DocDao docDao;

    @Inject
    private UserDao userDao;

    @Inject
    private CollaboratorDao collaboratorDao;

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
        if (!sessionUtil.isLoggedIn(request)) {
            return new Unauthorized("You have to login to be able to add a collaborator.").buildResponse();
        }

        int documentId = model.getDocumentId();
        String collaboratorUsername = model.getCollaboratorName();

        User currentUser = sessionUtil.getUser(request);

        Doc document = docDao.getDoc(documentId);
        if (document == null) {
            return new BadRequest("Document with id '%d' does not exist.", documentId).buildResponse();
        }

        User collaborator = userDao.getUserByName(collaboratorUsername);
        if (collaborator == null) {
            return new BadRequest("User '%s' does not exist. Please choose a valid username.", collaboratorUsername).buildResponse();
        }

        if (currentUser.getId() != document.getRepo().getOwner().getId()) {
            return new BadRequest("You are unauthorized. Only the owner of this document may add collaborators.").buildResponse();
        }

        if (collaborator.getId() == currentUser.getId()) {
            return new BadRequest("The owner cannot be added as collaborator.").buildResponse();
        }

        if (collaboratorDao.getCollaborator(collaborator, document) != null) {
            return new BadRequest("Collaborator '%s' was already added to this document.", collaboratorUsername).buildResponse();
        }

        Collaborator newCollaborator = new Collaborator();
        newCollaborator.setDoc(document);
        newCollaborator.setUser(collaborator);
        newCollaborator.setHasAccess(HasAccess.Y);

        collaboratorDao.createCollaborator(newCollaborator);

        return new Success("The collaborator '%s' was successfully added to your document", collaboratorUsername).buildResponse();
    }

    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response removeCollaborator(@NotNull CollaboratorRemovalModel model) {
        if (!sessionUtil.isLoggedIn(request)) {
            return new Unauthorized("You have to login to be able to remove a collaborator.").buildResponse();
        }

        User currentUser = sessionUtil.getUser(request);

        int documentId = model.getDocumentId();
        int collaboratorId = model.getCollaboratorId();

        Doc document = docDao.getDoc(documentId);
        if (document == null) {
            return new BadRequest("Document with id '%d' does not exist.", documentId).buildResponse();
        }

        Collaborator collaborator = collaboratorDao.getCollaborator(collaboratorId);
        if (collaborator == null) {
            return new BadRequest("Collaborator '%d' does not exist.", collaboratorId).buildResponse();
        }

        if (documentId != collaborator.getDoc().getId()) {
            return new BadRequest("Collaborator '%s' does not belong to this document and thus cannot be removed.", collaborator.getUser().getName()).buildResponse();
        }

        if (currentUser.getId() != document.getRepo().getOwner().getId() && currentUser.getId() != collaborator.getUser().getId()) {
            return new BadRequest("You are unauthorized. Only the owner of this document may remove collaborators.").buildResponse();
        }

        collaboratorDao.removeCollaborator(collaborator);

        return new Success("The collaborator '%s' was successfully removed from your document.", collaborator.getUser().getName()).buildResponse();
    }
}
