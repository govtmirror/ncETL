package gov.usgs.cida.data.grib;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.common.io.Flushables;
import static gov.usgs.cida.ncetl.spec.ArchiveSpec.*;
import gov.usgs.cida.ncetl.task.PerFileRollingTask;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
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
public class PerGribFileTask extends PerFileRollingTask {
    
    private static Logger log = LoggerFactory.getLogger(PerGribFileTask.class);
    
    public static final String RFC_CODE_REPLACE = "{rfc_code}";
    
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
        String inputDir = parameters.get(INPUT_DIR);
        String outputDir = parameters.get(OUTPUT_DIR);
        String completeDir = parameters.get(COMPLETE_DIR);
        String fileRegex = parameters.get(FILE_REGEX);
        String rfcCode = parameters.get(RFC_CODE);
        
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
        
        String dimExcludeStr = parameters.get(DIM_EXLUDES);
        String[] dimExcludes = dimExcludeStr.split(",");
        String varExcludeStr = parameters.get(VAR_EXLUDES);
        String[] varExcludes = varExcludeStr.split(",");
        String xyExcludeStr = parameters.get(XY_EXLUDES);
        String[] xyExcludes = xyExcludeStr.split(",");
        
        String unlimitedDim = parameters.get(UNLIM_DIM);
        String unlimitedUnits = parameters.get(UNLIM_UNITS);
        
        String fromVarStr = parameters.get(RENAME_FROM_VAR);
        String[] fromVars = fromVarStr.split(","); // not sure if there will ever be multiple
        String toVarStr = parameters.get(RENAME_TO_VAR);
        if (toVarStr.contains(",")) {
            throw new RuntimeException("Can only rename single variable");
        }
        
        String ncFilename = outputDir.getCanonicalPath() + File.separatorChar + "QPE." + year + "." + month + "." + rfcCode + ".nc";
        RollingNetCDFArchive roll = new RollingNetCDFArchive(new File(ncFilename));
        
        roll.setExcludeList(RollingNetCDFArchive.DIM, dimExcludes);
        roll.setExcludeList(RollingNetCDFArchive.VAR, varExcludes);
        roll.setExcludeList(RollingNetCDFArchive.XY, xyExcludes);
        roll.setGridMapping("Latitude_Longitude");
        roll.setUnlimitedDimension(unlimitedDim, unlimitedUnits);
        Map<String, String> varMap = Maps.newHashMap();
        for (String from : fromVars) {
            varMap.put(from, toVarStr);
        }
        roll.setGridVariables(varMap);
        return roll;
    }   
}