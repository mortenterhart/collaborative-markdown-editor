package org.dhbw.mosbach.ai.cmd.services.validation.basic;

import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * @author 6694964
 * @version 1.1
 */
@RequestScoped
public class BasicDocumentValidation {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(BasicDocumentValidation.class);

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

    public ValidationResult checkUserIsDocumentOwner(Doc document, User user) {
        if (user.getId() != document.getRepo().getOwner().getId()) {
            return ValidationResult.response(new BadRequest("Applied user is not the document owner"));
        }

        return ValidationResult.success("Applied user is the document owner");
    }

    public Doc getFoundDocument() {
        Doc document = foundDocument;
        foundDocument = null;
        return document;
    }
}
