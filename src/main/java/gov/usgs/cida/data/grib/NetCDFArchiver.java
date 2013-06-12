package gov.usgs.cida.data.grib;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Transformer;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NetCDFArchiver {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Transformer
	public Object processFiles(
			List<File> input,
			@Header(value="outputFile", required=true) String filename,
			@Header(value="config", required=true) ArchiveConfig cfg
			) 
		throws Exception 
	{
		
    	String outputDir = cfg.getOutputDir();
    	File output = new File(outputDir,filename);
    	
    	if (output.exists()) {
    		// NetCDF library is not reliable at overwriting existing files.
    		logger.info("Removing extant output file {}", output);
    		output.delete();
    	}
    	int rfc = cfg.getRfcCode();
    	
    	logger.info("Writing aggregated data for {} to {}", cfg.getName(), output);
    	
    	RollingNetCDFArchive rnca = new RollingNetCDFArchive(output);
    	try { 
	    	rnca.setExcludeList(RollingNetCDFArchive.DIM, cfg.getDim_excludes());
	    	rnca.setExcludeList(RollingNetCDFArchive.VAR, cfg.getVar_excludes());
	    	rnca.setExcludeList(RollingNetCDFArchive.XY, cfg.getXy_excludes());
	
	    	rnca.setUnlimitedDimension(cfg.getUnlimitedDim(), cfg.getUnlimitedUnits());
	    	
	    	rnca.setGridVariables(cfg.getRenames());
	    	rnca.setGridMapping("Latitude_Longitude");
	    	
	    	rnca.define(input.get(0));
	    	
	    	for (File f : input) {
	    		rnca.addFile(f);
	    	}
	    	rnca.flush();
	    	
	    	return output;
    	} catch (Exception x) {
    		
    		logger.warn("Problem in NetCDF", x);
    		
    		return new ArchiveException(x, rfc, cfg);
    		
    	} finally {
    		rnca.close();
    	}
	}
    
}
