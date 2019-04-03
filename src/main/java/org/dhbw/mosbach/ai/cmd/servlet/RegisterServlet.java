package org.dhbw.mosbach.ai.cmd.servlet;

import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to handle registering a new account
 *
 * @author 3040018
 */
@WebServlet(CmdConfig.SERVLET_PATH_REGISTER)
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 6763956539947432612L;
    private static final Logger log = LoggerFactory.getLogger(RegisterServlet.class);

    @Inject
    private UserDao userDao;

    @Inject
    private Hashing hashing;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = (String) request.getParameter(CmdConfig.SESSION_USERNAME);
        String password = hashing.hashPassword((String) request.getParameter(CmdConfig.PARAM_PASSWORD));
        String mail = (String) request.getParameter(CmdConfig.PARAM_MAIL);

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            if (userDao.getUserByName(username) == null) {
                User user = new User();
                user.setName(username);
                user.setPassword(password);
                user.setMail(mail);
                userDao.createUser(user);

                log.debug("User registered successfully.");
            } else {
                log.debug("Use already exists.");
            }
        } else {
            log.debug("No username or password provided.");
        }
    }
}
