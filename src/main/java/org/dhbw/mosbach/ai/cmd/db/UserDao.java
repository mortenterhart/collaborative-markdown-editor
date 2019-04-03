package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.Repo;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Dao class for user interactions with the database
 *
 * @author 3040018
 */
@RequestScoped
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    @PersistenceContext(unitName = CmdConfig.JPA_UNIT_NAME)
    private EntityManager em;

    @Inject
    private RepoDao repoDao;

    public UserDao() {
        this.em = JpaFactory.getEntityManager();
    }

    /**
     * Create a user to register them and also create a repo for them.
     *
     * @param u Given User object
     */
    @Transactional
    public void createUser(User u) {

        this.em.persist(u);

        Repo repo = new Repo();
        repo.setOwner(u);

        repoDao.createRepo(repo);

        log.debug("Created a new user in database");
    }

    /**
     * Get a user entry from the database based on the provided username.
     *
     * @param username Given username
     * @return A User object, if one was found with the username, otherwise it returns null
     */
    public User getUserByName(String username) {

        User user = null;

        try {
            user = (User) this.em
                .createQuery("SELECT u FROM User u WHERE LOWER(u.name)=:username")
                .setParameter("username", username.toLowerCase())
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return user;
    }
    
    /**
     * Get a user entry from the database based on the provided id.
     *
     * @param id Given id
     * @return A User object, if one was found with the id, otherwise it returns null
     */
    public User getUserById(int id) {

        User user = null;

        try {
            user = (User) this.em
                .createQuery("SELECT u FROM User u WHERE u.id=:id")
                .setParameter("id", id)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return user;
    }
}
