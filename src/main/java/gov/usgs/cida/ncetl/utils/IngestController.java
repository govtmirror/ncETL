package gov.usgs.cida.ncetl.utils;

import com.google.common.collect.Maps;
import gov.usgs.cida.ncetl.spec.IngestControlSpec;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jwalker
 */
public class IngestController {
    
    private static final Logger LOG = LoggerFactory.getLogger(
            IngestController.class);
    private static final String TIMER_NAME = "ncETL Ingestor";
    private static Timer timer = new Timer(TIMER_NAME, true); //daemon
    private static Map<String, TimerTask> runningTasks = Maps.newTreeMap();
    private static final long serialVersionUID = 1L;
    
    public static void setupIngestors() throws SQLException, NamingException,
                                               ClassNotFoundException,
                                               MalformedURLException {
        
        Connection con = DatabaseUtil.getConnection();
        try {
            List<FTPIngestTask> tasks = IngestControlSpec.unmarshalAllIngestors(
                    con);
            for (FTPIngestTask task : tasks) {
                startIngestTimer(task);
            }
        }
        finally {
            DatabaseUtil.closeConnection(con);
        }
    }
    
    protected static void startIngestTimer(FTPIngestTask task) {
        String name = task.getName();
        /* Was previously clearing out the timer and creating a new one
         * But cancelling a timertask means it can't be scheduled later
        if (task != null) { //cancel the task
            task.cancel();
        }
        timer.purge(); //remove the cancelled task from timer
        runningTasks.remove(name); //remove the task from runningTasks map
        */
        /*So instead use the runningTasks map to prevent duplicates*/
        if(!runningTasks.containsKey(name)) {
            try{// if task was already scheduled or cancelled, timer was cancelled, or timer thread terminated.
                timer.scheduleAtFixedRate(task, 0L, task.getRescanEvery()); 
                LOG.debug("timerTask started for ingest: " + name);
                runningTasks.put(name, task);
            } catch (IllegalStateException ex) {
                LOG.debug("Error scheduling timerTask "+name+": IllegalStateException");
            }
        }
    }
    
    /*
     * no usages now, but startIngestTimer may not work if the task is already in runningTasks
     */
    public static void restartIngestTimer(String taskName) {
        Connection con = null;
        try {
            con = DatabaseUtil.getConnection();
            FTPIngestTask task = IngestControlSpec.unmarshalSpecificIngestor(taskName, con);
            startIngestTimer(task);
        }
        catch (SQLException ex) {
            LOG.debug("problem with sql when restarting ingestor", ex);
        }
        catch (NamingException ex) {
            LOG.debug("naming problem in ingestor restart", ex);
        }
        catch (ClassNotFoundException ex) {
            LOG.debug("class not found when restarting ingest task", ex);
        }
        catch (MalformedURLException ex) {
            LOG.debug("ingest url is malformed", ex);
        }
        finally {
            DatabaseUtil.closeConnection(con);
        }
    }
    
    public static void shutdownTimer() {
        timer.cancel();
        LOG.debug("static timer terminated");
        timer = null;
    }
}
