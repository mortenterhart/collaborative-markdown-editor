package org.dhbw.mosbach.ai.cmd.session;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

@RequestScoped
public class SessionUtil {

    private HttpServletRequest request;

    public SessionUtil() {}

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

    private <T> T requireNotNull(T request) {
        if (request == null) {
            throw new NullPointerException("HTTP request is null");
        }

        return request;
    }
}
