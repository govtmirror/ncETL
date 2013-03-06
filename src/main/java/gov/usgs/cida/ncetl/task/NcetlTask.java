package gov.usgs.cida.ncetl.task;

import java.util.Map;
import java.util.TimerTask;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public abstract class NcetlTask extends TimerTask {

    public abstract void setRunParams(Map<String, Object> parameters);
    
    @Override
    public abstract void run();
    
}
