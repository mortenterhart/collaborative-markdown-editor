package org.dhbw.mosbach.ai.cmd.services;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.AFTER)
@RunAsClient
public class AuthenticationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceTest.class);

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "cmd.war")
                                       .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                                                            .importRuntimeAndTestDependencies()
                                                            .resolve()
                                                            .withTransitivity()
                                                            .asFile())
                                       .addAsResource("META-INF/beans.xml")
                                       .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                                       .addAsResource("META-INF/log4j.properties")
                                       .addPackages(true, "org/dhbw/mosbach/ai/cmd");

        log.info(archive.toString(true));

        return archive;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Test
    @Ignore
    @UsingDataSet("datasets/users.yml")
    public void testDoLogin() throws URISyntaxException {
        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(deploymentUrl.toURI().resolve("api/authentication/login"));

        Invocation postRequest = target.request().accept(MediaType.APPLICATION_JSON_TYPE).buildGet();
        Response response = postRequest.invoke();

        Assert.assertEquals(200, response.getStatus());
    }
}
