package gov.usgs.cida.data.grib;

import java.io.File;
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

    @ServiceActivator
	public void processFiles(
			List<File> input,
			@Header(value="rfc", required=true) String rfc,
			@Header(value="year", required=true) int year,
			@Header(value="month", required=true) int month
			) {
		
    	File output = findOutputFile(rfc, year, month);
    	RollingNetCDFArchive rnca = new RollingNetCDFArchive(output);
    	
    	
    	rnca.setExcludeMap(excludeMap);
    	rnca.setUnlimitedDimension(unlimited, unlimitedUnits);
    	rnca.setGridVariables(gridVariables);
    	rnca.setGridMapping(gridMapping);
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
