package gov.usgs.cida.data.grib;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.integration.annotation.Transformer;
import org.springframework.transaction.annotation.Transactional;

public class HistoryRecorder {

	@PersistenceContext
	private EntityManager em;
	
	@Transformer
	@Transactional
	public ArchiveConfig addSuccess(ArchiveConfig arc) {
		ArchiveConfig vv = em.merge(arc);
		
		vv.addHistory("success");
		
		return vv;
	}
	
}
