package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.services.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.testconfig.Datasets;
import org.dhbw.mosbach.ai.cmd.testconfig.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.PackageIncludes;
import org.dhbw.mosbach.ai.cmd.testconfig.TestUsers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME)
    public static WebArchive createDeployment() {
        WebArchive war = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                           .addBeansAndPersistenceDefinition()
                                           .addTestResources()
                                           .addPackages(PackageIncludes.USER_DAO)
                                           .packageWebArchive();

        log.info(war.toString(true));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Inject
    private UserDao userDao;

    @Test
    @UsingDataSet(Datasets.USERS)
    public void testGetUserByName() {
        log.info("Deployed under {}", deploymentUrl.toExternalForm());
        User user = userDao.getUserByName(TestUsers.JACKSON.getName());

        Assert.assertNotNull(user);
    }
}
