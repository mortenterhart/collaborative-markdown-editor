package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.test.config.DeploymentConfig;
import org.dhbw.mosbach.ai.cmd.test.config.TestUser;
import org.dhbw.mosbach.ai.cmd.test.config.TestUsers;
import org.dhbw.mosbach.ai.cmd.test.helper.DeploymentPackager;
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

@RunWith(Arquillian.class)
@DataSource(DeploymentConfig.DEFAULT_DATA_SOURCE)
@SeedDataUsing(DataSeedStrategy.CLEAN_INSERT)
@Transactional(TransactionMode.COMMIT)
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.STRICT)
@ApplyScriptAfter({ Scripts.DISABLE_REFERENTIAL_INTEGRITY, Scripts.RESET_TABLE_IDENTITIES })
@PersistenceTest
public class DocDaoIT {

    private static final Logger log = LoggerFactory.getLogger(DocDaoIT.class);

    @Deployment(name = DeploymentConfig.DEPLOYMENT_NAME)
    public static WebArchive createDeployment() {
        final WebArchive war = DeploymentPackager.createDeployment(DeploymentConfig.DEPLOYMENT_NAME)
                                                 .addBeansAndPersistenceDefinition()
                                                 .addTestResources()
                                                 .addPackages(PackageIncludes.DOC_DAO)
                                                 .packageWebArchive();

        log.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @ArquillianResource
    private URL deploymentUrl;

    @Inject
    private DocDao docDao;

    @Inject
    private RepoDao repoDao;

    @Before
    public void printDeploymentInfo() {
        log.info("Deployed at {}", deploymentUrl.toString());
    }

    @Test
    @UsingDataSet({ Datasets.USERS, Datasets.REPOS })
    public void testCreateDoc() {
        TestUser owner = TestUsers.VUEJS;
        Repo repo = repoDao.getRepo(owner);
        System.out.println(owner);
        System.out.println(repo);

        Doc doc = new Doc();
        doc.setName("Arquillian Integration Test");
        doc.setContent("This is an Arquillian integration test");
        doc.setRepo(repo);
        doc.setCuser(owner);
        doc.setUuser(owner);

        docDao.createDoc(doc);

        Doc loadedDoc = docDao.getDoc(doc.getId());

        Assert.assertNotNull(loadedDoc);
        Assert.assertEquals(doc.getId(), loadedDoc.getId());
        Assert.assertEquals(doc.getName(), loadedDoc.getName());
        Assert.assertEquals(doc.getContent(), loadedDoc.getContent());
        Assert.assertEquals(doc.getRepo(), loadedDoc.getRepo());
        Assert.assertEquals(doc.getCuser(), loadedDoc.getCuser());
        Assert.assertEquals(doc.getUuser(), loadedDoc.getUuser());
    }

    @Test
    @UsingDataSet(Datasets.DOCUMENTS)
    public void testGetDoc() {

    }
}
