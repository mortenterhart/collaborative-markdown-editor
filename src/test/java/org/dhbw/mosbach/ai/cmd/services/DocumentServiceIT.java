package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.services.helper.Authenticator;
import org.dhbw.mosbach.ai.cmd.services.helper.JsonUtil;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentAccessModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentTransferModel;
import org.dhbw.mosbach.ai.cmd.services.response.DocumentListResponse;
import org.dhbw.mosbach.ai.cmd.services.response.entity.DocumentListEntity;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeDeserializer;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeSerializer;
import org.dhbw.mosbach.ai.cmd.test.config.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.test.config.TestConfig;
import org.dhbw.mosbach.ai.cmd.test.config.TestUser;
import org.dhbw.mosbach.ai.cmd.test.config.TestUsers;
import org.dhbw.mosbach.ai.cmd.test.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.test.include.PackageIncludes;
import org.dhbw.mosbach.ai.cmd.test.resources.Datasets;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.DataSeedStrategy;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.SeedDataUsing;
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

import static org.dhbw.mosbach.ai.cmd.test.config.TestConfig.API_PREFIX;

/**
 * @author 6694964
 * @version 1.4
 */
@RunWith(Arquillian.class)
@DataSource(DeploymentConfig.DEFAULT_DATA_SOURCE)
@SeedDataUsing(DataSeedStrategy.CLEAN_INSERT)
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_TABLES_ONLY)
public class DocumentServiceIT {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceIT.class);

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME)
    public static WebArchive createDeployment() {
        final WebArchive war = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                                 .addMavenRuntimeAndTestDependencies()
                                                 .addBeansAndPersistenceDefinition()
                                                 .addTestResources()
                                                 .addPackages(PackageIncludes.DOCUMENT_SERVICE)
                                                 .packageWebArchive();

        log.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @BeforeClass
    public static void initResteasyClient() {
        log.info("Initializing RESTEasy client");
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
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
    @UsingDataSet(Datasets.DOCUMENTS)
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

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
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
    @UsingDataSet(Datasets.DOCUMENTS)
    public void testCollaboratorHasGrantedDocumentAccess() throws URISyntaxException {
        // First authenticate to the application in order to use
        // the document APIs
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.VUEJS);

        // Log the response of the authentication
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        // Check if the authentication was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        // Fetch the JSESSIONID authentication cookie from the response
        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        // Check that the cookie is not null
        Assert.assertNotNull(authCookie);

        // Verify that the authenticated user has access to document 1
        int documentId = 1;

        // Build a client and a web target for the /document/hasAccess API
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_ACCESS_PATH));

        // Invoke a POST request to the web target including
        // the authentication cookie in order to check for
        // access to document 1
        Response accessResponse = target.request(MediaType.APPLICATION_JSON)
                                        .cookie(authCookie)
                                        .post(Entity.json(new DocumentAccessModel(documentId)));

        // Read and log the response body
        String accessResponseBody = accessResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(accessResponseBody));

        // Check that the invocation was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), accessResponse.getStatus());
    }

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
    public void testOwnerHasGrantedDocumentAccess() throws URISyntaxException {
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

        // Verify that the authenticated user has access to document 1
        int documentId = 2;

        // Build a client and a web target for the /document/hasAccess API
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_ACCESS_PATH));

        // Invoke a POST request to the web target including
        // the authentication cookie in order to check for
        // access to document 1
        Response accessResponse = target.request(MediaType.APPLICATION_JSON)
                                        .cookie(authCookie)
                                        .post(Entity.json(new DocumentAccessModel(documentId)));

        // Read and log the response body
        String accessResponseBody = accessResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(accessResponseBody));

        // Check that the invocation was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), accessResponse.getStatus());
    }

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
    public void testUserHasDeniedDocumentAccess() throws URISyntaxException {
        // First authenticate to the application in order to use
        // the document APIs
        Response authResponse = Authenticator.authenticate(deploymentUrl.toURI(), TestUsers.VUEJS);

        // Log the response of the authentication
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(authResponse.readEntity(String.class)));

        // Check if the authentication was successful
        Assert.assertEquals(Response.Status.OK.getStatusCode(), authResponse.getStatus());

        // Fetch the JSESSIONID authentication cookie from the response
        Cookie authCookie = authResponse.getCookies().get(TestConfig.JSESSIONID);

        // Check that the cookie is not null
        Assert.assertNotNull(authCookie);

        // Verify that the authenticated user has access to document 1
        int documentId = 3;

        // Build a client and a web target for the /document/hasAccess API
        Client client = ClientBuilder.newClient();
        final WebTarget target = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_ACCESS_PATH));

        // Invoke a POST request to the web target including
        // the authentication cookie in order to check for
        // access to document 1
        Response accessResponse = target.request(MediaType.APPLICATION_JSON)
                                        .cookie(authCookie)
                                        .post(Entity.json(new DocumentAccessModel(documentId)));

        // Read and log the response body
        String accessResponseBody = accessResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(accessResponseBody));

        // Check that the invocation was successful
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), accessResponse.getStatus());
    }

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
    public void testTransferOwnership() throws URISyntaxException {
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

        Assert.assertNotNull(authCookie);

        int documentId = 1;
        TestUser newOwner = TestUsers.JACKSON;

        // Build a client and a web target for the /document/transferOwnership API
        Client client = ClientBuilder.newClient();
        final WebTarget transferTarget = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.DOCUMENT_TRANSFER_PATH));

        // Invoke a PATCH request to the web target including
        // the authentication cookie to transfer the ownership
        Response transferResponse = transferTarget.request(MediaType.APPLICATION_JSON)
                                                  .cookie(authCookie)
                                                  .method(HttpMethod.PATCH, Entity.json(new DocumentTransferModel(documentId, newOwner.getName())));

        // Read and log the response body
        String transferResponseBody = transferResponse.readEntity(String.class);
        log.info(jsonUtil.prettyPrint(transferResponseBody));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), transferResponse.getStatus());

        DocumentListResponse appUserDocuments = retrieveAllDocuments(authCookie);

        Assert.assertFalse(checkResponseContainsDocument(appUserDocuments, documentId));

        final WebTarget logoutTarget = client.target(deploymentUrl.toURI().resolve(API_PREFIX + TestConfig.AUTHENTICATION_LOGOUT_PATH));

        Response logoutResponse = logoutTarget.request(MediaType.APPLICATION_JSON)
                                              .cookie(authCookie)
                                              .post(null);

        log.info(jsonUtil.prettyPrint(logoutResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), logoutResponse.getStatus());

        Response verifyAuthResponse = Authenticator.authenticate(deploymentUrl.toURI(), newOwner);

        // Log the response of the authentication
        log.info(jsonUtil.prettyPrint(verifyAuthResponse.readEntity(String.class)));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), verifyAuthResponse.getStatus());

        Cookie verifyAuthCookie = verifyAuthResponse.getCookies().get(TestConfig.JSESSIONID);

        Assert.assertNotNull(verifyAuthCookie);

        DocumentListResponse jacksonDocuments = retrieveAllDocuments(verifyAuthCookie);

        Assert.assertTrue(checkResponseContainsDocument(jacksonDocuments, documentId));

        Doc transferredDocument = getDocumentFromResponse(jacksonDocuments, documentId);

        Assert.assertNotNull(transferredDocument);
        Assert.assertEquals(newOwner.getName(), transferredDocument.getRepo().getOwner().getName());
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
        return getDocumentFromResponse(response, documentId) != null;
    }

    private Doc getDocumentFromResponse(DocumentListResponse response, int documentId) {
        for (DocumentListEntity entity : response.getDocuments()) {
            Doc document = entity.getDocument();
            if (document.getId() == documentId) {
                return document;
            }
        }

        return null;
    }
}
