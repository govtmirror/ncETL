package gov.usgs.cida.ncetl.sis;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;
import gov.usgs.cida.ncetl.jpa.EtlHistory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;

public class FileFetcherTest {

	private FileFetcher victim;
	private String fileRegex = "^sample_(\\d{4})-(\\d{2})-(\\d{2})\\.txt$";

	@Before
	public void setUp() throws Exception {
		victim = new FileFetcher();
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
	
	@Test
	public void testListInputFiles_noFiles() {
		
		ArchiveConfig cfg = makeConfig("src/test/resources/NOT", null, fileRegex);
		
		Message<List<File>> result = victim.listInputFiles(cfg);
		
		assertNotNull("result", result);
		assertEquals("file list size", 0, result.getPayload().size());
	}

	@Test
	public void testListInputFiles_noHistory() {
		
		ArchiveConfig cfg = makeConfig("src/test/resources/input", null, fileRegex);
		
		Message<List<File>> result = victim.listInputFiles(cfg);
		
		assertNotNull("result", result);
		assertEquals("file list size", 2, result.getPayload().size());
	}

	@Test(expected=RuntimeException.class)
	public void testListInputFiles_regexValidation() {
		
		ArchiveConfig cfg = makeConfig("src/test/resources/input", null, "^sample_(\\d{4})-\\d{2}-(\\d{2})\\.txt$");
		
		Message<List<File>> result = victim.listInputFiles(cfg);
		
		assertNotNull("result", result);
		assertEquals("file list size", 1, result.getPayload().size());
	}

	@Test
	public void testListInputFiles_withHistory() {
		
		DateTime cutoff = new DateTime(2000,1,1,0,0);
		ArchiveConfig cfg = makeConfig("src/test/resources/input", cutoff.toDate(), fileRegex);
		
		Message<List<File>> result = victim.listInputFiles(cfg);
		
		assertNotNull("result", result);
		assertEquals("file list size", 1, result.getPayload().size());
	}

	@Test
	public void testSplit() {
		
		ArchiveConfig cfg = makeConfig("src/test/resources/input", null, "^test_(\\d{4})-(\\d{2})-(\\d{2})\\.grib$");

		List<File> payload = new ArrayList<File>();
		payload.add(new File("/tmp","test_2001-03-04.grib"));
		payload.add(new File("/tmp","test_2001-03-05.grib"));
		payload.add(new File("/tmp","test_2001-03-12.grib"));
		payload.add(new File("/tmp","test_2001-09-04.grib"));
		payload.add(new File("/tmp","test_2000-03-05.grib"));

		List<Message<File>> result = victim.split(payload, cfg);
		
		assertNotNull("result", result);
		for (Message<File> msg : result) {
			assertEquals("payload dir", "/tmp", msg.getPayload().getParent());
			MessageHeaders headers = msg.getHeaders();
			System.out.printf("headers for %s are %s\n", msg.getPayload(), headers);
			assertNotNull("corr id", headers.getCorrelationId());
			assertNotNull("seq num", headers.getSequenceNumber());
			assertNotNull("seq size", headers.getSequenceSize());
			
			assertTrue("rfc code in file name", headers.get("outputFile", String.class).contains("999"));
			assertEquals("rfc code in headers", 999, headers.get("rfc", Integer.class).intValue());
		}
	}

}
