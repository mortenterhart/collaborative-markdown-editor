package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.RepoDao;
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
import org.dhbw.mosbach.ai.cmd.services.validation.document.*;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * The {@code DocumentService} provides a REST compliant endpoint implementation
 * for creation, manipulation and retrieval of documents. A document is a shared
 * resource among multiple users which are able to edit the contents in the editor.
 * The document has a modifiable state which is kept consistent between the users
 * using the Websocket endpoint implementation in {@link org.dhbw.mosbach.ai.cmd.websocket.Endpoint}.
 *
 * All incoming requests are accurately validated by self-reliant validation functions
 * residing in the package {@link org.dhbw.mosbach.ai.cmd.services.validation.document}
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
 *
 * @see RestEndpoint
 * @see BasicDocumentValidation
 * @see DocumentInsertionValidation
 * @see DocumentRemovalValidation
 * @see DocumentAccessValidation
 */
@RequestScoped
@Path(ServiceEndpoints.PATH_DOCUMENT)
public class DocumentService extends RootService implements RestEndpoint {

    /**
     * Private logger instance for logging important service operations
     */
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    /*
     * Injected fields for creation and manipulation of documents in the database
     * and request validation
     */
    @Inject
    private DocDao docDao;

    @Inject
    private RepoDao repoDao;

    @Inject
    private CollaboratorDao collaboratorDao;

    @Inject
    private BasicDocumentValidation basicDocumentValidation;

    @Inject
    private DocumentInsertionValidation documentInsertionValidation;

    @Inject
    private DocumentRemovalValidation documentRemovalValidation;

    @Inject
    private DocumentAccessValidation documentAccessValidation;

    @Inject
    private DocumentTransferValidation documentTransferValidation;

    /**
     * The session utility is used to enable and simplify access to the user session.
     */
    @Inject
    private SessionUtil sessionUtil;

    /**
     * Creates a new document in the repository of the currently logged in user by specifying
     * the name of the new document inside the request model. It is examined if the document name
     * conforms to the naming requirements for documents which can be further explored in
     * {@link DocumentInsertionValidation}. In short, the name needs to consist of printable
     * characters not to be empty and may only contain a small subset of common characters. All
     * other combinations will result in a {@code 400 Bad Request} status to be returned.
     *
     * On the other hand, the name of the new document has to be unique inside the repository.
     * A document with a duplicated name is not permitted to be created.
     *
     * This operation can only be performed if the user is authenticated. An invocation without
     * valid session will result in {@code 401 Unauthorized}.
     *
     * @param insertionModel the request model containing the name of the new document
     * @return a {@code 200 OK} response if the document was created, otherwise
     */
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

    /**
     * Removes a document from the user's repository by supplying the id of the document to
     * be removed. The service checks if the id designating the document exists and whether
     * the requesting user has the appropriate right to remove this document. Only the owner
     * may remove his document. Other users requesting the removal will be denied. Next to
     * the document all collaborators attached to that document will also be removed.
     *
     * This operation can only be performed if the user is authenticated. An invocation without
     * valid session will result in {@code 401 Unauthorized}.
     *
     * @param removalModel the request model containing the id of the document to be removed
     * @return a {@code 200 OK} response if the document was removed, otherwise
     * {@code 400 Bad Request} if the document does not exist or the user is not authorized
     * to remove the document.
     */
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
        log.info("Removed document '{}' from repository of user '{}'", documentId, sessionUtil.getUser().getName());
        for (Collaborator collaborator : collaboratorDao.getCollaboratorsForDoc(document)) {
            collaboratorDao.removeCollaborator(collaborator);
            log.info("Removed collaborator '{}' from document '{}'", collaborator.getId(), documentId);
        }

        return new Success("Document was removed successfully").buildResponse();
    }

    /**
     * Investigates if the requesting user has the right to access a specific document referenced
     * by a supplied document id. The validation includes the check for the existence of the document.
     *
     * The user is granted access to the document if he is the owner or a named collaborator of the
     * document. Users which are outside of this group will be refused to access the document by
     * a {@code 403 Forbidden} response status.
     *
     * This operation can only be performed if the user is authenticated. An invocation without
     * valid session will result in {@code 401 Unauthorized}.
     *
     * @param accessModel the requesting model containing the document id to be checked for access
     * @return a {@code 200 OK} response if the user is permitted to access the document, otherwise
     * a {@code 403 Forbidden} response if the user is not allowed.
     */
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

        final ValidationResult ownerCheck = basicDocumentValidation.checkUserIsDocumentOwner(document, currentUser);
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

    /**
     * Retrieves all documents which are owned or collaborated by the current user. The response
     * includes a message and the HTTP status, as every other service too, but also an entire list
     * of document list entities. A document list entity is a response model specifically designed
     * for this service and summarizes the document metadata and content, the list of collaborators
     * for a document as well as an icon used inside the frontend to distinguish documents with
     * a single contributor from those with multiple collaborators. For more information, consult
     * the class {@link DocumentListEntity}.
     *
     * This service may probably return a large response with partly redundant information such as
     * duplicated users in document creation users and repository owners. Therefore it is advised
     * not to invoke this service too often in order to reduce overhead and performance decrease.
     *
     * The information provided in the response of this service are formatted according to some
     * default constraints. Each date field such as creation or update times are formatted as
     * ISO 8601 date strings. Unlike the enum used in the model, the {@code hasAccess} field of
     * collaborators is supplied as boolean value. Sensitive information such as user passwords
     * are omitted in the response as well as unused information like the document content.
     *
     * This operation can only be performed if the user is authenticated. An invocation without
     * valid session will result in {@code 401 Unauthorized}.
     *
     * @return a {@code 200 OK} response with all documents if the user is authenticated, otherwise
     * a {@code 401 Unauthorized} because the user needs to authenticate.
     */
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

    /**
     * Transfers the ownership of a certain document to another user by requiring the document
     * id and the name of the new owner. The validation includes checks for the existence of
     * the document and the user designated by the supplied username. Furthermore, it is checked
     * whether the requesting user is the current owner of the document as only he preserves the
     * privileged right to transfer his ownership to another user. Also, he can only transfer
     * the ownership to a user who is an active collaborator of the document.
     *
     * This operation can only be performed if the user is authenticated. An invocation without
     * valid session will result in {@code 401 Unauthorized}.
     *
     * @param transferModel the request model containing the document id and the name of the
     *                      new owner
     * @return a {@code 200 OK} response if the ownership was transferred, otherwise a
     * {@code 400 Bad Request} if the
     */
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
