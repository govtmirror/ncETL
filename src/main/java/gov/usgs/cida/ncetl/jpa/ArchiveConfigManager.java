package gov.usgs.cida.ncetl.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

public class ArchiveConfigManager implements ConfigFetcherI {

    @PersistenceContext
	private EntityManager em;
	
	private TypedQuery<ArchiveConfig> q;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
		this.q = em.createQuery("select s from ArchiveConfig s where s.rfc_code = ?", ArchiveConfig.class);
	}
	
	public synchronized ArchiveConfig fetch(int rfc) {
		q.setParameter(1, rfc);
		return q.getSingleResult();
	}
	
	public List<ArchiveConfig> all() {
		TypedQuery<ArchiveConfig> q = em.createQuery("select s from ArchiveConfig s ", gov.usgs.cida.ncetl.jpa.ArchiveConfig.class);
		
		return q.getResultList();
	}
}
