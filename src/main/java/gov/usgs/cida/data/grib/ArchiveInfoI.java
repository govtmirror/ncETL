package gov.usgs.cida.data.grib;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

public interface ArchiveInfoI {

	public abstract int getRfcCode();

	public abstract ArchiveConfig getConfig();

}