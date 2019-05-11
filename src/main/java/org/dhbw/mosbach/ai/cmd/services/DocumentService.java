package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.HistoryDao;
import org.dhbw.mosbach.ai.cmd.db.RepoDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.History;
import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.response.Forbidden;
import org.dhbw.mosbach.ai.cmd.response.Success;
import org.dhbw.mosbach.ai.cmd.response.Unauthorized;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentAccessModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentTransferModel;
import org.dhbw.mosbach.ai.cmd.services.response.DocumentListModel;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author 6694964
 */

@RequestScoped
@Path(ServiceEndpoints.PATH_DOCUMENT)
public class DocumentService implements RestService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    @Inject
    private UserDao userDao;

    @Inject
    private DocDao docDao;

    @Inject
    private RepoDao repoDao;

    @Inject
    private HistoryDao historyDao;

    @Inject
    private CollaboratorDao collaboratorDao;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response addDocument(@NotNull DocumentInsertionModel insertionModel) {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized("You have to login to be able to create a new document").buildResponse();
        }

        User currentUser = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        Repo repository = repoDao.getRepo(currentUser);

        String documentName = insertionModel.getName();

        Doc document = new Doc();
        document.setCuser(currentUser);
        document.setUuser(currentUser);
        document.setRepo(repository);
        document.setName(documentName);

        docDao.createDoc(document);

        return new Success("Document was created successfully").buildResponse();
    }

    @POST
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response removeDocument(@NotNull DocumentRemovalModel removalModel) {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized("You have to login to be able to remove a document").buildResponse();
        }

        User currentUser = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        int documentId = removalModel.getDocumentId();

        Doc document = docDao.getDoc(documentId);
        if (document == null) {
            return new BadRequest(String.format("Document %d does not exist", documentId)).buildResponse();
        }

        if (!currentUser.equals(document.getRepo().getOwner())) {
            return new BadRequest("You are unauthorized. Only the owner of this document may remove it.").buildResponse();
        }

        docDao.removeDoc(document);

        return new Success("Document was removed successfully").buildResponse();
    }

    @GET
    @Path("/hasAccess")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response hasDocumentAccess(@NotNull DocumentAccessModel accessModel) {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized("You have to login to have access to this document").buildResponse();
        }

        User currentUser = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        int documentId = accessModel.getDocumentId();

        Doc document = docDao.getDoc(documentId);
        if (document == null) {
            return new BadRequest(String.format("Document %d does not exist", documentId)).buildResponse();
        }

        boolean hasAccess = false;

        if (currentUser.equals(document.getRepo().getOwner())) {
            hasAccess = true;
        }

        List<Collaborator> collaborators = collaboratorDao.getCollaboratorsForDoc(document);
        ListIterator<Collaborator> iterator = collaborators.listIterator();
        while (iterator.hasNext() && !hasAccess) {
            Collaborator c = iterator.next();

            hasAccess = c.getUser().equals(currentUser);
        }

        if (!hasAccess) {
            return new Forbidden("You do not have the permission to access this document").buildResponse();
        }

        return new Success("You have granted access to this document").buildResponse();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response getAllDocuments() {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized("You have to login to be able to fetch all documents").buildResponse();
        }

        User currentUser = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        List<Doc> ownerDocs = docDao.getDocsOwnedBy(currentUser);
        List<Doc> collaboratorDocs = docDao.getDocsCollaboratedBy(currentUser);

        List<DocumentListModel> models = new ArrayList<>();
        for (Doc doc : ownerDocs) {
            List<History> history = historyDao.getFullHistoryForDoc(doc);
            List<Collaborator> collaborators = collaboratorDao.getCollaboratorsForDoc(doc);
            String icon = "person";

            if (collaborators != null && !collaborators.isEmpty()) {
                icon = "group";
            }

            models.add(new DocumentListModel(icon, doc, history, collaborators));
        }

        if (collaboratorDocs != null) {
            for (Doc collabDoc : collaboratorDocs) {
                List<History> history = historyDao.getFullHistoryForDoc(collabDoc);
                List<Collaborator> collaborators = collaboratorDao.getCollaboratorsForDoc(collabDoc);

                models.add(new DocumentListModel("group", collabDoc, history, collaborators));
            }
        }

        return Response.ok().entity(models).build();
    }

    @POST
    @Path("/transferOwnership")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response transferOwnership(@NotNull DocumentTransferModel transferModel) {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized("You have to login to be able to transfer an ownership").buildResponse();
        }

        User currentUser = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        int documentId = transferModel.getDocumentId();
        String newOwnerName = transferModel.getNewOwnerName();

        Doc document = docDao.getDoc(documentId);
        if (document == null) {
            return new BadRequest(String.format("Document %d does not exist", documentId)).buildResponse();
        }

        if (!currentUser.equals(document.getRepo().getOwner())) {
            return new BadRequest("You are unauthorized. Only the owner of this document can transfer his ownership.").buildResponse();
        }

        User newOwner = userDao.getUserByName(newOwnerName);
        if (newOwner == null) {
            return new BadRequest(String.format("User '%s' does not exist", newOwnerName)).buildResponse();
        }

        Repo newRepo = repoDao.getRepo(newOwner);

        document.setRepo(newRepo);
        document.setUuser(newOwner);
        docDao.transferRepo(document);

        return new Success(String.format("Ownership was transferred to '%s' successfully", newOwnerName)).buildResponse();
    }
}
