package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.RepoDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.Success;
import org.dhbw.mosbach.ai.cmd.response.Unauthorized;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

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
import java.time.LocalDateTime;

@ApplicationScoped
@Path(ServiceEndpoints.PATH_DOCUMENT)
public class DocumentService {

    @Inject
    private DocDao docDao;

    @Inject
    private RepoDao repoDao;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDocument(String documentName) {
        if (request.getSession().getAttribute(CmdConfig.SESSION_IS_LOGGED_IN) == null) {
            return new Unauthorized().create("You have to login to be able to create a new document");
        }

        User user = (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);

        Repo repository = repoDao.getRepo(user);

        Doc document = new Doc();
        document.setCuser(user);
        document.setRepo(repository);
        document.setCtime(LocalDateTime.now());
        document.setName(documentName);

        docDao.createDoc(document);

        return new Success().create("Document was created successfully");
    }
}
