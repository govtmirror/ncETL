package gov.usgs.cida.data.grib;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

public class ArchiveException extends Exception implements ArchiveInfoI {
	private static final long serialVersionUID = 1L;
	private int rfcCode;
	private ArchiveConfig config;
	
	public ArchiveException(Throwable cause, int rc, ArchiveConfig cfg) {
		super(cause);
		
		rfcCode = rc;
		config = cfg;
	}

	@Override
	public int getRfcCode() {
		return rfcCode;
	}

	@Override
	public ArchiveConfig getConfig() {
		return config;
	}
	
}