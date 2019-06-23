package org.dhbw.mosbach.ai.cmd.services.validation.document;

import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class DocumentValidation {

    @Inject
    private DocDao docDao;

    private Doc foundDocument;

    public ValidationResult checkDocumentExists(int documentId) {
        Doc document = docDao.getDoc(documentId);
        if (document == null) {
            return ValidationResult.response(new BadRequest("Document with id '%d' does not exist.", documentId));
        }

        foundDocument = document;
        return ValidationResult.success("Document with id '%d' exists.", documentId);
    }

    public Doc getFoundDocument() {
        Doc document = foundDocument;
        foundDocument = null;
        return document;
    }

    public ValidationResult checkUserIsDocumentOwner(Doc document, User currentUser) {
        if (currentUser.getId() != document.getRepo().getOwner().getId()) {
            return ValidationResult.response(new BadRequest("Current user is not the document owner"));
        }

        return ValidationResult.success("Current user is the document owner");
    }
}
