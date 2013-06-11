package gov.usgs.cida.data.grib;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

import java.io.File;
import java.util.List;

import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Transformer;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NetCDFArchiver {
	
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
    	int rfc = cfg.getRfcCode();
    	
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
    		
    		return new ArchiveException(x, rfc, cfg);
    		
    	} finally {
    		rnca.close();
    	}
	}
    
}
