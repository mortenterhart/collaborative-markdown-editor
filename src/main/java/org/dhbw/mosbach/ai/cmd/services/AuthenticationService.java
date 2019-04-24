package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.response.BadRequest;
import org.dhbw.mosbach.ai.cmd.response.Forbidden;
import org.dhbw.mosbach.ai.cmd.response.Success;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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

@ApplicationScoped
@Path(ServiceEndpoints.PATH_AUTHENTICATION)
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Inject
    private UserDao userDao;

    @Inject
    private Hashing hashing;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doLogin(LoginModel loginModel) {
        String username = loginModel.getUsername();
        String password = loginModel.getPassword();

        if (username == null || username.isEmpty()) {
            log.debug("login: applied username was null or empty");
            return new BadRequest().create("Username was not specified");
        }

        User user = userDao.getUserByName(username);
        if (user == null) {
            log.debug("login: User with username '{}' not defined", username);
            return new Forbidden().create("Invalid username or password");
        }

        if (!hashing.checkPassword(password, user.getPassword())) {
            log.debug("login: User '{}' inserted wrong password", username);
            return new Forbidden().create("Invalid username or password");
        }

        request.getSession().setAttribute(CmdConfig.SESSION_USER, user);
        request.getSession().setAttribute(CmdConfig.SESSION_USERNAME, user.getName());
        request.getSession().setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, true);

        log.debug("login: User '{}' logged in successfully", username);
        return new Success().create("Logged in successfully");
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doRegister(RegisterModel registerModel) {
        String username = registerModel.getUsername();
        String email = registerModel.getEmail();
        String password = registerModel.getPassword();

        if (username == null || username.isEmpty()) {
            log.debug("register: applied username was null or empty");
            return new BadRequest().create("Username was not specified");
        }

        if (email == null || email.isEmpty()) {
            log.debug("register: applied email was null or empty");
            return new BadRequest().create("Email was not specified");
        }

        if (password == null || password.isEmpty()) {
            log.debug("register: applied password was null or empty");
            return new BadRequest().create("Password was not specified");
        }

        if (userDao.getUserByName(username) != null) {
            log.debug("register: User '{}' is already registered", username);
            return new BadRequest().create(String.format("Username \"%s\" is already registered", username));
        }

        User newUser = new User();
        newUser.setName(username);
        newUser.setMail(email);

        String hashedPassword = hashing.hashPassword(password);
        newUser.setPassword(hashedPassword);

        userDao.createUser(newUser);

        log.debug("User '{}' was registered successfully", username);
        return new Success().create("Registration successful");
    }
}
