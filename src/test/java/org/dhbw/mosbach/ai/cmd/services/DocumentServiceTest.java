package org.dhbw.mosbach.ai.cmd.services;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.AFTER)
public class DocumentServiceTest {

    @Deployment
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

        System.out.println(archive.toString(true));

        return archive;
    }

    @Inject
    private DocumentService service;

    @Test
    public void documentServiceTest() {
        Assert.assertNotNull(service);
    }
}
