package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.HistoryDao;
import org.dhbw.mosbach.ai.cmd.db.RepoDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.History;
import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.Success;
import org.dhbw.mosbach.ai.cmd.response.Unauthorized;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentModel;
import org.dhbw.mosbach.ai.cmd.services.response.DocumentListModel;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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

/**
 * @author 6694964
 */

@ApplicationScoped
@Path(ServiceEndpoints.PATH_DOCUMENT)
public class DocumentService {

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
    public Response addDocument(DocumentModel documentModel) {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized().create("You have to login to be able to create a new document");
        }

        User user = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        Repo repository = repoDao.getRepo(user);

        String documentName = documentModel.getName();

        Doc document = new Doc();
        document.setCuser(user);
        document.setUuser(user);
        document.setRepo(repository);
        document.setName(documentName);

        docDao.createDoc(document);

        return new Success().create("Document was created successfully");
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDocuments() {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized().create("You have to login to be able to fetch all documents");
        }

        User sessionUser = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        List<Doc> ownerDocs = docDao.getDocsOwnedBy(sessionUser);
        List<Doc> collaboratorDocs = docDao.getDocsCollaboratedBy(sessionUser);

        List<DocumentListModel> models = new ArrayList<>();
        for (Doc doc : ownerDocs) {
            List<History> history = historyDao.getFullHistoryForDoc(doc);
            List<Collaborator> collaborators = collaboratorDao.getCollaboratorsForDoc(doc);
            String icon = "person";

            if (!collaborators.isEmpty()) {
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
}
