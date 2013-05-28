package gov.usgs.cida.ncetl.mocks;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;
import gov.usgs.cida.ncetl.jpa.ConfigFetcherI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockSpecManager implements ConfigFetcherI {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/* (non-Javadoc)
	 * @see gov.usgs.cida.ncetl.mocks.SpecFetcherI#fetch(int)
	 */
	@Override
	public  ArchiveConfig fetch(int rfc) {
		logger.info("spec for {}", rfc);
		return new ArchiveConfig();
	}
}
