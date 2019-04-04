package org.dhbw.mosbach.ai.cmd.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.RepoDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to handle the creating and deleteing of docs.
 * Also handles interacting with the doc history.
 *
 * @author 3040018
 */
@WebServlet(urlPatterns={CmdConfig.SERVLET_PATH_CREATE_DOC, 
						 CmdConfig.SERVLET_PATH_DELETE_DOC, 
						 CmdConfig.SERVLET_PATH_HISTORY,
						 CmdConfig.SERVLET_PATH_TRANSFER_OWNER})
public class DocServlet extends HttpServlet {

	private static final long serialVersionUID = 1912864889384524719L;
	private static final Logger log = LoggerFactory.getLogger(DocServlet.class);
	
	@Inject
	private DocDao docDao;
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private RepoDao repoDao;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		User currentUser = (User)request.getSession().getAttribute(CmdConfig.SESSION_USER);
		
		if(currentUser != null) {
			switch(request.getRequestURI()) {
				case CmdConfig.SERVLET_PATH_CREATE_DOC: createDoc((String)request.getParameter("docName"), currentUser); 		 break;
				case CmdConfig.SERVLET_PATH_DELETE_DOC: deleteDoc(Integer.parseInt(request.getParameter("docId")), currentUser); break;
				case CmdConfig.SERVLET_PATH_HISTORY: break;
				case CmdConfig.SERVLET_PATH_TRANSFER_OWNER:

					transferOwner(Integer.parseInt(request.getParameter("docId")), 
								  Integer.parseInt(request.getParameter("userId")), 
								  currentUser); 				
					break;
			}
		} else {
			log.debug("Current user was not found in session");
		}
	}
	
	/**
	 * Creates a new doc using the given doc name
	 * @param docName Given doc name
	 * @param currentUser Current user in session
	 */
	private void createDoc(String docName, User currentUser) {
		
		Doc d = new Doc();
		d.setCuser(currentUser);
		d.setContent("");
		d.setRepo(repoDao.getRepo(currentUser));
		d.setName(docName);
		d.setUuser(currentUser);
		
		docDao.createDoc(d);
	}
	
	/**
	 * Sets a doc to inactive, 'deleting' it.
	 * @param docid Given doc id
	 * @param currentUser Current user in session
	 */
	private void deleteDoc(int docid, User currentUser) {
		
		Doc d = docDao.getDoc(docid);
		
		if(d != null && d.getCuser().equals(currentUser)) {
			
			//TODO: Update doc to inactive
			
			docDao.updateDoc(d, currentUser);
		} else {
			log.debug("Doc was not found or the current user is not the owner of the doc");
		}
	}
	
	/**
	 * Transfer ownership of a doc from the current to a new owner
	 * @param userId Given id of the user who will own the doc.
	 * @param docId Given doc id
	 * @param currentUser Current session user and supposedly owner of the doc
	 */
	private void transferOwner(int userId, int docId, User currentUser) {
		
		Doc d 			  = docDao.getDoc(docId);
		User newOwner 	  = userDao.getUserById(userId);
		Repo newOwnerRepo = repoDao.getRepo(newOwner);
		
		if(d != null && newOwner != null && newOwnerRepo != null && d.getRepo().equals(repoDao.getRepo(currentUser))) {
			
			d.setUuser(currentUser);
			d.setRepo(newOwnerRepo);
			
			docDao.transferRepo(d);
		} else {
			log.debug("Either the doc or the new user was not found");
		}
	}
}