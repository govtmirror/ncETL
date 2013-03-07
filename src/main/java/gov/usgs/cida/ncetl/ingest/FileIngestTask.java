package gov.usgs.cida.ncetl.ingest;

import java.net.MalformedURLException;
import java.util.regex.Pattern;
import org.joda.time.DateTime;

/**
 * This is meant to move/copy files from one place on the filesystem to another.
 * It may also rename the files and organize them into a hierarchy
 * 
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class FileIngestTask extends IngestTask {

    @Override
    public long getRescanEvery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isActive() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toJSONString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toXMLString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String ingestName;
    private String datasetName; //actually catalog name
    private long rescanEvery;
    private Pattern fileRegex;
    private DateTime lastSuccessfulRun;
    private boolean active;

    public static class Builder {
        
        private String ingestName;
        private String datasetName; //actually catalog name
        private long rescanEvery;
        private Pattern fileRegex;
        private DateTime startDate;
        private boolean active;
        
        
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

        public Builder rescanEvery(long rescanEvery) {
            this.rescanEvery = rescanEvery;
            return this;
        }

        public Builder startDate(DateTime start) {
            this.startDate = start;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }
        
        public Builder(String name, String url) throws MalformedURLException {
            ingestName = name;
            rescanEvery = DEFAULT_RESCAN_PERIOD;
            fileRegex = Pattern.compile(EVERYTHING_REGEX);
            // start at Jan 1, 1970 0Z
            startDate = new DateTime(0L);
            active = DEFAULT_ACTIVE;
        }
        
        public FileIngestTask build() {
            FileIngestTask ingest = new FileIngestTask();
            ingest.ingestName = ingestName;
            ingest.datasetName = datasetName;
            ingest.rescanEvery = rescanEvery;
            ingest.fileRegex = fileRegex;
            ingest.lastSuccessfulRun = startDate;
            ingest.active = active;
            return ingest;
        }
    }
    
}
