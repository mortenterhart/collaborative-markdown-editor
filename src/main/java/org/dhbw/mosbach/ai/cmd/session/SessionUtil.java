package org.dhbw.mosbach.ai.cmd.session;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestScoped
public class SessionUtil {

    public boolean createSession(HttpServletRequest request, User user) {
        if (isLoggedIn(request)) {
            return false;
        }

        HttpSession session = requireNotNull(request).getSession(true);
        session.setAttribute(CmdConfig.SESSION_USER, user);
        session.setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, true);

        return true;
    }

    public boolean isLoggedIn(HttpServletRequest request) {
        return requireNotNull(request).getSession(false) != null && getUser(request) != null;
    }

    public boolean invalidateSession(HttpServletRequest request) {
        if (!isLoggedIn(request)) {
            return false;
        }

        HttpSession session = requireNotNull(request).getSession();
        session.setAttribute(CmdConfig.SESSION_USER, null);
        session.setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, null);
        session.invalidate();

        return true;
    }

    public User getUser(HttpServletRequest request) {
        return (User) requireNotNull(request).getSession().getAttribute(CmdConfig.SESSION_USER);
    }

    private <T> T requireNotNull(T request) {
        if (request == null) {
            throw new NullPointerException("HTTP request is null");
        }

        return request;
    }
}
