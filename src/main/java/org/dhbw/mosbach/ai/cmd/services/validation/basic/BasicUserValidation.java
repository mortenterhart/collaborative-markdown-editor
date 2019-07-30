package org.dhbw.mosbach.ai.cmd.services.validation.basic;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * The {@code BasisUserValidation} offers basic validation functionality for usernames
 * by checking if a specified username is defined in the database. This is accomplished
 * by injecting database access objects (DAOs) into this managed class to allow access
 * to database lookup operations.
 *
 * Considering that loading a user from the database is an expensive operation the loaded
 * user is cached within the {@code foundUser} attribute. This way the service calling
 * validation methods contained in this class does not need to load the user again, but
 * can fetch the cached user via the getter method {@link BasicUserValidation#getFoundUser}.
 * Accordingly, there is no duplication of database operations increasing performance.
 *
 * If the cached user is accessed via the mentioned getter method the cache attribute is
 * set back to {@code null} to clear the cache for next checks and to signalize other
 * services using these validation methods that there has been no user yet that was loaded
 * from the database.
 *
 * @author 6694964
 * @version 1.1
 *
 * @see UserDao
 * @see User
 * @see org.dhbw.mosbach.ai.cmd.services.validation.ModelValidation
 */
@RequestScoped
public class BasicUserValidation {

    /**
     * Private logging instance to log validation operations
     */
    private static final Logger log = LoggerFactory.getLogger(BasicUserValidation.class);

    /**
     * Injected field for loading user objects from the database
     */
    @Inject
    private UserDao userDao;

    /**
     * Cached instance of the last loaded user to be conveyed by a service
     * in order to reduce database operations. Once accessed, this user
     * attribute is cleared back to {@code null}.
     */
    private User foundUser;

    /**
     * Checks if a user with the supplied username exists in the database by using
     * lookup operations provided by the {@link UserDao}. If the user exists in the
     * database the loaded user instance is cached in the {@code foundUser} attribute
     * and can be fetched using the getter method. A successful validation result is
     * returned.
     *
     * If the user does not exist, the validation method returns an unsuccessful
     * validation result.
     *
     * @param username the username for the user to be checked for existence
     * @return a successful validation result if the user exists, otherwise an
     * unsuccessful validation result.
     */
    @NotNull
    public ValidationResult checkUserExists(String username) {
        User user = userDao.getUserByName(username);
        if (user == null) {
            return ValidationResult.response(new BadRequest("User '%s' does not exist", username));
        }

        foundUser = user;
        return ValidationResult.success("Username '%s' already exists", username);
    }

    /**
     * Fetches the cached user attribute after checking for existence of a specified
     * username using {@link BasicUserValidation#checkUserExists(String)}. If the
     * method has found a concrete user this user object is returned. If the method
     * was not invoked before or if no user was found it returns {@code null}.
     *
     * @return the loaded user object from the database if one was found, null otherwise.
     */
    public User getFoundUser() {
        User user = foundUser;
        foundUser = null;
        return user;
    }
}
