package gov.usgs.cida.ncetl.sis;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class FileFetcherUnitTest extends FileFetcher {
	
	private static final long HOUR = 1000*60*60;
	private static final long DAY = 24 * HOUR;
	
	@Test
	public void testMakeOutputFileName_1() {
		String fn = makeOutputFileName(1999, 11, 42);
		System.out.printf("out file name %s\n",  fn);
		assertTrue("has year", fn.contains("1999"));
		assertTrue("has month", fn.contains("11"));
	}

	@Test
	public void testMakeOutputFileName_2() {
		String fn = makeOutputFileName(2012, 3, 409);
		System.out.printf("out file name %s\n",  fn);
		assertTrue("has year", fn.contains("2012"));
		assertTrue("has month", fn.contains("03"));
		assertTrue("has rfc", fn.contains("409"));
	}

	@Test
	public void testOneMonthAgo() {
		Date oma = oneMonthAgo();
		
		System.out.printf("one month ago %s\n", oma);
		
		long diff = System.currentTimeMillis() - oma.getTime();
		assertTrue("long enuf", diff > 20 * DAY);
		assertTrue("small enuf", diff < 40 * DAY);
	}

	@Test
	public void testDaysInMonth() {
		assertEquals(28, daysInMonth(1999, 2));
		assertEquals(29, daysInMonth(2004, 2));
		assertEquals(31, daysInMonth(2012, 1));
		assertEquals(31, daysInMonth(1957, 8));
	}

}
