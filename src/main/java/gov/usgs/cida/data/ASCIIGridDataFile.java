/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.data;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import static org.joda.time.DateTimeFieldType.*;

/**
 *
 * @author jwalker
 */
public class ASCIIGridDataFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(ASCIIGridDataFile.class);
    
    private File underlyingFile;
    private Instant startDate = null;
    private int timesteps = -1;
    private String varName = null;
    
    public ASCIIGridDataFile(File infile) {
        this.underlyingFile = infile;
        
        // ASSUMING VAR.TIMESTEP.grid
        String filename = infile.getName();
        this.varName = filename.split("\\.")[0].toLowerCase();
    }
    
    /*
     * Sets startDate, timesteps based on assumption that they are in first two lines
     * firstline = "\ttimesteps"
     * secondline = "firstTime\tvalueGRIDid1\t...\tvalueGRIDidn"
     */
    public void inspectFile() throws FileNotFoundException, IOException {
        BufferedReader buf = new BufferedReader(new FileReader(underlyingFile));
        String line = null;
        try {
            if ((line = buf.readLine()) != null) {
                timesteps = Integer.parseInt(line.trim());
            }
            if ((line = buf.readLine()) != null) {
                String yyyymmdd = line.substring(0, 7);
                startDate = Instant.parse(yyyymmdd, DateTimeFormat.forPattern("yyyyMMdd"));
            }
            
            if (timesteps == -1 || startDate == null) {
                LOG.error("File doesn't look like it should, unable to pull date out of file");
                throw ASCIIGrid2NetCDFConverter.rtex;
            }
        }
        finally {
            IOUtils.closeQuietly(buf);
        }
    }
    
    /**
     * Kind of convoluted way of converting to instant and back to date, but whatever
     * Just want to create something netcdf can do something with
     * @return formatted units for netcdf
     */
    public String getTimeUnits() {
        return "days since " + startDate.get(year()) + "-" +
                startDate.get(monthOfYear()) + "-" + startDate.get(dayOfMonth());
    }
    
    public int getTimestepCount() {
        return timesteps;
    }
    
    public String getVariableName() {
        return varName;
    }
    
    private BufferedReader buffer = null;
    private String[] currentLine = null;
    private int marker = 1;
    private int strideLength = -1;
    
    public void openForReading(int strideLength) throws FileNotFoundException, IOException {
        buffer = new BufferedReader(new FileReader(underlyingFile));
        // First line has number with how many lines follow it
        buffer.readLine();
        this.strideLength = strideLength;
    }

    public boolean hasMoreStrides() {
        return (marker + strideLength < currentLine.length);
    }
    
    public boolean readNextLine() throws IOException {
        String line = null;
        if ((line = buffer.readLine()) != null) {
            currentLine = line.split("\\s+");
            return true;
        }
        return false;
    }

    /**
     * Please do not try to run this in any concurrent fashion
     * it will break badly.
     * @return 
     */
    public float[] readTimestepByStride() {
        float[] strideVals = new float[strideLength];
        for (int i=0; i<strideLength; i++) {
            strideVals[i] = Float.parseFloat(currentLine[marker]);
            marker++;
        }
        return strideVals;
    }
    
    public void closeForReading() {
        IOUtils.closeQuietly(buffer);
    }
}
