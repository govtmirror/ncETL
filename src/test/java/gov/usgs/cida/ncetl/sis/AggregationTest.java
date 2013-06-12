package gov.usgs.cida.ncetl.sis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.channel.QueueChannel;

public class AggregationTest {

	private ConfigurableApplicationContext context;
	private AggregationChecker aggregationChecker;
	
	@Before
	public void init() {
		context = new ClassPathXmlApplicationContext("aggregation-test-context.xml");
		aggregationChecker = context.getBean("aggregationChecker", AggregationChecker.class);
	}
	
	private void testOneCase(AggregationChecker.TestCase tc) throws Exception {
		try {
			System.out.printf("loaded\n");

			aggregationChecker.setTestCase(tc);
			context.start();

			Thread.sleep(8*1000);

			assertTrue("survived", true);

			context.stop();

			QueueChannel errorChannel = context.getBean("errorChannel", QueueChannel.class);
			Message<?> msg = errorChannel.receive(1);

			assertNull("error message", msg);

		} finally {
			context.close();
		}
	}
	
	@Test
	public void testVariedIncomplete() throws Exception {
		testOneCase(AggregationChecker.TestCase.VARIED_INCOMPLETE);
	}
	
	@Test
	public void testSingleComplete() throws Exception {
		testOneCase(AggregationChecker.TestCase.SINGLE_COMPLETE);
	}

	@Test
	public void testTwoComplete() throws Exception {
		testOneCase(AggregationChecker.TestCase.TWO_COMPLETE);
	}
}
