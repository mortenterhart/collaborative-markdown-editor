package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.HistoryDao;
import org.dhbw.mosbach.ai.cmd.db.RepoDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentAccessModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentTransferModel;
import org.dhbw.mosbach.ai.cmd.services.response.DocumentListResponse;
import org.dhbw.mosbach.ai.cmd.services.response.Forbidden;
import org.dhbw.mosbach.ai.cmd.services.response.Success;
import org.dhbw.mosbach.ai.cmd.services.response.Unauthorized;
import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentIcon;
import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentListEntity;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentAccessValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentInsertionValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentRemovalValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentTransferValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    private DocDao docDao;

    @Inject
    private RepoDao repoDao;

    @Inject
    private CollaboratorDao collaboratorDao;

    @Inject
    private SessionUtil sessionUtil;

    @Inject
    private DocumentValidation documentValidation;

    @Inject
    private DocumentInsertionValidation documentInsertionValidation;

    @Inject
    private DocumentRemovalValidation documentRemovalValidation;

    @Inject
    private DocumentAccessValidation documentAccessValidation;

    @Inject
    private DocumentTransferValidation documentTransferValidation;

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response addDocument(@NotNull DocumentInsertionModel insertionModel) {
        if (!sessionUtil.isLoggedIn()) {
            return new Unauthorized("You have to login to be able to create a new document.").buildResponse();
        }

        final ValidationResult documentInsertionCheck = documentInsertionValidation.validate(insertionModel);
        if (documentInsertionCheck.isInvalid()) {
            return documentInsertionCheck.buildResponse();
        }

        User currentUser = sessionUtil.getUser();

        Repo repository = repoDao.getRepo(currentUser);

        String documentName = insertionModel.getName();

        Doc document = new Doc();
        document.setCuser(currentUser);
        document.setUuser(currentUser);
        document.setRepo(repository);
        document.setName(documentName.trim());

        docDao.createDoc(document);
        log.info("Document '{}' was created for user '{}'", documentName, currentUser.getName());

        return new Success("Document was created successfully").buildResponse();
    }

    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response removeDocument(@NotNull DocumentRemovalModel removalModel) {
        if (!sessionUtil.isLoggedIn()) {
            return new Unauthorized("You have to login to be able to remove a document.").buildResponse();
        }

        final ValidationResult documentRemovalCheck = documentRemovalValidation.validate(removalModel);
        if (documentRemovalCheck.isInvalid()) {
            return documentRemovalCheck.buildResponse();
        }

        int documentId = removalModel.getDocumentId();
        Doc document = docDao.getDoc(documentId);

        docDao.removeDoc(document);
        for (Collaborator collaborator : collaboratorDao.getCollaboratorsForDoc(document)) {
            collaboratorDao.removeCollaborator(collaborator);
        }

        return new Success("Document was removed successfully").buildResponse();
    }

    @POST
    @Path("/hasAccess")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response hasDocumentAccess(@NotNull DocumentAccessModel accessModel) {
        if (!sessionUtil.isLoggedIn()) {
            return new Unauthorized("You have to login to have access to this document.").buildResponse();
        }

        final ValidationResult documentAccessCheck = documentAccessValidation.validate(accessModel);
        if (documentAccessCheck.isInvalid()) {
            return documentAccessCheck.buildResponse();
        }

        User currentUser = sessionUtil.getUser();

        Doc document = documentAccessValidation.getFoundDocument();

        boolean hasAccess = false;

        final ValidationResult ownerCheck = documentValidation.checkUserIsDocumentOwner(document, currentUser);
        if (ownerCheck.isValid()) {
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
        if (!sessionUtil.isLoggedIn()) {
            return new Unauthorized("You have to login to be able to fetch all documents.").buildResponse();
        }

        User currentUser = sessionUtil.getUser();

        List<Doc> ownedDocuments = docDao.getDocsOwnedBy(currentUser);
        List<Doc> collaboratorDocuments = docDao.getDocsCollaboratedBy(currentUser);

        List<DocumentListEntity> documentEntities = new ArrayList<>();

        if (ownedDocuments != null) {
            for (Doc document : ownedDocuments) {
                DocumentIcon listIcon = DocumentIcon.PERSON;
                List<Collaborator> collaborators = collaboratorDao.getCollaboratorsForDoc(document);

                if (collaborators != null && !collaborators.isEmpty()) {
                    listIcon = DocumentIcon.GROUP;
                }

                documentEntities.add(new DocumentListEntity(listIcon, document, collaborators));
            }
        }

        if (collaboratorDocuments != null) {
            for (Doc collaboratedDocument : collaboratorDocuments) {
                List<Collaborator> collaborators = collaboratorDao.getCollaboratorsForDoc(collaboratedDocument);

                documentEntities.add(new DocumentListEntity(DocumentIcon.GROUP, collaboratedDocument, collaborators));
            }
        }

        return new DocumentListResponse(documentEntities, "Documents loaded successfully").buildResponse();
    }

    @PATCH
    @Path("/transferOwnership")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response transferOwnership(@NotNull DocumentTransferModel transferModel) {
        if (!sessionUtil.isLoggedIn()) {
            return new Unauthorized("You have to login to be able to transfer an ownership.").buildResponse();
        }

        final ValidationResult transferOwnershipCheck = documentTransferValidation.validate(transferModel);
        if (transferOwnershipCheck.isInvalid()) {
            return transferOwnershipCheck.buildResponse();
        }

        String newOwnerName = transferModel.getNewOwnerName();

        Doc document = documentTransferValidation.getFoundDocument();
        User newOwner = documentTransferValidation.getNewOwner();

        Repo newRepo = repoDao.getRepo(newOwner);

        document.setRepo(newRepo);
        document.setUuser(newOwner);
        docDao.transferRepo(document);

        return new Success("Ownership was transferred to '%s' successfully", newOwnerName).buildResponse();
    }
}
