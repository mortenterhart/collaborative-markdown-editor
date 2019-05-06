package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.History;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Dao class for history interactions with the database
 *
 * @author 3040018
 */
@RequestScoped
public class HistoryDao {

    private static final Logger log = LoggerFactory.getLogger(HistoryDao.class);

    @PersistenceContext(unitName = CmdConfig.JPA_UNIT_NAME)
    private EntityManager em;

    public HistoryDao() {
        this.em = JpaFactory.getEntityManager();
    }

    /**
     * Add an entry to the history table
     * Checks if the latest available hash matches the current hash and
     * does not create a new entry if it does.
     *
     * @param h Given history object
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public void createHistory(History h) {
    	
    	for(History singleHistory : (List<History>) getLatestHistoryForDoc(h.getDoc())) {
    		if(singleHistory.getHash().equals(h.getHash()))
    			return;
    	}
    
        this.em.persist(h);

        log.debug("Created a new history entry in database");
    }

    /**
     * Get the latest history entry for a given document
     * @param d Given doc object
     * @return A history object, if an entry was found, null otherwise
     */
	public History getLatestHistoryForDoc(Doc d) {

    	History history = null;

        try {
        	history = (History) this.em
                   .createQuery("Select h FROM History h WHERE h.doc.id=:doc_id ORDER BY h.ctime DESC")
                   .setParameter("doc_id", d.getId())
                   .getResultList()
                   .get(0);
        } catch (NoResultException e) {
            return null;
        }

        return history;
    }
    
	/**
	 * Gets a full list of a documents history
	 * @param d Given document
	 * @return A list of history entries if there are any, null otherwise
	 */
    @SuppressWarnings("unchecked")
    @Transactional
	public List<History> getFullHistoryForDoc(Doc d) {
    	
    	List<History> fullHistory = new ArrayList<>();
    	
    	try {
    		fullHistory = (List<History>) this.em
                   .createQuery("Select h FROM History h WHERE h.doc.id=:doc_id ORDER BY h.ctime DESC")
                   .setParameter("doc_id", d.getId())
                   .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    	
    	return fullHistory;
    }
}
