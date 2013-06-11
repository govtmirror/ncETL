package gov.usgs.cida.ncetl.sis;

import gov.usgs.cida.data.grib.ArchiveInfoI;
import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

import java.io.FileNotFoundException;

public class InputNotFoundException 
	extends FileNotFoundException 
	implements ArchiveInfoI
{
	private static final long serialVersionUID = 1L;
	
	private int rfcCode;
	private ArchiveConfig config;

	public InputNotFoundException(String fn, int rc, ArchiveConfig cfg) {
		super(fn);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InputNotFoundException [");
		builder.append(getMessage());
		builder.append(", rfcCode=").append(rfcCode);
		builder.append(", config=").append(config);
		builder.append("]");
		return builder.toString();
	}

	
}
