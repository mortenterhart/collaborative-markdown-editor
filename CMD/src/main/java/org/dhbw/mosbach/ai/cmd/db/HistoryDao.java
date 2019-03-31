package org.dhbw.mosbach.ai.cmd.db;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.dhbw.mosbach.ai.cmd.model.History;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dao class for history interactions with the database
 * @author 3040018
 */
@RequestScoped
public class HistoryDao {

	private static final Logger log = LoggerFactory.getLogger(HistoryDao.class);

	@PersistenceContext(unitName=CmdConfig.JPA_UNIT_NAME)
	private EntityManager em;

	public HistoryDao() {
		this.em = JpaFactory.getEntityManager();
	}
	
	/**
	 * Add an entry to the history table
	 * @param h Given history object
	 */
	@Transactional
	public void createHistory(History h) {

		this.em.persist(h);

		log.debug("Created a new user in database");
	}
}
