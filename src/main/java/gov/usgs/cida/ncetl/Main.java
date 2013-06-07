package gov.usgs.cida.ncetl;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.core.PollableChannel;

public class Main {

	public static final int SECOND = 1000;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("jpa-integration-context.xml");
		 
		 context.start();
		 
		 PollableChannel queue = context.getBean("finis", PollableChannel.class);

		 // Timeout is a safety measure; we expect to be notified by the queue
		 long timeout = 1 * HOUR;
		 
		 Message<?> msg = queue.receive(timeout);

		 context.stop();
	}

}
