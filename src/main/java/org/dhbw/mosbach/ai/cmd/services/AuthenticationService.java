package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.Success;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;
import org.dhbw.mosbach.ai.cmd.services.response.LoginUserModel;
import org.dhbw.mosbach.ai.cmd.services.validation.LoginValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.RegisterValidation;
import org.dhbw.mosbach.ai.cmd.services.validation.ValidationResult;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doLogin(@NotNull LoginModel loginModel) {
        ValidationResult loginCheck = loginValidation.validate(loginModel);
        if (loginCheck.isInvalid()) {
            return loginCheck.getResponse().buildResponse();
        }

        String username = loginModel.getUsername();

        User user = userDao.getUserByName(username);

        request.getSession().setAttribute(CmdConfig.SESSION_USER, user);
        request.getSession().setAttribute(CmdConfig.SESSION_USERNAME, user.getName());
        request.getSession().setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, true);

        log.debug("login: User '{}' logged in successfully", username);
        return new LoginUserModel(new Success("Logged in successfully"), user).buildResponse();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doRegister(@NotNull RegisterModel registerModel) {
        ValidationResult registerCheck = registerValidation.validate(registerModel);
        if (registerCheck.isInvalid()) {
            return registerCheck.getResponse().buildResponse();
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

        log.debug("User '{}' was registered successfully", username);
        return new Success("Registration successful").buildResponse();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response doLogout() {
        if (request.getSession(false) == null || request.getSession().getAttribute(CmdConfig.SESSION_USER) == null) {
            return new Success("You are already logged out").buildResponse();
        }

        request.getSession().setAttribute(CmdConfig.SESSION_USER, null);
        request.getSession().setAttribute(CmdConfig.SESSION_USERNAME, null);
        request.getSession().setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, false);
        request.getSession().invalidate();

        return new Success("Successfully logged out").buildResponse();
    }
}
