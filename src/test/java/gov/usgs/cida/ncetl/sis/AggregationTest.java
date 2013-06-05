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
	
	@Before
	public void init() {
		context = new ClassPathXmlApplicationContext("aggregation-test-context.xml");
	}
	
	@Test
	public void testAggregation() throws Exception {
		try {
			System.out.printf("loaded\n");

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

}
