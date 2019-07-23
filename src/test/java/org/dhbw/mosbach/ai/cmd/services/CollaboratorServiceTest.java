package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.services.helper.Authenticator;
import org.dhbw.mosbach.ai.cmd.services.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.services.helper.JsonUtil;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.CollaboratorRemovalModel;
import org.dhbw.mosbach.ai.cmd.testconfig.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;

import static org.dhbw.mosbach.ai.cmd.testconfig.TestConfig.API_PREFIX;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_TABLES_ONLY)
public class CollaboratorServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CollaboratorServiceTest.class);

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME)
    public static WebArchive createDeployment() {
        final WebArchive war = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                                 .addMavenRuntimeAndTestDependencies()
                                                 .addBeansAndPersistenceDefinition()
                                                 .addTestResources()
                                                 .addPackages(PackageIncludes.COLLABORATOR_SERVICE)
                                                 .packageWebArchive();

        log.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @BeforeClass
    public static void initResteasyClient() {
        System.out.println("Initializing RESTEasy client");
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
    public void testAddCollaborator() throws URISyntaxException {
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.WILDFLY);

        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        Assert.assertNotNull(authCookie);

        int documentId = 2;
        TestUser collaborator = TestUsers.APP_USER;

        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.COLLABORATOR_ADD_PATH));

        Response addResponse = target.request(MediaType.APPLICATION_JSON)
                                     .cookie(authCookie)
                                     .post(Entity.json(new CollaboratorInsertionModel(documentId, collaborator.getName())));

        log.info(jsonUtil.prettyPrint(addResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), addResponse.getStatus());
    }

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
    public void testRemoveCollaborator() throws URISyntaxException {
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.APP_USER);

        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        Assert.assertNotNull(authCookie);

        int documentId = 1;
        int collaboratorId = 2;

        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.COLLABORATOR_REMOVE_PATH));

        Response removeResponse = target.request(MediaType.APPLICATION_JSON)
                                        .cookie(authCookie)
                                        .method(HttpMethod.DELETE, Entity.json(new CollaboratorRemovalModel(documentId, collaboratorId)));

        log.info(jsonUtil.prettyPrint(removeResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), removeResponse.getStatus());
    }
}
