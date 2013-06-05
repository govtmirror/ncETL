package gov.usgs.cida.ncetl.sis;

import static org.junit.Assert.*;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;
import gov.usgs.cida.ncetl.jpa.EtlHistory;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Transformer;

public class AggregationChecker {
	
	private ArchiveConfig cfg;
	
	public AggregationChecker() {
		this.cfg = makeConfig("/tmp", null, "test_(\\d{4})-(\\d{2})-(\\d{2}).txt");
	}

	public ArchiveConfig getConfig() {
		return cfg;
	}

	private ArchiveConfig makeConfig(String dir, Date since, String fileRegex) {
		ArchiveConfig value = new ArchiveConfig();
		
		value.setInputDir(dir);
		
		// should have three groups: year, month, day
		value.setFileRegex(fileRegex);
		
		List<EtlHistory> etlHistories;
		if (since == null) {
			etlHistories = Collections.emptyList();
		} else {
			EtlHistory h = new EtlHistory();
			h.setTs(new Timestamp(since.getTime()));
			etlHistories = Collections.singletonList(h);
		}
		value.setEtlHistories(etlHistories);
		
		value.setRfcCode(999);
		
		return value;
	}

	private static class Expect {
		private String ofn;
		
		public Expect(String output) {
			ofn = output;
		}
		
		public void expectOfn(String v) {
			assertEquals("output file name", ofn, v);
		}
	}
	
	private Map<String,Expect> expected = new HashMap<String, AggregationChecker.Expect>();
	
    @Transformer
	public String processFiles(
			List<File> input,
			@Header(value="outputFile", required=true) String filename,
			@Header(value="config", required=true) ArchiveConfig config
			) 
		throws Exception 
	{
		
    	String outputDir = cfg.getOutputDir();

    	assertNotNull("input file list", input);
    	assertEquals("config", this.cfg, config);
    	
    	// each set of input files should have the same rfc, year, month, and output file name
    	for (File f : input) {
    		checkExpections(f, filename);
    	}
    	
    	return "ok";
    	
	}
    
    private File addExpectation(String fname, String output) {
    	File v = new File("/tmp",fname);
    	expected.put(fname, new Expect(output));
    	return v;
    }
    
    private void checkExpections(File f, String ofn) {
    	Expect e = expected.get(f.getName());
    	e.expectOfn(ofn);
    }
    
    public List<File> generate() {
    	List<File> value = new ArrayList<File>();
    	
    	value.add(addExpectation("test_1999-12-31.txt","CPE.1999.12.999.nc"));
    	
    	value.add(new File("/tmp","test_2012-12-31.txt"));
    	value.add(new File("/tmp","test_1999-08-31.txt"));
    	value.add(new File("/tmp","test_1999-08-22.txt"));
    	value.add(new File("/tmp","test_1999-08-11.txt"));
    	value.add(new File("/tmp","test_1999-08-01.txt"));
    	value.add(new File("/tmp","test_2012-12-01.txt"));
    	value.add(new File("/tmp","test_2012-12-02.txt"));
    	value.add(new File("/tmp","test_2012-12-03.txt"));
    	value.add(new File("/tmp","test_2012-12-04.txt"));
    	
    	return value;
    }
}
