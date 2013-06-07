package gov.usgs.cida.data.grib;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;

import gov.usgs.cida.ncetl.sis.LatchTrigger;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.PollableChannel;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.jmx.export.MBeanExporter;

public class OneshotTest implements ApplicationListener<ApplicationEvent>{

	private ConfigurableApplicationContext context;
	private AtomicInteger counter;
	private SourcePollingChannelAdapter source;
	
	@Before
	public void init() {
		context = new ClassPathXmlApplicationContext("oneshot-context.xml");
		context.addApplicationListener(this);
		counter = context.getBean("counter", AtomicInteger.class);
		source = context.getBean("source", SourcePollingChannelAdapter.class);
	}
	
	@Test
	public void testLoad() {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 System.out.printf("config-fetcher %s\n", oneshot);
			 assertNotNull("oneshot", oneshot);
			 
			 assertEquals("no traffic", 0, counter.get());
		 } finally {
			 context.close();
		 }
	}
	
	@Test
	public void testStart() throws Exception {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 context.start();
			 Thread.sleep(1*1000);
			 System.out.println("Stopping");
			 context.stop();
			 
			 assertEquals("traffic", 3, counter.get());
		 } finally {
			 context.close();
		 }
	}

	@Test
	public void testStopByJMX() throws Exception {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 IntegrationMBeanExporter jmx = context.getBean("jmx", IntegrationMBeanExporter.class);
			 
			 PollableChannel queue = context.getBean("queue", PollableChannel.class);
			 
			 System.err.println(queue.getClass().getName());

			 context.start();
			 
			 Thread.sleep(5*1000);
			 
			 // Will the context auto-stop when the poller returns null?
			 
			 /*
			  * Answer: no.
			  * It looks like Spring Integration does not have this capability built-in
			  * (see http://stackoverflow.com/questions/13607283/spring-integration-iterate-through-contents-of-directory-only-once
			  * 
			  * Possible solutions:
			  * 1) Timer (hack hack)
			  * 2) Add a "finis" message after reading all files in the directory, and route that message to the control bus
			  *    to stop the context, at tail of chain.
			  * 3) Use aggregation headers from the JPA query (stacked under the per-output headers) to mark the poller execution, 
			  *    then aggregate at the tail end and route the aggregation to the control bus.
			  * 4) Declare an mbean server and use the JMX operation stopActiveComponents
			  *    see http://static.springsource.org/spring-integration/reference/htmlsingle/#jmx-shutdown
			  *    This does not shut down the context, though it does shut down the source; since it waits,
			  *    it is probably safe to stop the context after the method returns.
			  * 5) Use aggregation as (4), but route to a Queue, and have the main program wait at the queue until the message arrives.
			  */
			 long timeout = 3*1000;
			 jmx.stopActiveComponents(false, timeout);
			 context.stop();
			 
			 assertFalse("Source endpoint running", source.isRunning());
			 
			 assertFalse("Context still running", context.isRunning());
			 
			 assertEquals("traffic", 3, counter.get());
		 } finally {
			 context.close();
		 }
	}
	
	@Test
	public void testStopWithAggregation() throws Exception {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 IntegrationMBeanExporter jmx = context.getBean("jmx", IntegrationMBeanExporter.class);
			 
			 PollableChannel queue = context.getBean("queue", PollableChannel.class);
			 
			 System.err.println(queue.getClass().getName());

			 context.start();
			 
			 Thread.sleep(5*1000);
			 
			 long timeout = 30*1000;
			 
			 Message<?> msg = queue.receive(timeout);
			 System.out.printf("Message %s payload %s\n", msg, msg.getPayload());

			 context.stop();
			 
			 assertFalse("Source endpoint running", source.isRunning());
			 
			 assertFalse("Context still running", context.isRunning());
			 
			 assertEquals("traffic", 3, counter.get());
		 } finally {
			 context.close();
		 }
	}

	private boolean useJmx = false;

	@Test
	public void testGo() throws Exception {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 context.start();
			 
			 // watch out -- without the sleep in here, the framework has time to wrap around and schedule another
			 // run before the first run is finished, producing two invocations of the pipeline.
			 Thread.sleep(1*1000);

			 oneshot.enable();
			 
			 Thread.sleep(1*1000);
			 System.out.println("Stopping");
			 context.stop();
			 
			 assertEquals("enable after first run has no effect", 3, counter.get());

		 } finally {
			 context.close();
		 }
	}

	@Test
	public void testDisable() throws Exception {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 
			 oneshot.disable();
			 
			 context.start();

			 Thread.sleep(5*1000);
			 System.out.println("Stopping");
			 context.stop();
			 
			 assertEquals("disabled means no traffic", 0, counter.get());

		 } finally {
			 context.close();
		 }
	}

	@Test
	public void testStartTwice() throws Exception {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting 1ce");
			 			 
			 context.start();

			 Thread.sleep(1*1000);
			 System.out.println("Starting 2ce");

			 context.start();
			 
			 // give it time to finish
			 Thread.sleep(1*1000);
			 // assertFalse("not running", context.isRunning());
			 
			 assertEquals("started twice, but only enabled once", 3, counter.get());
		 } finally {
			 context.close();
		 }
	}

	@Test
	public void testStartTwiceWithEnable() throws Exception {
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting 1ce");
			 			 
			 context.start();

			 Thread.sleep(1*1000);
			 
			 oneshot.enable();
			 System.out.println("Starting 2ce");

			 context.start();
			 
			 // give it time to finish
			 Thread.sleep(1*1000);

			 // So context.start does not reset the state of the polling endpoint
			 assertEquals("started twice, with enable", 3, counter.get());
		 } finally {
			 context.close();
		 }
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		System.out.printf("Observed application event %s\n", event.toString());
		
	}

}
