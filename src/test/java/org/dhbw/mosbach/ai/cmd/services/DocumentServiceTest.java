package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.testconfig.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.PackageIncludes;
import org.dhbw.mosbach.ai.cmd.testconfig.TestConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.TestUsers;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.services.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.services.helper.JsonUtil;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.services.response.DocumentListResponse;
import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentListEntity;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeDeserializer;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeSerializer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_TABLES_ONLY)
public class DocumentServiceTest {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceTest.class);

    private static final String API_PREFIX = RootService.class.getAnnotation(ApplicationPath.class).value().substring(1);

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME)
    public static WebArchive createDeployment() {
        WebArchive war = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                           .addMavenRuntimeAndTestDependencies()
                                           .addBeansAndPersistenceDefinition()
                                           .addTestResources()
                                           .addPackages(PackageIncludes.DOCUMENT_SERVICE)
                                           .packageWebArchive();

        log.info(war.toString(true));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Inject
    private UserDao userDao;

    @BeforeClass
    public static void initResteasyClient() {
        System.out.println("Initializing RESTEasy client");
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    @Test
    @UsingDataSet("datasets/documents.yml")
    public void testGetAllDocuments() throws URISyntaxException, IOException {
        Response authResponse = authenticateToAPI(deploymentUrl.toURI());

        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        final WebTarget target = ClientBuilder.newClient().target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_ALL_PATH));

        log.info("GET {}", target.getUri().getPath());

        Response documentResponse = target.register(LocalDateTimeSerializer.class)
                                          .register(LocalDateTimeDeserializer.class)
                                          .request(MediaType.APPLICATION_JSON)
                                          .cookie(authCookie)
                                          .get();

        String responseBody = documentResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(responseBody));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), documentResponse.getStatus());

        DocumentListResponse documentList = jsonUtil.deserialize(responseBody, DocumentListResponse.class);

        Assert.assertNotNull(documentList);
        Assert.assertFalse(documentList.getDocuments().isEmpty());

        boolean ownsDocument = false;
        for (DocumentListEntity entity : documentList.getDocuments()) {
            if (entity.getDocument().getName().equals("Lecture Java EE")) {
                ownsDocument = true;
                break;
            }
        }

        Assert.assertTrue(ownsDocument);
    }

    private Response authenticateToAPI(URI deploymentBaseURI) {
        final WebTarget target = ClientBuilder.newClient().target(deploymentBaseURI.resolve(API_PREFIX + TestConfig.AUTHENTICATION_LOGIN_PATH));

        log.info("POST {}", target.getUri().getPath());

        return target.request(MediaType.APPLICATION_JSON)
                     .post(Entity.json(new LoginModel(TestUsers.WILDFLY.getUsername(), TestUsers.WILDFLY.getPassword())));
    }
}
