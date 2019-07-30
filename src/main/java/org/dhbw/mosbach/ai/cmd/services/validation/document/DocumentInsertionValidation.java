package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.response.InternalServerError;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.dhbw.mosbach.ai.cmd.util.MatchTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author 6694964
 * @version 1.1
 */
@RequestScoped
public class DocumentInsertionValidation implements ModelValidation<DocumentInsertionModel> {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(DocumentInsertionValidation.class);

    private static final String DOCUMENT_NAME_VALID_CHARS = "[A-Za-z0-9_\\-()?!#=@$%&/+.*]";
    private static final String DOCUMENT_NAME_VALID_CHARS_WITH_SPACE = "[A-Za-z0-9_\\-()?!#=@$%&/+.* ]";

    private static final Pattern DOCUMENT_NAME_FORMAT = Pattern.compile("^" + DOCUMENT_NAME_VALID_CHARS + "(" + DOCUMENT_NAME_VALID_CHARS_WITH_SPACE + "*" + DOCUMENT_NAME_VALID_CHARS + "+)*$");
    private static final Pattern CONTAINS_ONLY_WHITESPACE = Pattern.compile("^\\p{javaWhitespace}+$");

    @Inject
    private BasicFieldValidation basicFieldValidation;

    @Inject
    private DocDao docDao;

    @Inject
    private SessionUtil sessionUtil;

    /**
     * Checks the passed payload of the respective document insertion model type for
     * validity. The validation includes:
     *
     * <ul>
     * <li>check for specification of the name for the new document</li>
     * <li>check for the correct format and permitted characters in the document name</li>
     * <li>check if the document name is unique among the documents in the user's repository.</li>
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
     * @param model the provided non-null document insertion model from the document
     *              service
     * @return a non-null validation result indicating if the document insertion
     * validation was successful or not
     * @see ModelValidation#validate(Payload)
     * @see org.dhbw.mosbach.ai.cmd.services.DocumentService
     */
    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentInsertionModel model) {
        if (model == null) {
            return ValidationResult.response(new InternalServerError("DocumentInsertionModel is null"));
        }

        String documentName = model.getDocumentName();

        final ValidationResult nameSpecifiedCheck = basicFieldValidation.checkSpecified(PayloadParameters.DOCUMENT_NAME, documentName);
        if (nameSpecifiedCheck.isInvalid()) {
            return nameSpecifiedCheck;
        }

        final ValidationResult nameFormatCheck = checkDocumentNameFormat(documentName);
        if (nameFormatCheck.isInvalid()) {
            return nameFormatCheck;
        }

        documentName = documentName.trim();

        final ValidationResult nameExistenceCheck = checkDocumentNameUnique(documentName);
        if (nameExistenceCheck.isInvalid()) {
            return nameExistenceCheck;
        }

        return ValidationResult.success("Document may be added to the repository");
    }

    private ValidationResult checkDocumentNameFormat(String documentName) {
        if (CONTAINS_ONLY_WHITESPACE.matcher(documentName).matches()) {
            return ValidationResult.response(new BadRequest("Document name is empty"));
        }

        if (!DOCUMENT_NAME_FORMAT.matcher(documentName.trim()).matches()) {
            String invalidChars = MatchTools.findDisparateMatches(DOCUMENT_NAME_VALID_CHARS_WITH_SPACE, documentName.trim());
            return ValidationResult.response(new BadRequest("Document name contains invalid characters: %s", invalidChars));
        }

        return ValidationResult.success("Document name is in correct format");
    }

    private ValidationResult checkDocumentNameUnique(String documentName) {
        User currentUser = sessionUtil.getUser();
        List<Doc> userDocuments = docDao.getDocsOwnedBy(currentUser);
        userDocuments.addAll(docDao.getDocsCollaboratedBy(currentUser));

        for (Doc document : userDocuments) {
            if (document.getName().equals(documentName)) {
                return ValidationResult.response(new BadRequest("Document with name '%s' already exists. Please choose another name.", documentName));
            }
        }

        return ValidationResult.success("Document name is unique");
    }
}
