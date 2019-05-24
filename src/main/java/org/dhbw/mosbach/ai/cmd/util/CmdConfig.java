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
	 * Form parameter name for mail address
	 */
	public static final String PARAM_MAIL = "mail";
	/**
	 * Unit name for JPA / JTA
	 */
	public static final String JPA_UNIT_NAME = "cmd";
	/**
	 * Hashing algorithm for the doc content and history database table
	 */
	public static final String HASH_DOC_CONTENT = "SHA-1";
}