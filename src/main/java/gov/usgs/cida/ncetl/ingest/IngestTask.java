package gov.usgs.cida.ncetl.ingest;

import java.util.TimerTask;

/**
 *
 * @author jwalker
 */
public abstract class IngestTask extends TimerTask {

    public static final long DEFAULT_RESCAN_PERIOD = 1000 * 60 * 60;
    public static final boolean DEFAULT_ACTIVE = false;
    public static final String EVERYTHING_REGEX = ".*";

    public abstract long getRescanEvery();
    
    public abstract boolean isActive();
    
    public abstract String getName();

    public abstract String toJSONString();

    public abstract String toXMLString();
    
}
