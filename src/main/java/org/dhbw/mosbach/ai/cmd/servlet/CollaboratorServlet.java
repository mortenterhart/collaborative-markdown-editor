package org.dhbw.mosbach.ai.cmd.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dhbw.mosbach.ai.cmd.db.CollaboratorDao;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.HasAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to the handle adding and removing collaborators form a certain doc as well
 * as being able to unsubscribe from a given doc by yourself
 * @author 3040018
 */
@WebServlet(urlPatterns={CmdConfig.SERVLET_PATH_ADD_COLLAB, 
						 CmdConfig.SERVLET_PATH_REMOVE_COLLAB, 
						 CmdConfig.SERVLET_PATH_UNSUBSCRIBE})
public class CollaboratorServlet extends HttpServlet {

	private static final long serialVersionUID = -6331663664740780743L;
	private static final Logger log = LoggerFactory.getLogger(CollaboratorServlet.class);

	@Inject
	CollaboratorDao collaboratorDao;
	
	@Inject
	DocDao docDao;
	
	@Inject
	UserDao userDao;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Doc  givenDoc 	 = docDao.getDoc(Integer.parseInt(request.getParameter("docId")));
		User givenUser 	 = userDao.getUserById(Integer.parseInt(request.getParameter("userId")));
		User currentUser = (User)request.getSession().getAttribute(CmdConfig.SESSION_USER);
		
		if(givenDoc != null && givenUser != null && currentUser != null) {
			
			Collaborator c = new Collaborator();
			c.setDoc(givenDoc);
			
			switch(request.getRequestURI()) {	
				case CmdConfig.SERVLET_PATH_ADD_COLLAB: 	addCollab(givenUser, currentUser, c); 							break;
				case CmdConfig.SERVLET_PATH_REMOVE_COLLAB: 	removeOrUnsubscribeCollab(givenUser, currentUser, c, true); 	break;
				case CmdConfig.SERVLET_PATH_UNSUBSCRIBE: 	removeOrUnsubscribeCollab(givenUser, currentUser, c, false);	break;
			}
		} else {
			log.debug("Invalid or empty parameters found");
		}
	}

	/**
	 * Add a given user as a collaborator to a given doc
	 * @param userToAdd User to be added as a collaborator
	 * @param currentUser Current user, has to be the owner of the doc
	 * @param c Given collaborator object
	 */
	private void addCollab(User userToAdd, User currentUser, Collaborator c) {
		
		if(c.getDoc().getCuser().equals(currentUser)) {
			
			c.setHasAccess(HasAccess.Y);
			c.setUser(userToAdd);
			
			collaboratorDao.createCollaborator(c);
		} else {
			log.debug("Current user is not the owner of the document");
		}
	}
	
	/**
	 * Revoke the access to a doc for  a certain user
	 * @param userToRemove The user to be removed from collaborating
	 * @param currentUser The current user in the session
	 * @param c Given collaborator object
	 * @param isRemove True if it is a remove operation, initiated by the doc owner
	 */
	private void removeOrUnsubscribeCollab(User userToRemove, User currentUser, Collaborator c, boolean isRemove) {
		
		c.setHasAccess(HasAccess.N);
		
		if(isRemove && c.getDoc().getCuser().equals(currentUser))
			c.setUser(userToRemove);
		else
			c.setUser(currentUser);		
		
		collaboratorDao.updateCollaborator(c);
	}
}