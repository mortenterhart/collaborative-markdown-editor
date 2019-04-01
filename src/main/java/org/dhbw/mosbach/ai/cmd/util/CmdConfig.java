package org.dhbw.mosbach.ai.cmd.util;

/**
 * Configuration for certain globally reused variables
 *
 * @author 3040018
 */
public abstract class CmdConfig {

    /**
     * Session attribute name and form parameter name for storing username
     */
    public static final String SESSION_USERNAME = "username";
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
     * Path t the RegisterServlet class
     */
    public static final String SERVLET_PATH_REGISTER = "/register";
    /**
     * JPA unit name
     */
    public static final String PERSISTENCE_UNIT_NAME = "cmd";
}
