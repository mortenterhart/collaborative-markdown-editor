package org.dhbw.mosbach.ai.cmd.db;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dao class for doc interactions with the database
 *
 * @author 3040018
 */
@RequestScoped
public class DocDao {
	
	private static final Logger log = LoggerFactory.getLogger(DocDao.class);
	
	@PersistenceContext(unitName = CmdConfig.JPA_UNIT_NAME)
    private EntityManager em;
	
	@Inject
	CollaboratorDao collaboratorDao;
	
    public DocDao() {
        this.em = JpaFactory.getEntityManager();
    }

    /**
     * Add an entry to the doc table
     *
     * @param d Given doc object
     */
    @Transactional
    public void createDoc(Doc d) {

        this.em.persist(d);

        log.debug("Created a new doc in database");
    }

    /**
     * Get a single document from the database based on the given id
     * @param id Given document id
     * @return A doc object if one was found, null otherwise
     */
	public Doc getDoc(int id) {

    	Doc doc = null;

        try {
        	doc = (Doc) this.em
                   .createQuery("FROM Doc d WHERE d.id=:doc_id")
                   .setParameter("doc_id", id)
                   .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return doc;
    }
	
	/**
	 * Get all the documents where the given user is the owner or a collaborator
	 * @param u Given user
	 * @return A list of docs, null if nothing is found
	 */
	@SuppressWarnings("unchecked")
	public List<Doc> getDocsForUser(User u) {
		
		List<Doc> docs = new ArrayList<>();
		
		try {
			
			List<Collaborator> collaborations = collaboratorDao.getCollaborationsForUser(u);
			
    		docs = (List<Doc>) this.em
                   .createQuery("FROM Doc d WHERE (d.owner.id=:user_id OR d.id IN :collaborations.doc.id) ORDER BY d.ctime DESC")
                   .setParameter("user_id", u.getId())
                   .setParameter("collaborations", collaborations)
                   .getResultList();
        } catch (NoResultException e) {
            return null;
        }
		
		return docs.isEmpty() ? docs : null;
	}
	
	/**
	 * Update a certain document in the database
	 * @param d Given doc object
	 * @param u Given user who initiated the update
	 */
	public void updateDoc(Doc d, User u) {
		
		this.em.createQuery("UPDATE Doc d SET d.content:=content, d.uuser:=uuser WHERE d.id:=id")
		.setParameter("content", d.getContent())
		.setParameter("uuser", u.getId())
		.setParameter("id", d.getId());
		
		log.debug("Updated document: " + d.getId());
	}
}
