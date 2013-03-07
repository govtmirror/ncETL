package gov.usgs.cida.data.grib;

import com.google.common.io.Closeables;
import com.google.common.io.Flushables;
import static gov.usgs.cida.ncetl.spec.task.ArchiveSpec.*;
import gov.usgs.cida.ncetl.task.NcetlTask;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.InvalidRangeException;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class GribFileTask extends NcetlTask {
    
    private static Logger log = LoggerFactory.getLogger(GribFileTask.class);
    
    public static final String RFC_CODE_REPLACE = "{rfc_code}";
    
    private Map<String, Object> parameters = null;
    
    @Override
    public void run() {
        if (parameters != null) {
            processFile();
        }
        else {
            log.error("Must set minimum parameter set");
        }
    }
    
    private void processFile() {
        String inputDir = (String)parameters.get(INPUT_DIR);
        String outputDir = (String)parameters.get(OUTPUT_DIR);
        String completeDir = (String)parameters.get(COMPLETE_DIR);
        String fileRegex = (String)parameters.get(FILE_REGEX);
        String rfcCode = String.valueOf((Integer)parameters.get(RFC_CODE));
        
        if (inputDir == null || outputDir == null) {
            throw new RuntimeException("Must specify input and output directories");
        }
        
        File inputDirFile = new File(inputDir);
        File outputDirFile = new File(outputDir);
        if (!inputDirFile.exists() || !inputDirFile.isDirectory() ||
            !outputDirFile.exists() || !outputDirFile.isDirectory()) {
            throw new RuntimeException("Input and output directories must exist");
        }
        String rfcRegex = fileRegex.replace(RFC_CODE_REPLACE, rfcCode);
        Pattern rfcPattern = Pattern.compile(rfcRegex);
        FileFilter filter = new RegexFileFilter(rfcPattern);
        File[] listFiles = inputDirFile.listFiles(filter);
        RollingNetCDFArchive rollingNetCDF = null;
        try {
            String currentMonth = null;
            Arrays.sort(listFiles);
            for (File file : listFiles) {
                Matcher rfcMatcher = rfcPattern.matcher(file.getName());
                if (rfcMatcher.matches()) {
                    String year = rfcMatcher.group(1);
                    String month = rfcMatcher.group(2);
                    // this part takes place monthly
                    if (!month.equals(currentMonth)) {
                        currentMonth = month;
                        if (rollingNetCDF != null) {
                            Flushables.flushQuietly(rollingNetCDF);
                            //rollingNetCDF.finish();
                            Closeables.closeQuietly(rollingNetCDF);
                        }
                        try {
                            rollingNetCDF = setupNetCDF(outputDirFile, year, month, rfcCode);
                            rollingNetCDF.define(file);
                        }
                        catch (FactoryException ex) {
                            throw new RuntimeException("Error getting CRS from prototype grib\n" + ex.getMessage());
                        }
                        catch (TransformException ex) {
                            throw new RuntimeException("Error transforming to lat lon\n" + ex.getMessage());
                        }
                        catch (InvalidRangeException ex) {
                            throw new RuntimeException("Invalid range when defining netcdf\n" + ex.getMessage());
                        }
                        catch (IOException ex) {
                            throw new RuntimeException("General IOException caught\n" + ex.getMessage());
                        }
                    }
                    try {
                        // need to open existing and make sure it is good to go
                        rollingNetCDF.addFile(file);
                    }
                    catch (Exception ex) {
                        throw new RuntimeException("Exception occured adding new file, " + file + " " + ex.getMessage());
                    }
                }
                if (completeDir != null) {
                    file.renameTo(new File(completeDir + File.separator + file.getName()));
                }
            }
        }
        finally {
            Closeables.closeQuietly(rollingNetCDF);
        }
    }
    
    private RollingNetCDFArchive setupNetCDF(File outputDir, String year, String month, String rfcCode) throws IOException {
        
        List<String> dimExcludes = (List<String>)parameters.get(DIM_EXCLUDES);
        List<String> varExcludes = (List<String>)parameters.get(VAR_EXCLUDES);
        List<String> xyExcludes = (List<String>)parameters.get(XY_EXCLUDES);
        
        String unlimitedDim = (String)parameters.get(UNLIM_DIM);
        String unlimitedUnits = (String)parameters.get(UNLIM_UNITS);
        
        Map<String, String> renames = (Map<String, String>)parameters.get(RENAMES);
        
        String ncFilename = outputDir.getCanonicalPath() + File.separatorChar + "QPE." + year + "." + month + "." + rfcCode + ".nc";
        RollingNetCDFArchive roll = new RollingNetCDFArchive(new File(ncFilename));
        
        roll.setExcludeList(RollingNetCDFArchive.DIM, dimExcludes);
        roll.setExcludeList(RollingNetCDFArchive.VAR, varExcludes);
        roll.setExcludeList(RollingNetCDFArchive.XY, xyExcludes);
        
        roll.setGridMapping("Latitude_Longitude");
        roll.setUnlimitedDimension(unlimitedDim, unlimitedUnits);
        roll.setGridVariables(renames);
        return roll;
    }   

    @Override
    public void setRunParams(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}