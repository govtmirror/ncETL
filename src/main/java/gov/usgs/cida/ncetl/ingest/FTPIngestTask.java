package gov.usgs.cida.ncetl.ingest;

import gov.usgs.cida.ncetl.spec.IngestControlSpec;
import gov.usgs.cida.ncetl.utils.DatabaseUtil;
import gov.usgs.cida.ncetl.utils.FileHelper;
import gov.usgs.cida.ncetl.utils.ModifiedSinceFilter;
import gov.usgs.webservices.jdbc.spec.Spec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jwalker
 */
public class FTPIngestTask extends IngestTask {
    
    private static final Logger LOG = LoggerFactory.getLogger(FTPIngestTask.class.getName());
    public static final String DEFAULT_PASSWORD = "anonymous";
    public static final String DEFAULT_USER = "anonymous";

    private FTPIngestTask() {
        client = new FTPClient();
        lastSuccessfulRun = new DateTime(0L);
    }

    @Override
    public long getRescanEvery() {
        return rescanEvery;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public String getName() {
        return ingestName;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("ftpLocation", ftpLocation);
        json.put("rescanEvery", rescanEvery);
        json.put("filePattern", fileRegex.pattern());
        json.put("lastSuccess", lastSuccessfulRun);
        json.put("username", username);
        json.put("password", password);
        return json.toJSONString();
    }

    @Override
    public String toXMLString() {
        StringBuilder str = new StringBuilder();
        str.append("<?xml");
        return str.toString();
    }

    @Override
    public void run() {
            if (!active) {
            return;
        }
        boolean everythingIsGood = false;
        try {
            int port = (ftpLocation.getPort() != -1) ? ftpLocation.getPort() : ftpLocation.getDefaultPort();
            FTPClientConfig config = new FTPClientConfig(
                    FTPClientConfig.SYST_UNIX);
            client.configure(config);
            client.connect(ftpLocation.getHost(), port);
            client.login(username, password);
            String status = client.getStatus();
            int reply = client.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                LOG.info("FTP server refused connection.");
            }
            LOG.info(status);
            client.changeWorkingDirectory(ftpLocation.getPath());
            everythingIsGood = ingestDirectory(".");
        }
        catch (SocketException ex) {
            LOG.error(ex.getMessage());
        }
        catch (IOException ex) {
            LOG.error(ex.getMessage());
        }

        if (everythingIsGood) {
            lastSuccessfulRun = new DateTime();
            java.sql.Date newSuccessDate = new java.sql.Date(new java.util.Date().getTime());
            java.sql.Time newSuccessTime = new java.sql.Time(new java.util.Date().getTime());
            LOG.debug("SUCCESSFUL INGEST");
            try {
                IngestControlSpec spec = new IngestControlSpec();
                Map<String, String[]> params = new HashMap<String, String[]>(1);
                params.put("s_" + IngestControlSpec.NAME, new String[] {ingestName});
                params.put(IngestControlSpec.SUCCESS_DATE, new String[] {newSuccessDate.toString()});
                params.put(IngestControlSpec.SUCCESS_TIME, new String[] {newSuccessTime.toString()});
                Spec.loadParameters(spec, params);
                Connection con = DatabaseUtil.getConnection();
                Spec.updateRow(spec, con);
            } catch (Exception ex) { //throws SQLException, NamingException, ClassNotFoundException 
                LOG.error(ex.getMessage());
            }
        }
    }

    private boolean ingestDirectory(String dir) throws IOException {
        boolean completedSuccessfully = true;
        FTPFile[] files = client.listFiles(dir, new ModifiedSinceFilter(
                lastSuccessfulRun));
        for (FTPFile file : files) {
            LOG.debug("ingesting file named " + file.getName());
            if (file.isDirectory()) {
                //because datasetName is actually catalog that folder already exists in /datasets
                if (!ingestDirectory(dir + File.separator  + datasetName + File.separator + file.getName())) {
                    completedSuccessfully = false;
                }
            } else {
                Matcher matcher = fileRegex.matcher(file.getName());
                if (matcher.matches() && !client.retrieveFile(file.getName(),
                        new FileOutputStream(
                        FileHelper.getDatasetsDirectory() + File.separator + datasetName + File.separator + file.getName()))) {
                    // TODO keep a list of files that failed to try to correct next time
                    completedSuccessfully = false;
                }
            }
        }
        return completedSuccessfully;
    }

    public static FTPIngestTask fromRequest(HttpServletRequest request) throws
            MalformedURLException {
        String name = request.getParameter("name");
        String ftpLoc = request.getParameter("ftpLocation");
        long rescan = Long.parseLong(request.getParameter("rescanEvery"));
        String regex = request.getParameter("fileRegex");
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        boolean active = ("true".equalsIgnoreCase(request.getParameter("active"))) ? true : false;
        FTPIngestTask ftpObj = (new Builder(name, ftpLoc)).rescanEvery(rescan).fileRegex(
                regex).username(user).password(pass).active(active).build();
        return ftpObj;
    }
    
    private String ingestName;
    private String datasetName; //actually catalog name
    private URL ftpLocation;
    private long rescanEvery;
    private FTPClient client;
    private Pattern fileRegex;
    private DateTime lastSuccessfulRun;
    private String username;
    private String password;
    private boolean active;

    public static class Builder {

        public Builder fileRegex(String fileRegex) {
            this.fileRegex = Pattern.compile(fileRegex);
            return this;
        }
        
        public Builder name(String name) {
            this.ingestName = name;
            return this;
        }
        
        public Builder datasetName(String name) {
            this.datasetName = name;
            return this;
        }

        public Builder location(String ftpLocation) throws MalformedURLException {
            this.ftpLocation = new URL(ftpLocation);
            return this;
        }

        public Builder rescanEvery(long rescanEvery) {
            this.rescanEvery = rescanEvery;
            return this;
        }

        public Builder startDate(DateTime start) {
            this.startDate = start;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }
        
        private String ingestName;
        private String datasetName; //actually catalog name
        private URL ftpLocation;
        private long rescanEvery;
        private FTPClient client;
        private Pattern fileRegex;
        private DateTime startDate;
        private String username;
        private String password;
        private boolean active;

        public Builder(String name, String url) throws MalformedURLException {
            ingestName = name;
            location(url);
            rescanEvery = DEFAULT_RESCAN_PERIOD;
            fileRegex = Pattern.compile(EVERYTHING_REGEX);
            // start at Jan 1, 1970 0Z
            startDate = new DateTime(0L);
            username = DEFAULT_USER;
            password = DEFAULT_PASSWORD;
            active = DEFAULT_ACTIVE;
        }

        public FTPIngestTask build() {
            FTPIngestTask ingest = new FTPIngestTask();
            ingest.ingestName = ingestName;
            ingest.datasetName = datasetName;
            ingest.ftpLocation = ftpLocation;
            ingest.rescanEvery = rescanEvery;
            ingest.fileRegex = fileRegex;
            ingest.lastSuccessfulRun = startDate;
            ingest.username = username;
            ingest.password = password;
            ingest.active = active;
            return ingest;
        }
    }
}
