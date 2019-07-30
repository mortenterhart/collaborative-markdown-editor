package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicDocumentValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * The {@code DocumentRemovalValidation} provides a validation to check an incoming
 * user request with a provided document removal model for the existence of the
 * document pointed to by a supplied document id and for the possession of necessary
 * owner rights in order to be able to remove a document from a repository.
 *
 * This validation uses the injection mechanism offered by CDI to get access to more
 * general validation classes such as {@link BasicDocumentValidation} to perform the
 * document removal validation correctly and to verify the existence of documents and
 * the ownership of the referenced document.
 *
 * Considering that loading a document from the database is an expensive operation the
 * loaded document is cached within the {@code foundDocument} attribute. This way the
 * service calling validation methods contained in this class does not need to load the
 * document again, but can fetch the cached document via the getter method {@link
 * DocumentRemovalValidation#getFoundDocument()}. Accordingly, there is no duplication
 * of database operations increasing performance.
 *
 * If the cached document is accessed via the mentioned getter method the cache attribute
 * is set back to {@code null} to clear the cache for next checks and to signalize other
 * services using these validation methods that there has been no document yet that was
 * loaded from the database.
 *
 * @author 6694964
 * @version 1.1
 *
 * @see ModelValidation
 * @see BasicDocumentValidation
 * @see SessionUtil
 */
@RequestScoped
public class DocumentRemovalValidation implements ModelValidation<DocumentRemovalModel> {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(DocumentRemovalValidation.class);

    @Inject
    private BasicDocumentValidation basicDocumentValidation;

    @Inject
    private SessionUtil sessionUtil;

    /**
     * Cached instance of the last loaded document to be conveyed by a
     * service in order to reduce database operations. Once accessed, this
     * document attribute is cleared back to {@code null}.
     */
    private Doc foundDocument;

    /**
     * Checks the passed payload of the respective document removal model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for the existence of the document referenced by the document id</li>
     * <li>check if the current user is the owner of the document.</li>
     * </ul>
     *
     * The {@code validate} method follows the principle of error handling descending from
     * the Go programming language. Accordingly, a potential error in the validation is
     * returned as an unsuccessful {@link ValidationResult} using a sufficient error message
     * rather than throwing an exception which requires additional mappers in the services
     * to handle those exceptions. A successful validation is returned in the same manner
     * as a successful validation result.
     *
     * In any case, the method should accept a non-null payload and return a non-null
     * validation result. In case the payload should be {@code null} this method should
     * answer with an unsuccessful validation result containing an internal server error
     * response.
     *
     * @param model the provided non-null document removal model from the document service
     * @return a non-null validation result indicating if the document removal validation
     * was successful or not
     * @see ModelValidation#validate(Payload)
     * @see org.dhbw.mosbach.ai.cmd.services.DocumentService
     */
    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentRemovalModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("DocumentRemovalModel is null"));
        }

        final int documentId = model.getDocumentId();

        final ValidationResult documentExistenceCheck = basicDocumentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        foundDocument = basicDocumentValidation.getFoundDocument();
        User currentUser = sessionUtil.getUser();

        final ValidationResult ownerCheck = basicDocumentValidation.checkUserIsDocumentOwner(foundDocument, currentUser);
        if (ownerCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document may remove it."));
        }

        return ValidationResult.success("Document may be removed");
    }

    /**
     * Retrieves the cached instance of the found document loaded by the method
     * checking the existence of the referenced document. If this method has found
     * a corresponding document this getter method returns the instance for this
     * document and clears the {@code foundDocument} attribute back to {@code null}.
     * The method returns null in case no collaborator was found or the method was
     * not executed before.
     *
     * @return the cached instance of the document or {@code null} if the document
     * was not found or the method for checking the existence of a document was
     * not executed previously.
     */
    public Doc getFoundDocument() {
        Doc document = foundDocument;
        foundDocument = null;
        return document;
    }
}
