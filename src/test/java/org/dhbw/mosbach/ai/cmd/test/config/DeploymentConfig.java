package org.dhbw.mosbach.ai.cmd.test.config;

public final class DeploymentConfig {

    public static final String DEPLOYMENT_NAME = "cmd";

    public static final String DEFAULT_DATA_SOURCE = "java:jboss/datasources/cmdDS";

    public static final String PROJECT_POM = "pom.xml";

    private static final String META_INF = "META-INF";

    public static final String BEANS_XML = META_INF + "/beans.xml";

    public static final String PERSISTENCE_XML = META_INF + "/persistence.xml";

    public static final String TEST_PERSISTENCE_XML = META_INF + "/test-persistence.xml";

    public static final String LOG4J_PROPERTIES = META_INF + "/log4j.properties";
}
