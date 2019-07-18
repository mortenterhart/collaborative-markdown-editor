package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.services.helper.Authenticator;
import org.dhbw.mosbach.ai.cmd.services.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.services.helper.JsonUtil;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.response.DocumentListResponse;
import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentListEntity;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeDeserializer;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeSerializer;
import org.dhbw.mosbach.ai.cmd.testconfig.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.PackageIncludes;
import org.dhbw.mosbach.ai.cmd.testconfig.TestConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.TestUsers;
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

import javax.ws.rs.ApplicationPath;
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

/**
 * @author 6694964
 * @version 1.4
 */
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

    @BeforeClass
    public static void initResteasyClient() {
        System.out.println("Initializing RESTEasy client");
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    @Test
    @UsingDataSet("datasets/documents.yml")
    public void testGetAllDocuments() throws URISyntaxException {
        // First authenticate to the application in order to use
        // the document APIs
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.WILDFLY);

        // Log the response of the authentication
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        // Check if the authentication was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        // Fetch the JSESSIONID authentication cookie from the response
        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        // Check that the cookie is not null
        Assert.assertNotNull(authCookie);

        // Build a web target for the /document/all API
        final WebTarget target = ClientBuilder.newClient().target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_ALL_PATH));

        log.info("GET {}", target.getUri().getPath());

        // Invoke a GET request to the web target including the
        // authentication cookie
        Response documentResponse = target.register(LocalDateTimeSerializer.class)
                                          .register(LocalDateTimeDeserializer.class)
                                          .request(MediaType.APPLICATION_JSON)
                                          .cookie(authCookie)
                                          .get();

        // Read and log the response body
        String responseBody = documentResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(responseBody));

        // Check if the invocation was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), documentResponse.getStatus());

        // Deserialize the JSON response
        DocumentListResponse documentList = jsonUtil.deserialize(responseBody, DocumentListResponse.class);

        // Check that the document list is non-null and not empty
        Assert.assertNotNull(documentList);
        Assert.assertFalse(documentList.getDocuments().isEmpty());

        // Check whether the response contains some documents of the user
        Assert.assertTrue(checkResponseContainsDocument(documentList, "Lecture Java EE"));
        Assert.assertTrue(checkResponseContainsDocument(documentList, "Testdocument"));
        Assert.assertFalse(checkResponseContainsDocument(documentList, "Collaborative Markdown Editor"));
    }

    @Test
    @UsingDataSet("datasets/documents.yml")
    public void testAddDocument() throws URISyntaxException {
        // First authenticate to the application in order to use
        // the document APIs
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.JACKSON);

        // Log the response of the authentication
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        // Check if the authentication was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        // Fetch the JSESSIONID authentication cookie from the response
        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        // Check that the cookie is not null
        Assert.assertNotNull(authCookie);

        // Add a new document with this name
        String documentName = "JAX-RS with Jackson";

        // Build a client and a web target for the /document/add API
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_ADD_PATH));

        // Invoke a POST request to the web target including
        // the authentication cookie
        Response response = target.request(MediaType.APPLICATION_JSON)
                                  .cookie(authCookie)
                                  .post(Entity.json(new DocumentInsertionModel(documentName)));

        // Read and log the response body
        String responseBody = response.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(responseBody));

        // Check that the document insertion was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Get all documents from the other API to verify
        // that the document has been added
        DocumentListResponse documentList = retrieveAllDocuments(authCookie);

        // Check if the document list is non-null and not empty
        Assert.assertNotNull(documentList);
        Assert.assertFalse(documentList.getDocuments().isEmpty());

        // Check that the new document is contained in the response of
        // the /document/all API
        Assert.assertTrue(checkResponseContainsDocument(documentList, documentName));
    }

    @Test
    @UsingDataSet("datasets/documents.yml")
    public void testRemoveDocument() throws URISyntaxException {
        // First authenticate to the application in order to use
        // the document APIs
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.APP_USER);

        // Log the response of the authentication
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        // Check if the authentication was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        // Fetch the JSESSIONID authentication cookie from the response
        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        // Check that the cookie is not null
        Assert.assertNotNull(authCookie);

        // Remove the document with this document id
        int documentId = 1;

        // Build a client and a web target for the /document/remove API
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_REMOVE_PATH));

        // Invoke a DELETE request to the web target including
        // the authentication cookie in order to remove the
        // corresponding document
        Response removeResponse = target.request(MediaType.APPLICATION_JSON)
                                        .cookie(authCookie)
                                        .build(HttpMethod.DELETE, Entity.json(new DocumentRemovalModel(documentId)))
                                        .invoke();

        // Read and log the response body
        String removeResponseBody = removeResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(removeResponseBody));

        // Assert the document removal was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), removeResponse.getStatus());

        // Retrieve all documents from the other API to verify
        // that the removed document is not returned
        DocumentListResponse documentList = retrieveAllDocuments(authCookie);

        // Check if the document list is non-null and not empty
        Assert.assertNotNull(documentList);
        Assert.assertFalse(documentList.getDocuments().isEmpty());

        // Check if response does not contain the document
        Assert.assertFalse(checkResponseContainsDocument(documentList, documentId));
    }

    private DocumentListResponse retrieveAllDocuments(Cookie authCookie) throws URISyntaxException {
        // Check that the authentication cookie is not null
        Assert.assertNotNull(authCookie);

        // Build a new web target for the /document/all API
        final WebTarget documentTarget = ClientBuilder.newClient().target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_ALL_PATH));
        JsonUtil jsonUtil = new JsonUtil();

        // Invoke a GET request to the web target including
        // the authentication cookie in order to get all documents
        Response documentResponse = documentTarget.request(MediaType.APPLICATION_JSON)
                                                  .cookie(authCookie)
                                                  .get();

        // Read and log the response body
        String documentResponseBody = documentResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(documentResponseBody));

        // Check if the document retrieval was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), documentResponse.getStatus());

        // Deserialize the JSON response
        return jsonUtil.deserialize(documentResponseBody, DocumentListResponse.class);
    }

    private boolean checkResponseContainsDocument(DocumentListResponse response, String documentName) {
        for (DocumentListEntity entity : response.getDocuments()) {
            if (entity.getDocument().getName().equals(documentName)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkResponseContainsDocument(DocumentListResponse response, int documentId) {
        for (DocumentListEntity entity : response.getDocuments()) {
            if (entity.getDocument().getId() == documentId) {
                return true;
            }
        }

        return false;
    }
}
