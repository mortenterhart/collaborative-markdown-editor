package org.dhbw.mosbach.ai.cmd.db;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean class for user interactions with the database
 * @author 3040018
 */
@RequestScoped
public class UserDao implements Serializable{

	private static final long serialVersionUID = -5249679220763364046L;
	private static final Logger log = LoggerFactory.getLogger(UserDao.class);

	@PersistenceContext(unitName=CmdConfig.JPA_UNIT_NAME)
	private EntityManager em;

	public UserDao() {
		this.em = JpaFactory.getEntityManager();
	}

	/**
	 * Create a user to register them.
	 * @param u Given User object
	 */
	@Transactional
	public void createUser(User u) {

		this.em.persist(u);

		log.debug("Created a new user in database");
	}

	/**
	 * Get a user entry from the database based on the provided username.
	 * @param username Given username
	 * @return A User object, if one was found with the username, otherwise it returns null
	 */
	public User getUser(String username) {

		User user = null;

		try{
			user = (User) this.em
					.createQuery("FROM User u WHERE LOWER(u.name)=:username")
					.setParameter("username", username.toLowerCase())
					.getSingleResult();
		}
		catch (NoResultException e){
			return null;
		}

		return user;
	}
}