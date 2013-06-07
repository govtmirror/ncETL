package gov.usgs.cida.data.grib;

import static org.junit.Assert.*;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;
import gov.usgs.cida.ncetl.jpa.RenameMapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NetCDFArchiverTest {

	private NetCDFArchiver victim;

	private static String testGribFile = RollingNetCDFArchiveTest.class.getClassLoader().getResource(
			"gov/usgs/cida/data/grib/QPE.20100319.009.160").getFile();
	private static File tmpNc = null;

	@BeforeClass
	public static void setUpClass() throws IOException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		tmpNc = new File(tmpdir,"" + Long.toString(System.nanoTime()) + ".nc");
	}

	@After
	public void tearDown() throws IOException {
		if (tmpNc.exists()) {
			FileUtils.forceDelete(tmpNc);
		}
	}

	@Before
	public void setUp() throws Exception {
		victim = new NetCDFArchiver();
	}

	private ArchiveConfig mockConfig() {
		// data set up from gov.usgs.cida.data.grib.RollingNetCDFArchiveTest
		
		ArchiveConfig value = new ArchiveConfig();
		
        List<String> dimList = Lists.newLinkedList();
        dimList.add("time1");
        
        List<String> varList = Lists.newLinkedList();
        varList.add("time1");
        varList.add("time_bounds");
        varList.add("time1_bounds");
        
        List<String> xyList = Lists.newLinkedList();
        xyList.add("Total_precipitation_surface_Mixed_intervals_Accumulation");
        xyList.add("PolarStereographic_Projection");
        xyList.add("x");
        xyList.add("y");
        
        value.setXy_excludes(xyList);
        value.setDim_excludes(dimList);
        value.setVar_excludes(varList);
        
        value.setUnlimitedDim("time");
        value.setUnlimitedUnits("hours since 2000-01-01 00:00:00");
        
        Map<String, String> varMap = Maps.newHashMap();
        varMap.put("1-hour_Quantitative_Precip_Estimate_surface_1_Hour_Accumulation","1-hour_Quantitative_Precip_Estimate_surface_1_Hour_Accumulation");
        varMap.put("Total_precipitation_surface_Mixed_intervals_Accumulation", "1-hour_Quantitative_Precip_Estimate_surface_1_Hour_Accumulation");
        value.setRenames(varMap);
        
        value.setOutputDir(System.getProperty("java.io.tmpdir"));
        
		return value;
	}
	
	@Test
	public void testProcessFiles() throws Exception {
		
		ArchiveConfig cfg = mockConfig();
		List<File> input = new ArrayList<File>();
		input.add(new File(testGribFile));
		
		String outputName = tmpNc.getName();
		File output = victim.processFiles(input, outputName, cfg);
		
		assertTrue("output exists", output.exists());
		
		System.out.printf("Created file %s", output.getAbsolutePath());
		
		assertTrue("output has content", 0 < output.length());
	}

}
