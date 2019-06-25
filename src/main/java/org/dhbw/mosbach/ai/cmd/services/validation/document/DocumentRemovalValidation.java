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

        Doc document = basicDocumentValidation.getFoundDocument();
        User currentUser = sessionUtil.getUser();

        final ValidationResult ownerCheck = basicDocumentValidation.checkUserIsDocumentOwner(document, currentUser);
        if (ownerCheck.isInvalid()) {
            return ValidationResult.response(new BadRequest("You are unauthorized. Only the owner of this document may remove it."));
        }

        return ValidationResult.success("Document may be removed");
    }
}
