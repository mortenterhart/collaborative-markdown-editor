package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URL;

@RunWith(Arquillian.class)
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_TABLES_ONLY)
public class UserDaoTest {

    private static final Logger log = LoggerFactory.getLogger(UserDaoTest.class);

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "cmd.war")
                                       .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                                                            .importRuntimeAndTestDependencies()
                                                            .resolve()
                                                            .withTransitivity()
                                                            .asFile())
                                       .addAsResource("META-INF/beans.xml")
                                       .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                                       .addAsResource("META-INF/log4j.properties")
                                       .addPackages(true, "org/dhbw/mosbach/ai/cmd");

        log.info(war.toString(true));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Inject
    private UserDao userDao;

    @Test
    @UsingDataSet("users.yml")
    public void testGetUserByName() {
        User user = userDao.getUserByName("testuser");

        Assert.assertNotNull(user);
    }
}
