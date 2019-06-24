package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentAccessModel;
import org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class DocumentAccessValidation implements ModelValidation<DocumentAccessModel> {

    @Inject
    private DocumentValidation documentValidation;

    @Inject
    private CollaboratorDao collaboratorDao;

    @Inject
    private SessionUtil sessionUtil;

    private Doc foundDocument;

    @Override
    @NotNull
    public ValidationResult validate(@NotNull DocumentAccessModel model) {
        int documentId = model.getDocumentId();

        final ValidationResult documentExistenceCheck = documentValidation.checkDocumentExists(documentId);
        if (documentExistenceCheck.isInvalid()) {
            return documentExistenceCheck;
        }

        foundDocument = documentValidation.getFoundDocument();

        return ValidationResult.success("Access to this document may be checked");
    }

    public Doc getFoundDocument() {
        return foundDocument;
    }
}
