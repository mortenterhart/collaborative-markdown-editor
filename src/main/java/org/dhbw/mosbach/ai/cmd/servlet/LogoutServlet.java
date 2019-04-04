package org.dhbw.mosbach.ai.cmd.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(CmdConfig.SERVLET_PATH_LOGOUT)
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = -434612786888310928L;
	private static final Logger log = LoggerFactory.getLogger(LogoutServlet.class);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Clean the session and invalidate it
		request.getSession().setAttribute(CmdConfig.SESSION_USER, "");
		request.getSession().setAttribute(CmdConfig.SESSION_IS_LOGGED_IN, false);
		request.getSession().invalidate();
		
		response.sendRedirect("");
	}
}