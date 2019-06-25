package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.PayloadParameters;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.basic.BasicFieldValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.dhbw.mosbach.ai.cmd.util.MatchTools;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Pattern;

@RequestScoped
public class DocumentInsertionValidation implements ModelValidation<DocumentInsertionModel> {

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

    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentInsertionModel model) {
        String documentName = model.getName();

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
