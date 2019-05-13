package org.dhbw.mosbach.ai.cmd.util;

/**
 * Configuration for certain globally reused variables
 *
 * @author 3040018
 */
public abstract class CmdConfig {

	/**
	 * Session attribute name and form parameter name for storing user name
	 */
	public static final String SESSION_USERNAME = "username";
	/**
	 * Session attribute name for storing user id
	 */
	public static final String SESSION_USERID = "userId";
	/**
	 * Session attribute to show if a user is logged in or not
	 */
	public static final String SESSION_IS_LOGGED_IN = "isLoggedIn";
	/**
	 * Session attribute to store a user object
	 */
	public static final String SESSION_USER = "user";
	/**
	 * Form parameter name for password
	 */
	public static final String PARAM_PASSWORD = "password";
	/**
	 * Form parameter name for mail address
	 */
	public static final String PARAM_MAIL = "mail";
	/**
	 * Unit name for JPA / JTA
	 */
	public static final String JPA_UNIT_NAME = "cmd";
	/**
	 * Path to the LoginServlet class
	 */
	public static final String SERVLET_PATH_LOGIN = "/login";
	/**
	 * Path to the RegisterServlet class
	 */
	public static final String SERVLET_PATH_REGISTER = "/register";
	/**
	 * Path to the LogoutServlet class
	 */
	public static final String SERVLET_PATH_LOGOUT = "/logout";
	/**
	 * Path to the UserServlet class
	 */
	public static final String SERVLET_PATH_PROFILE = "/profile";
	/**
	 * Path to the DocServlet class, handles requests regarding the history of a doc
	 */
	public static final String SERVLET_PATH_HISTORY = "/history";
	/**
	 * Path to the DocServlet class, creates a new doc
	 */
	public static final String SERVLET_PATH_CREATE_DOC = "/createDoc";
	/**
	 * Path to the DocServlet class, deletes a given doc
	 */
	public static final String SERVLET_PATH_DELETE_DOC = "/deleteDoc";
	/**
	 * Path to the CollaboratorServlet class, adds a new collaborator to a doc
	 */
	public static final String SERVLET_PATH_ADD_COLLAB = "/addCollab";
	/**
	 * Path to the CollaboratorServlet class, removes a given collaborator from a given doc
	 */
	public static final String SERVLET_PATH_REMOVE_COLLAB = "/removeCollab";
	/**
	 * Path to the CollaboratorServlet class, used when a user removes himself from a doc
	 */
	public static final String SERVLET_PATH_UNSUBSCRIBE = "/unsubscribe";
	/**
	 * Path to the DocServlet class to transfer ownership of a given doc to another user.
	 */
	public static final String SERVLET_PATH_TRANSFER_OWNER = "transfer";
	/**
	 * JPA unit name
	 */
	public static final String PERSISTENCE_UNIT_NAME = "cmd";
	/**
	 * Hashing algorithm for the doc content and history database table
	 */
	public static final String HASH_DOC_CONTENT = "SHA-1";
}