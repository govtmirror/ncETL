package gov.usgs.cida.ncetl.task;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public abstract class PerFileRollingTask extends NcetlTask {
    
    private static Logger log = LoggerFactory.getLogger(PerFileRollingTask.class);

    // In theory a database should be able to populate this Map
    protected Map<String, String> parameters = null;
    
    public PerFileRollingTask() {
        
    }

    @Override
    public void setRunParams(Map<String, String> hopefullyAStringMapWillDo) {
        parameters = hopefullyAStringMapWillDo;
    }

}
