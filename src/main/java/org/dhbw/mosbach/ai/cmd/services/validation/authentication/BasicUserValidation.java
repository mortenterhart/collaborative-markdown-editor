package org.dhbw.mosbach.ai.cmd.services.validation.authentication;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@RequestScoped
public class BasicUserValidation {

    private Logger log = LoggerFactory.getLogger(BasicUserValidation.class);

    @Inject
    private UserDao userDao;

    private User foundUser;

    @NotNull
    public ValidationResult checkUserExists(String username) {
        User user = userDao.getUserByName(username);
        if (user == null) {
            return ValidationResult.response(new BadRequest("User '%s' does not exist", username));
        }

        foundUser = user;
        return ValidationResult.success("Username '%s' already exists", username);
    }

    public User getFoundUser() {
        User user = foundUser;
        foundUser = null;
        return user;
    }
}
