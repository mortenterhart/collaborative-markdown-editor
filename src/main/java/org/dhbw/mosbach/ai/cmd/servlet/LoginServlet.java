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
 * Servlet to handle the login of users
 *
 * @author 3040018
 */
@WebServlet(CmdConfig.SERVLET_PATH_LOGIN)
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = -5445230009178009381L;
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Inject
    private UserDao userDao;

    @Inject
    private Hashing hashing;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = (String) request.getParameter(CmdConfig.SESSION_USERNAME);

        if (username != null && !username.isEmpty()) {
            User user = userDao.getUserByName(username);

            if (user != null) {
                if (hashing.checkPassword((String) request.getParameter(CmdConfig.PARAM_PASSWORD), user.getPassword())) {
                	
                	request.getSession().setAttribute(CmdConfig.SESSION_USER, user);
                	request.getSession().setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, true);
                    log.debug("Login successful!");
                } else {
                    log.debug("Wrong password!");
                }
            } else {
                log.debug("User not found!");
            }
        } else {
            log.debug("No username provided!");
        }
    }
}