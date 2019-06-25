package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentAccessModel;
import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicDocumentValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class DocumentAccessValidation implements ModelValidation<DocumentAccessModel> {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(DocumentAccessValidation.class);

    @Inject
    private BasicDocumentValidation basicDocumentValidation;

    private Doc foundDocument;

    /**
     * Checks the passed payload of the respective document access model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for the existence of the document pointed to by the document id.</li>
     * </ul>
     *
     * The actual investigation of document access occurs in the document access service
     * itself because it is the task of the service rather than the validation to examine
     * whether the user has access to a specific document. This validation only takes care
     * of the request which contains the document id.
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
     * @param model the provided non-null document access model from the document service
     * @return a non-null validation result indicating if the document access validation
     * was successful or not
     * @see ModelValidation#validate(Payload)
     * @see org.dhbw.mosbach.ai.cmd.services.DocumentService
     */
    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentAccessModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("DocumentAccessModel is null"));
        }

        final int documentId = model.getDocumentId();

        final ValidationResult documentExistenceCheck = basicDocumentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        foundDocument = basicDocumentValidation.getFoundDocument();

        return ValidationResult.success("Access to this document may be checked");
    }

    public Doc getFoundDocument() {
        return foundDocument;
    }
}
