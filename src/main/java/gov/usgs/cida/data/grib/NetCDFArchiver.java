package gov.usgs.cida.data.grib;

import gov.usgs.cida.ncetl.spec.task.ArchiveSpec;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.integration.Message;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.ServiceActivator;

public class NetCDFArchiver {
    private Map<String, List<String>> excludeMap;
    private String unlimited;
    private String unlimitedUnits;
    private String gridMapping;
    private Map<String, String> gridVariables;
    private File outputDir;
    private String rfcCode;
    
    @ServiceActivator
	public void processFiles(
			List<File> input,
			@Header(value="rfc", required=true) String rfc,
			@Header(value="year", required=true) int year,
			@Header(value="month", required=true) int month,
			@Header(value="archive-spec", required=true) ArchiveConfig archiveConfig
			) throws IOException {
		
    	File output = findOutputFile(rfc, year, month);
    	RollingNetCDFArchive rnca = new RollingNetCDFArchive(output);
    	
    	this.rfcCode = rfc;
    	
    	rnca.setExcludeList("dim_excludes", archiveConfig.getDim_excludes());
    	rnca.setExcludeList("var_excludes", archiveConfig.getVar_excludes());
    	rnca.setExcludeList("xy_excludes", archiveConfig.getXy_excludes());

    	rnca.setUnlimitedDimension(unlimited, unlimitedUnits);
    	rnca.setGridVariables(gridVariables);
    	rnca.setGridMapping(gridMapping);
	}
    
    private File findOutputFile(String rfc, int year, int month) {
        String ncFilename = "QPE." + year + "." + month + "." + rfcCode + ".nc";
		return new File(outputDir, ncFilename);
	}

	public void setExcludeList(String key, List<String> excludes) {
        this.excludeMap.put(key, excludes);
    }
    
    public void setUnlimitedDimension(String dimName, String units) {
        this.unlimited = dimName;
        this.unlimitedUnits = units;
    }
    
    public void setGridMapping(String gridMappingName) {
        this.gridMapping = gridMappingName;
    }
    
    public void setGridVariables(Map<String, String> variableMapping) {
        gridVariables = variableMapping;
    }

}
