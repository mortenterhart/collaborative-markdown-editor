package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.services.helper.Authenticator;
import org.dhbw.mosbach.ai.cmd.services.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.services.helper.JsonUtil;
import org.dhbw.mosbach.ai.cmd.services.helper.PasswordGenerator;
import org.dhbw.mosbach.ai.cmd.services.payload.RegisterModel;
import org.dhbw.mosbach.ai.cmd.services.payload.TestLoginModel;
import org.dhbw.mosbach.ai.cmd.services.response.LoginUserResponse;
import org.dhbw.mosbach.ai.cmd.testconfig.Datasets;
import org.dhbw.mosbach.ai.cmd.testconfig.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.PackageIncludes;
import org.dhbw.mosbach.ai.cmd.testconfig.TestConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.TestUser;
import org.dhbw.mosbach.ai.cmd.testconfig.TestUsers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_TABLES_ONLY)
public class AuthenticationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceTest.class);

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME)
    public static WebArchive createDeployment() {
        final WebArchive war = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                                 .addMavenRuntimeAndTestDependencies()
                                                 .addBeansAndPersistenceDefinition()
                                                 .addTestResources()
                                                 .addPackages(PackageIncludes.AUTHENTICATION_SERVICE)
                                                 .packageWebArchive();

        log.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Test
    @UsingDataSet(Datasets.USERS)
    public void testDoLogin() throws URISyntaxException {
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(TestConfig.API_PREFIX + TestConfig.AUTHENTICATION_LOGIN_PATH));

        TestUser user = TestUsers.ADMIN;

        Response response = target.request(MediaType.APPLICATION_JSON)
                                  .post(Entity.json(new TestLoginModel(user)));

        String responseBody = response.readEntity(String.class);
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(responseBody));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        LoginUserResponse userResponse = jsonUtil.deserialize(responseBody, LoginUserResponse.class);

        Assert.assertNotNull(userResponse);
        Assert.assertNotNull(userResponse.getUser());
        Assert.assertEquals(user.getName(), userResponse.getUser().getName());
        Assert.assertEquals(user.getMail(), userResponse.getUser().getMail());
    }

    @Test
    @RunAsClient
    public void testDoRegister(@ArquillianResteasyResource(TestConfig.API_PREFIX) final WebTarget target) {
        PasswordGenerator generator = new PasswordGenerator();

        String username = TestUsers.WILDFLY.getName();
        String email = TestUsers.WILDFLY.getMail();
        String password = generator.generateSecurePassword(TestConfig.PASSWORD_LENGTH);

        JsonUtil jsonUtil = new JsonUtil();
        String registrationPayload = jsonUtil.serialize(new RegisterModel(username, email, password));

        Response registerResponse = target.path(TestConfig.AUTHENTICATION_REGISTER_PATH)
                                          .request(MediaType.APPLICATION_JSON)
                                          .post(Entity.json(registrationPayload));

        String registerResponseBody = registerResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(registerResponseBody));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), registerResponse.getStatus());

        Response secondResponse = target.path(TestConfig.AUTHENTICATION_REGISTER_PATH)
                                        .request(MediaType.APPLICATION_JSON)
                                        .post(Entity.json(registrationPayload));

        String secondResponseBody = secondResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(secondResponseBody));

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), secondResponse.getStatus());
    }

    @Test
    @UsingDataSet(Datasets.USERS)
    public void testDoLogoutWithSession() throws URISyntaxException {
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.APP_USER);

        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(TestConfig.API_PREFIX + TestConfig.AUTHENTICATION_LOGOUT_PATH));

        Response logoutResponse = target.request(MediaType.APPLICATION_JSON)
                                        .cookie(authCookie)
                                        .post(null);

        String logoutResponseBody = logoutResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(logoutResponseBody));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), logoutResponse.getStatus());
    }

    @Test
    @RunAsClient
    public void testDoLogoutWithoutSession(@ArquillianResteasyResource(TestConfig.API_PREFIX) final WebTarget target) {
        Response logoutResponse = target.path(TestConfig.AUTHENTICATION_LOGOUT_PATH)
                                        .request(MediaType.APPLICATION_JSON)
                                        .post(null);

        String logoutResponseBody = logoutResponse.readEntity(String.class);
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(logoutResponseBody));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), logoutResponse.getStatus());
    }
}
