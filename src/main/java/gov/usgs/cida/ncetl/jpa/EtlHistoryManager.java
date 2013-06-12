package gov.usgs.cida.ncetl.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// @Repository
public class EtlHistoryManager {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext
	private EntityManager em;
		
	// Transaction propagation REQUIRES_NEW so error will get recorded even though this is in error chain 
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void recordError(
			String msg,
			int rfc) {
						
		Query direct = em.createNativeQuery("insert into ETL_HISTORY(ARCHIVE_ID, OUTCOME) " +
		" values ((select ID from ARCHIVE_CONFIG where RFC_CODE = ?), ?)");
		
		direct.setParameter(1, rfc);
		direct.setParameter(2, gov.usgs.cida.ncetl.jpa.EtlHistory.trimOutcome(msg));
		
		int ct = direct.executeUpdate();
		
		logger.trace("insert result is {}", ct);
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void recordOk(ArchiveConfig cfg, String msg) {
		cfg = em.merge(cfg);
		cfg.addHistory(msg);
		em.flush();
	}
}
