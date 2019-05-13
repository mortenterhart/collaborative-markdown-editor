package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Dao class for repo interactions with the database
 *
 * @author 3040018
 */
@Dependent
public class RepoDao {

    private static final Logger log = LoggerFactory.getLogger(RepoDao.class);

    @PersistenceContext(unitName = CmdConfig.JPA_UNIT_NAME)
    private EntityManager em;

    /**
     * Create a repo entry in the database for a user
     *
     * @param repo Given repo object
     */
    @Transactional
    public void createRepo(Repo repo) {

        this.em.persist(repo);

        log.debug("Created a repo entry");
    }

    /**
     * Get a repo from the database based on the given user
     *
     * @param user Given user
     * @return A Repo object, if one was found with the user id, otherwise it returns null
     */
    public Repo getRepo(User user) {

        Repo repo = null;

        try {
            repo = (Repo) this.em
                .createQuery("SELECT r FROM Repo r WHERE r.owner.id=:userid")
                .setParameter("userid", user.getId())
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return repo;
    }
}
