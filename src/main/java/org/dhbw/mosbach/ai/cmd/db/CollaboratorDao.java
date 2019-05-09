package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.User;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.HasAccess;
import org.dhbw.mosbach.ai.cmd.util.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Dao class for collaborator interactions with the database
 *
 * @author 3040018
 */
@RequestScoped
public class CollaboratorDao {

    private static final Logger log = LoggerFactory.getLogger(CollaboratorDao.class);

    @PersistenceContext(unitName = CmdConfig.JPA_UNIT_NAME)
    private EntityManager em;

    public CollaboratorDao() {
        this.em = JpaFactory.getEntityManager();
    }

    /**
     * Add an entry to the doc table
     *
     * @param c Given doc object
     */
    @Transactional
    public void createCollaborator(Collaborator c) {

        this.em.persist(c);

        log.debug("Created a new collaborator entry in database");
    }

    /**
     * Get all the collaborators for a certain doc
     *
     * @param d Given doc
     * @return A list of collaborators found for that doc, null if no collaborators are found
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public List<Collaborator> getCollaboratorsForDoc(Doc d) {

        List<Collaborator> collaborators = new ArrayList<>();

        try {
            collaborators = (List<Collaborator>) this.em
                .createQuery("SELECT c FROM Collaborator c WHERE c.doc.id=:id AND c.hasAccess=:hasAccess")
                .setParameter("id", d.getId())
                .setParameter("hasAccess", HasAccess.Y)
                .getResultList();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }

        return collaborators;
    }

    /**
     * Get all the docs a certain given user collaborates on
     *
     * @param u Given user
     * @return A list of collaborations where the user has access, null if there are none
     */
    @SuppressWarnings("unchecked")
    public List<Collaborator> getCollaborationsForUser(User u) {

        List<Collaborator> collaborators = new ArrayList<>();

        try {
            collaborators = (List<Collaborator>) this.em
                .createQuery("SELECT c FROM Collaborator c WHERE c.user.id=:id AND c.hasAccess=:hasAccess")
                .setParameter("id", u.getId())
                .setParameter("hasAccess", HasAccess.Y)
                .getResultList();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }

        return collaborators;
    }

    /**
     * Get a certain collaborator entry based on the given doc and user
     *
     * @param u Given user
     * @param d Given doc
     * @return A collaborator object, if one is found, null otherwise
     */
    public Collaborator getCollaborator(User u, Doc d) {

        Collaborator collaborator = null;

        try {
            collaborator = (Collaborator) this.em
                .createQuery("SELECT c FROM Collaborator c WHERE c.user.id=:user_id AND c.doc.id=:doc_id AND c.hasAccess=:hasAccess")
                .setParameter("user_id", u.getId())
                .setParameter("doc_id", d.getId())
                .setParameter("hasAccess", HasAccess.Y)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return collaborator;
    }

    public Collaborator getCollaborator(int id) {
        Collaborator collaborator = null;

        try {
            collaborator = (Collaborator) this.em
                .createQuery("SELECT c FROM Collaborator c WHERE c.id = :collaborator_id")
                .setParameter("collaborator_id", id)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return collaborator;
    }

    /**
     * Update an existing collaborator entry
     *
     * @param c Given collaborator object
     * @return The number of updated rows
     */
    @Transactional
    public int updateCollaborator(Collaborator c) {

        log.debug("Updating collaborator: " + c.getId());

        return this.em.createQuery("UPDATE Collaborator c SET c.hasAccess=:hasAccess WHERE c.doc.id=:doc_id AND c.user.id=:user_id")
                      .setParameter("hasAccess", c.getHasAccess())
                      .setParameter("doc_id", c.getDoc().getId())
                      .setParameter("user_id", c.getUser().getId())
                      .executeUpdate();
    }

    /**
     * Remove an existing collaborator from the database
     * based on collaborator ID.
     *
     * @param c the collaborator to remove
     * @return the number of deleted rows
     */
    @Transactional
    public int removeCollaborator(Collaborator c) {
        return this.em.createQuery("DELETE FROM Collaborator c WHERE c.id = :collaboratorId")
                      .setParameter("collaboratorId", c.getId())
                      .executeUpdate();
    }
}
