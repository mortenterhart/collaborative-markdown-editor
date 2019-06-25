package org.dhbw.mosbach.ai.cmd.session;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

/**
 * The session utility is a simplification regarding interactions with the user
 * session by providing mechanisms to create new sessions, check if a requesting
 * user is logged in, getting user information from the session and finally
 * destroy and invalidate the session.
 *
 * For this purpose, this utility uses the CDI injection mechanism to inject
 * an incoming request in form of a {@link HttpServletRequest} into the constructor
 * of this utility annotated with {@code @Inject}. It is then stored inside the
 * request attribute and obtained whenever a service requires access to the session.
 * With the request attribute the utility gains access to the session that is a
 * managed bean stored at server side.
 *
 * @author 6694964
 * @version 1.2
 *
 * @see HttpServletRequest
 * @see Inject
 */
@RequestScoped
public class SessionUtil {

    private HttpServletRequest request;

    public SessionUtil() {
    }

    @Inject
    public SessionUtil(@Context HttpServletRequest request) {
        this.request = requireNotNull(request);
    }

    public boolean createSession(User user) {
        if (isLoggedIn()) {
            return false;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(CmdConfig.SESSION_USER, user);
        session.setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, true);

        return true;
    }

    public boolean isLoggedIn() {
        return request.getSession(false) != null && getUser() != null;
    }

    public boolean invalidateSession() {
        if (!isLoggedIn()) {
            return false;
        }

        HttpSession session = request.getSession();
        session.setAttribute(CmdConfig.SESSION_USER, null);
        session.setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, null);
        session.invalidate();

        return true;
    }

    public User getUser() {
        return (User) request.getSession().getAttribute(CmdConfig.SESSION_USER);
    }

    private HttpServletRequest requireNotNull(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("HTTP request is null");
        }

        return request;
    }
}
