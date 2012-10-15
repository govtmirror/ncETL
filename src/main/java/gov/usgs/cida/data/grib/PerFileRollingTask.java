package gov.usgs.cida.data.grib;

import com.google.common.io.Closeables;
import gov.usgs.cida.ncetl.task.NcetlTask;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class PerFileRollingTask extends NcetlTask {
    
    private Map<String, String> parameters = null;

    
    public static final String INPUT_DIR = "inputDir";
    public static final String OUTPUT_DIR = "outputDir";
    public static final String COMPLETE_DIR = "completeDir";
    public static final String RFC_LIST = "rfcList";
    
    public PerFileRollingTask() {
        
    }

    @Override
    public void setRunParams(Map<String, String> hopefullyAStringMapWillDo) {
        parameters = hopefullyAStringMapWillDo;
    }

    @Override
    public void run() {
        if (parameters != null) {
            stuff();
        }
        else {
            // big error, must set minimum parameter set
        }
    }
    
    private void stuff() {
        String inputDir = parameters.get(INPUT_DIR);
        String outputDir = parameters.get(OUTPUT_DIR);
        String completeDir = parameters.get(COMPLETE_DIR);
        if (inputDir == null || outputDir == null) {
            throw new RuntimeException("Must specify input and output directories");
        }
        
        File inputDirFile = new File(inputDir);
        File outputDirFile = new File(outputDir);
        if (!inputDirFile.exists() || !inputDirFile.isDirectory() ||
            !outputDirFile.exists() || !outputDirFile.isDirectory()) {
            throw new RuntimeException("Input and output directories must exist");
        }
        String commaSeparatedList = parameters.get(RFC_LIST);
        String[] rfcList = commaSeparatedList.split(",");
        for (String rfcCode : rfcList) {
            Pattern rfcPattern = Pattern.compile("QPE\\.(\\d{4})(\\d{2})\\d{2}\\.009\\." + rfcCode + "$");
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
//                        if (!month.equals(currentMonth)) {
//                            currentMonth = month;
//                            if (rollingNetCDF != null) {
//                                Flushables.flushQuietly(rollingNetCDF);
//                                //rollingNetCDF.finish();
//                                Closeables.closeQuietly(rollingNetCDF);
//                            }
//                            rollingNetCDF = setupNetCDF(outputDirFile, year, month, rfcCode);
//                            try {
//                                rollingNetCDF.define(file);
//                            }
//                            catch (FactoryException ex) {
//                                throw new RuntimeException("Error getting CRS from prototype grib\n" + ex.getMessage());
//                            }
//                            catch (TransformException ex) {
//                                throw new RuntimeException("Error transforming to lat lon\n" + ex.getMessage());
//                            }
//                            catch (InvalidRangeException ex) {
//                                throw new RuntimeException("Invalid range when defining netcdf\n" + ex.getMessage());
//                            }
//                        }
                        try {
                            // need to open existing and make sure it is good to go
                            rollingNetCDF.addFile(file);
                        }
                        catch (Exception ex) {
                            throw new RuntimeException("Exception occured adding new file, " + file + ex.getMessage());
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
    }
}
