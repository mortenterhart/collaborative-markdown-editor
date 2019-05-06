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
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorModel;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.HasAccess;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path(ServiceEndpoints.PATH_COLLABORATOR)
public class CollaboratorService {

    @Inject
    private DocDao docDao;

    @Inject
    private UserDao userDao;

    @Inject
    private CollaboratorDao collaboratorDao;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCollaborator(CollaboratorModel model) {
        if (request.getSession().getAttribute(CmdConfig.SESSION_USER) == null) {
            return new Unauthorized("You have to login to be able to add a collaborator").buildResponse();
        }

        int documentId = model.getDocumentId();
        String collaboratorUsername = model.getCollaboratorName();

        User owner = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        Doc document = docDao.getDoc(documentId);
        if (document == null) {
            return new BadRequest(String.format("Document with id '%d' does not exist", documentId)).buildResponse();
        }

        User collaborator = userDao.getUserByName(collaboratorUsername);
        if (collaborator == null) {
            return new BadRequest(String.format("User '%s' does not exist. Please choose a valid username.", collaboratorUsername)).buildResponse();
        }

        if (owner.getId() != document.getCuser().getId()) {
            return new BadRequest("You are unauthorized. Only the owner of this document may add collaborators.").buildResponse();
        }

        if (collaboratorDao.getCollaborator(collaborator, document) != null) {
            return new BadRequest(String.format("Collaborator '%s' was already added to this document", collaboratorUsername)).buildResponse();
        }

        Collaborator newCollaborator = new Collaborator();
        newCollaborator.setDoc(document);
        newCollaborator.setUser(collaborator);
        newCollaborator.setHasAccess(HasAccess.Y);

        collaboratorDao.createCollaborator(newCollaborator);

        return new Success(String.format("The collaborator '%s' was successfully added to your document", collaboratorUsername)).buildResponse();
    }
}
