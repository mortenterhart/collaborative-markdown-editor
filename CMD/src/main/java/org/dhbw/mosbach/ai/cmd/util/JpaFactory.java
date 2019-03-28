package org.dhbw.mosbach.ai.cmd.util;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Factory to get an instance of the EntityManager.
 * @author 3040018
 */
public abstract class JpaFactory {
	
	private static EntityManager em;

	public static EntityManager getEntityManager() {
		if (em == null) {
			em = Persistence.createEntityManagerFactory(CmdConfig.PERSISTENCE_UNIT_NAME).createEntityManager();
		}
		return em;
	}
}