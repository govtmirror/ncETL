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

	public enum TestCase {
		VARIED_INCOMPLETE,
		SINGLE_COMPLETE,
		TWO_COMPLETE
	}
	private TestCase testCase = TestCase.VARIED_INCOMPLETE;
	
	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
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
		value.setOutputDir("/tmp/grib");
		
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
		
    	System.out.printf("Got chunk for %s,  size %d\n", filename, input.size());
    	
    	String outputDir = cfg.getOutputDir();
    	assertNotNull("output dir", outputDir);
    	
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
    	
    	switch (testCase) {
    	case VARIED_INCOMPLETE:
    	
	    	value.add(addExpectation("test_1999-12-31.txt","QPE.1999.12.999.nc"));
	    	value.add(addExpectation("test_2012-12-31.txt","QPE.2012.12.999.nc"));
	    	value.add(addExpectation("test_1999-08-31.txt","QPE.1999.08.999.nc"));
	    	value.add(addExpectation("test_1999-08-22.txt","QPE.1999.08.999.nc"));
	    	value.add(addExpectation("test_1999-08-11.txt","QPE.1999.08.999.nc"));
	    	value.add(addExpectation("test_1999-08-01.txt","QPE.1999.08.999.nc"));
	    	value.add(addExpectation("test_2012-12-01.txt","QPE.2012.12.999.nc"));
	    	value.add(addExpectation("test_2012-12-02.txt","QPE.2012.12.999.nc"));
	    	value.add(addExpectation("test_2012-12-03.txt","QPE.2012.12.999.nc"));
	    	value.add(addExpectation("test_2012-12-04.txt","QPE.2012.12.999.nc"));
	    	
	    	break;    	
	    	
    	case SINGLE_COMPLETE:
        	value.add(addExpectation("test_2000-02-29.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-01.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-02.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-03.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-04.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-05.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-06.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-07.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-08.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-09.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-10.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-11.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-12.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-13.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-14.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-15.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-16.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-17.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-18.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-19.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-20.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-21.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-22.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-23.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-24.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-25.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-26.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-27.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-28.txt","QPE.2000.02.999.nc"));
        	
        	break;
        	
    	case TWO_COMPLETE:
        	value.add(addExpectation("test_2000-02-29.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-01.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-02.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-03.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-04.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-05.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-06.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-07.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-08.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-09.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-10.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-11.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-12.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-13.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-14.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-15.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-16.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-17.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-18.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-19.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-20.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-21.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-22.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-23.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-24.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-25.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-26.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-27.txt","QPE.2000.02.999.nc"));
        	value.add(addExpectation("test_2000-02-28.txt","QPE.2000.02.999.nc"));
        	
        	value.add(addExpectation("test_2001-02-01.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-02.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-03.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-04.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-05.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-06.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-07.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-08.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-09.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-10.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-11.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-12.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-13.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-14.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-15.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-16.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-17.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-18.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-19.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-20.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-21.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-22.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-23.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-24.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-25.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-26.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-27.txt","QPE.2001.02.999.nc"));
        	value.add(addExpectation("test_2001-02-28.txt","QPE.2001.02.999.nc"));

        	break;
    	}
    	
    	// FileFetcher does this, too
    	Collections.sort(value);
    	
    	return value;
    }
}
