package org.dhbw.mosbach.ai.cmd.db;

import org.dhbw.mosbach.ai.cmd.model.Collaborator;
import org.dhbw.mosbach.ai.cmd.model.Doc;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private CollaboratorDao collaboratorDao;

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
     *
     * @param id Given document id
     * @return A doc object if one was found, null otherwise
     */
    public Doc getDoc(int id) {

        Doc doc = null;

        try {
            doc = (Doc) this.em
                .createQuery("SELECT d FROM Doc d WHERE d.id=:doc_id")
                .setParameter("doc_id", id)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return doc;
    }

    /**
     * Get all the documents where the given user is the owner
     *
     * @param u Given user
     * @return A list of docs, null if nothing is found
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public List<Doc> getDocsOwnedBy(User u) {

        List<Doc> docs = new ArrayList<>();

        try {
            docs = (List<Doc>) this.em
                .createQuery("SELECT d FROM Doc d WHERE d.repo.owner.id=:user_id ORDER BY d.ctime DESC")
                .setParameter("user_id", u.getId())
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }

        return docs;
    }

    /**
     * Get all the documents where the given user is a collaborator
     *
     * @param u Given user
     * @return A list of docs, null if nothing is found
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public List<Doc> getDocsCollaboratedBy(User u) {

        List<Doc> docs = new ArrayList<>();

        try {
            docs = (List<Doc>) this.em
                .createQuery("SELECT d FROM Doc d, Collaborator c WHERE d.id = c.doc.id AND c.user.id = :userId ORDER BY d.ctime DESC")
                .setParameter("userId", u.getId())
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }

        return docs;
    }

    /**
     * Utility program to turn the list of collaborator objects into a list of doc ids
     *
     * @param collaborations Given list of collaborator objects
     * @return A list of integers, containing the doc ids
     */
    private List<Integer> getIdListFromCollaborator(List<Collaborator> collaborations) {
        return collaborations.stream().map(Collaborator::getDoc).collect(Collectors.toList()).stream().map(Doc::getId).collect(Collectors.toList());
    }

    /**
     * Update a certain document in the database
     *
     * @param d Given doc object
     * @param u Given user who initiated the update
     * @return The number of updated rows
     */
    @Transactional
    public int updateDoc(Doc d, User u) {

        log.debug("Updated document: " + d.getId());

        return this.em.createQuery("UPDATE Doc d SET d.content=:content, d.uuser=:uuser WHERE d.id=:id")
                      .setParameter("content", d.getContent())
                      .setParameter("uuser", u.getId())
                      .setParameter("id", d.getId())
                      .executeUpdate();
    }

    @Transactional
    public int transferRepo(Doc d) {

        log.debug("Updated document: " + d.getId());

        return this.em.createQuery("UPDATE Doc d SET d.repo=:repo, d.uuser=:uuser WHERE d.id=:id")
                      .setParameter("repo", d.getRepo())
                      .setParameter("uuser", d.getUuser())
                      .setParameter("id", d.getId())
                      .executeUpdate();
    }
}
