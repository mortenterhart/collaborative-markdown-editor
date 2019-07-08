package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.services.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.services.helper.JsonUtil;
import org.dhbw.mosbach.ai.cmd.services.payload.LoginModel;
import org.dhbw.mosbach.ai.cmd.testconfig.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.PackageIncludes;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.AFTER)
//@RunAsClient
public class AuthenticationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceTest.class);

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME/*, testable = false*/)
    public static WebArchive createDeployment() {
        WebArchive archive = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                               .addMavenRuntimeAndTestDependencies()
                                               .addBeansAndPersistenceDefinition()
                                               .addTestResources()
                                               .addPackages(PackageIncludes.AUTHENTICATION_SERVICE)
                                               .packageWebArchive();

        log.info(archive.toString(true));

        return archive;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Test
    @UsingDataSet("datasets/users.yml")
    public void testDoLogin() throws URISyntaxException {
        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(deploymentUrl.toURI().resolve("api/authentication/login"));

        Invocation postRequest = target.request().accept(MediaType.APPLICATION_JSON_TYPE).buildPost(Entity.json(new LoginModel("testuser", "Testuser1")));
        Response response = postRequest.invoke();

        String responseBody = response.readEntity(String.class);
        JsonUtil jsonUtil = new JsonUtil();
        log.info(jsonUtil.prettyPrint(responseBody));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
