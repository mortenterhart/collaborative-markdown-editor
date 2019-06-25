package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;
import org.dhbw.mosbach.ai.cmd.services.response.LoginUserResponse;
import org.dhbw.mosbach.ai.cmd.services.response.Success;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.LoginValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.RegisterValidation;
import org.dhbw.mosbach.ai.cmd.session.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The {@code AuthenticationService} provides a REST compliant endpoint implementation
 * for authentication against the server. It facilitates services used for registration
 * of new users, login and logout.
 *
 * All incoming requests are accurately validated by self-reliant validation functions
 * residing in the package {@link org.dhbw.mosbach.ai.cmd.services.validation.authentication}
 * and cause a corresponding response to be sent back to the client. Request and response
 * payloads are serialized and deserialized using JAX-RS annotations and their implementations
 * and are provided using special payload and response models. Based on the applied request
 * and the service conditions, the endpoint may return one of the following status codes:
 *
 * <ul>
 * <li>{@code 200 OK}: The request was processed successfully and the desired operation
 * was done.</li>
 * <li>{@code 400 Bad Request}: The request contained invalid fields or some conditions were
 * not met. The operation was aborted.</li>
 * <li>{@code 401 Unauthorized}: The client is not authorized to perform some operation
 * because he is not authenticated. He has to login first before proceeding.</li>
 * <li>{@code 403 Forbidden}: The client is not permitted to access a specific document.</li>
 * </ul>
 *
 * Both request and response are provided as JSON formatted fields.
 *
 * @author 6694964
 * @version 1.3
 *
 * @see RestEndpoint
 * @see LoginValidation
 * @see RegisterValidation
 */
@RequestScoped
@Path(ServiceEndpoints.PATH_AUTHENTICATION)
public class AuthenticationService extends RootService implements RestEndpoint {

    /**
     * Private instance of logging engine
     */
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    /*
     * Injected fields for database access and request validation
     */
    @Inject
    private UserDao userDao;

    @Inject
    private Hashing hashing;

    @Inject
    private LoginValidation loginValidation;

    @Inject
    private RegisterValidation registerValidation;

    /**
     * The session utility is used to enable and simplify access to the user session.
     */
    @Inject
    private SessionUtil sessionUtil;

    /**
     * Performs the login for a given username and password inside a {@link LoginModel}.
     * The login creates a new session for the user calling this service unless there
     * is already a session for this user. The service responds with a message and the
     * user object which is used for providing user information in the frontend such as
     * name and email. In addition, a cookie is returned via the {@code Set-Cookie} header
     * to store the {@code JSESSIONID} referencing the user session.
     *
     * @param loginModel the request model containing username and password
     * @return a {@code 200 OK} response if the login is successful including the user
     * object, otherwise {@code 400 Bad Request}.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doLogin(@NotNull LoginModel loginModel) {
        final ValidationResult loginCheck = loginValidation.validate(loginModel);
        if (loginCheck.isInvalid()) {
            return loginCheck.buildResponse();
        }

        String username = loginModel.getUsername();

        User user = userDao.getUserByName(username);

        if (sessionUtil.createSession(user)) {
            log.debug("login: Created new session for user '{}'", username);
        }

        log.info("login: User '{}' logged in successfully", username);
        return new LoginUserResponse(user, "Logged in successfully.").buildResponse();
    }

    /**
     * Registers a new user at the application by means of specifying the username, the email
     * address and the password. The new user is persisted to the database. Alongside the
     * creation of a new user a new repository is created for storing the user's documents.
     *
     * The username, email and password have to match some syntax rules which can be explored
     * in greater detail in {@link RegisterValidation}. The password is encrypted using the
     * BCrypt algorithm which is implemented in {@link Hashing}. Additionally, it is examined
     * whether the user with this username already exists.
     *
     * @param registerModel the request model containing username, email address and password
     *                      for the new user
     * @return a {@code 200 OK} response if the registration was successful, otherwise
     * {@code 400 Bad Request} if some of the constraints do not match
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doRegister(@NotNull RegisterModel registerModel) {
        final ValidationResult registerCheck = registerValidation.validate(registerModel);
        if (registerCheck.isInvalid()) {
            return registerCheck.buildResponse();
        }

        String username = registerModel.getUsername();
        String email = registerModel.getEmail();
        String password = registerModel.getPassword();

        User newUser = new User();
        newUser.setName(username);
        newUser.setMail(email);

        String hashedPassword = hashing.hashPassword(password);
        newUser.setPassword(hashedPassword);

        userDao.createUser(newUser);

        log.info("register: User '{}' was registered successfully", username);
        return new Success("Registration successful.").buildResponse();
    }

    /**
     * Logs the current user out of the application. If the user is still logged in, his session
     * is destroyed and invalidated. If he is not logged in any more, in case his session expired,
     * the service also responds with a successful message. It also transmits the demand for deleting
     * the {@code JSESSIONID} cookie from the client.
     *
     * @return a {@code 200 OK} response if the user was logged out or if he was already logged out
     */
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doLogout() {
        if (!sessionUtil.isLoggedIn()) {
            return new Success("You are already logged out.").buildResponse();
        }

        User user = sessionUtil.getUser();

        log.info("logout: Logged user '{}' out", user.getName());
        if (sessionUtil.invalidateSession()) {
            log.info("logout: Session of user '{}' was invalidated", user.getName());
        }

        return new Success("Successfully logged out.").buildResponse();
    }
}
