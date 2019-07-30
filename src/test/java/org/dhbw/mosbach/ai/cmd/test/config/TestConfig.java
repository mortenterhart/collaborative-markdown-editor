package org.dhbw.mosbach.ai.cmd.test.config;

public abstract class TestConfig {

    public static final String API_PREFIX = "api";

    public static final String AUTHENTICATION_LOGIN_PATH = "/authentication/login";

    public static final String AUTHENTICATION_LOGOUT_PATH = "/authentication/logout";

    public static final String AUTHENTICATION_REGISTER_PATH = "/authentication/register";

    public static final String DOCUMENT_ADD_PATH = "/document/add";

    public static final String DOCUMENT_REMOVE_PATH = "/document/remove";

    public static final String DOCUMENT_ACCESS_PATH = "/document/hasAccess";

    public static final String DOCUMENT_ALL_PATH = "/document/all";

    public static final String DOCUMENT_TRANSFER_PATH = "/document/transferOwnership";

    public static final String COLLABORATOR_ADD_PATH = "/collaborators/add";

    public static final String COLLABORATOR_REMOVE_PATH = "/collaborators/remove";

    public static final String JSESSIONID = "JSESSIONID";

    public static final int PASSWORD_LENGTH = 30;
}
