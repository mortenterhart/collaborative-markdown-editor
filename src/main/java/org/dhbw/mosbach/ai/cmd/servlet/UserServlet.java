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

/**
 * Servlet to handle profile editing of a user
 *
 * @author 3040018
 */
@WebServlet(CmdConfig.SERVLET_PATH_PROFILE)
public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 4857232168353017457L;
	private static final Logger log = LoggerFactory.getLogger(UserServlet.class);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}