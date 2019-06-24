package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.response.Success;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;
import org.dhbw.mosbach.ai.cmd.services.response.LoginUserResponse;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.LoginValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.authentication.RegisterValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
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
 * @author 6694964
 */

@RequestScoped
@Path(ServiceEndpoints.PATH_AUTHENTICATION)
public class AuthenticationService implements RestService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Inject
    private UserDao userDao;

    @Inject
    private Hashing hashing;

    @Inject
    private LoginValidation loginValidation;

    @Inject
    private RegisterValidation registerValidation;

    @Inject
    private SessionUtil sessionUtil;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doLogin(@NotNull LoginModel loginModel) {
        final ValidationResult loginCheck = loginValidation.validate(loginModel);
        if (loginCheck.isInvalid()) {
            return loginCheck .buildResponse();
        }

        String username = loginModel.getUsername();

        User user = userDao.getUserByName(username);

        if (sessionUtil.createSession(user)) {
            log.debug("login: Created new session for user '{}'", username);
        }

        log.debug("login: User '{}' logged in successfully", username);
        return new LoginUserResponse(user, "Logged in successfully.").buildResponse();
    }

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

        log.debug("register: User '{}' was registered successfully", username);
        return new Success("Registration successful.").buildResponse();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doLogout() {
        if (!sessionUtil.isLoggedIn()) {
            return new Success("You are already logged out.").buildResponse();
        }

        User user = sessionUtil.getUser();

        if (sessionUtil.invalidateSession()) {
            log.debug("logout: Session of user '{}' was invalidated", user.getName());
        }

        return new Success("Successfully logged out.").buildResponse();
    }
}
