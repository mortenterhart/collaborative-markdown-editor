package org.dhbw.mosbach.ai.cmd.services.validation.authentication;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class UserValidation {

    private Logger log = LoggerFactory.getLogger(UserValidation.class);

    @Inject
    private UserDao userDao;

    @Inject
    private Hashing hashing;

    private User foundUser;

    public ValidationResult checkUserExists(String username) {
        User user = userDao.getUserByName(username);
        if (user == null) {
            return ValidationResult.response(new BadRequest("Username '%s' already exists", username));
        }

        foundUser = user;
        return ValidationResult.success("Username does not exist yet");
    }

    public User getFoundUser() {
        User user = foundUser;
        foundUser = null;
        return user;
    }
}
