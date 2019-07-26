package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.test.config.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.test.config.TestConfig;
import org.dhbw.mosbach.ai.cmd.test.config.TestUser;
import org.dhbw.mosbach.ai.cmd.test.config.TestUsers;
import org.dhbw.mosbach.ai.cmd.test.helper.DeploymentPackager;
import org.dhbw.mosbach.ai.cmd.test.helper.PasswordGenerator;
import org.dhbw.mosbach.ai.cmd.test.include.DependencyIncludes;
import org.dhbw.mosbach.ai.cmd.test.include.PackageIncludes;
import org.dhbw.mosbach.ai.cmd.test.resources.Datasets;
import org.dhbw.mosbach.ai.cmd.test.resources.Scripts;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.DataSeedStrategy;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.SeedDataUsing;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URL;

/**
 * @author 6694964
 * @version 1.2
 */
@RunWith(Arquillian.class)
@DataSource(DeploymentConfig.DEFAULT_DATA_SOURCE)
@Transactional(TransactionMode.COMMIT)
@SeedDataUsing(DataSeedStrategy.CLEAN_INSERT)
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.STRICT)
@ApplyScriptAfter({ Scripts.DISABLE_REFERENTIAL_INTEGRITY, Scripts.RESET_TABLE_IDENTITIES })
@PersistenceTest
public class UserDaoIT {

    private static final Logger log = LoggerFactory.getLogger(UserDaoIT.class);

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME)
    public static WebArchive createDeployment() {
        final WebArchive war = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                                 .addBeansAndPersistenceDefinition()
                                                 .addTestResources()
                                                 .addPackages(PackageIncludes.USER_DAO)
                                                 .addMavenDependencies(DependencyIncludes.USER_DAO)
                                                 .packageWebArchive();

        log.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Inject
    private UserDao userDao;

    @Inject
    private RepoDao repoDao;

    @Inject
    private Hashing hashing;

    @Before
    public void printDeploymentInfo() {
        log.info("Deployed at {}", deploymentUrl.toString());
    }

    @Test
    public void testCreateUserVerifyAttributes() {
        TestUser sourceUser = TestUsers.ADMIN;

        User user = new User();
        user.setName(sourceUser.getName());
        user.setMail(sourceUser.getMail());

        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generateSecurePassword(TestConfig.PASSWORD_LENGTH);
        user.setPassword(hashing.hashPassword(password));

        userDao.createUser(user);

        User loadedUser = userDao.getUserByName(user.getName());

        Assert.assertNotNull(loadedUser);
        Assert.assertEquals(user.getName(), loadedUser.getName());
        Assert.assertEquals(user.getMail(), loadedUser.getMail());
        Assert.assertEquals(user.getPassword(), loadedUser.getPassword());
        Assert.assertTrue(hashing.checkPassword(password, loadedUser.getPassword()));

        Repo repository = repoDao.getRepo(loadedUser);

        Assert.assertNotNull(repository);
        Assert.assertEquals(loadedUser.getId(), repository.getId());
        Assert.assertEquals(loadedUser, repository.getOwner());
    }

    @Test
    @UsingDataSet(Datasets.USERS)
    public void testGetUserById() {
        TestUser sourceUser = TestUsers.JACKSON;

        User loadedUser = userDao.getUserById(sourceUser.getId());

        Assert.assertNotNull(loadedUser);
        Assert.assertEquals(sourceUser.getId(), loadedUser.getId());
        Assert.assertEquals(sourceUser.getName(), loadedUser.getName());
        Assert.assertEquals(sourceUser.getMail(), loadedUser.getMail());
        Assert.assertTrue(hashing.checkPassword(sourceUser.getPassword(), loadedUser.getPassword()));
    }

    @Test
    @UsingDataSet(Datasets.USERS)
    public void testGetUserByName() {
        TestUser sourceUser = TestUsers.JACKSON;

        User user = userDao.getUserByName(sourceUser.getName());

        Assert.assertNotNull(user);
        Assert.assertEquals(sourceUser.getName(), user.getName());
        Assert.assertEquals(sourceUser.getMail(), user.getMail());
        Assert.assertTrue(hashing.checkPassword(sourceUser.getPassword(), user.getPassword()));
    }

    @Test
    @UsingDataSet(Datasets.USERS)
    public void testUpdateUser() {
        TestUser user = TestUsers.JACKSON.clone();

        String name = "jpa";
        String mail = "hibernate@jpa.com";
        String password = new PasswordGenerator().generateSecurePassword(TestConfig.PASSWORD_LENGTH);

        user.setName(name);
        user.setMail(mail);
        user.setPassword(hashing.hashPassword(password));

        userDao.updateUser(user);

        User loadedUser = userDao.getUserById(user.getId());

        Assert.assertNotNull(loadedUser);
        Assert.assertNotNull(loadedUser.getCtime());
        Assert.assertNotNull(loadedUser.getUtime());
        Assert.assertEquals(user.getId(), loadedUser.getId());
        Assert.assertEquals(user.getName(), loadedUser.getName());
        Assert.assertEquals(user.getMail(), loadedUser.getMail());
        Assert.assertEquals(user.getPassword(), loadedUser.getPassword());
        Assert.assertTrue(hashing.checkPassword(password, loadedUser.getPassword()));
    }
}
