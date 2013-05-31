package gov.usgs.cida.data.grib;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import gov.usgs.cida.ncetl.sis.LatchTrigger;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OneshotTest {

	private ConfigurableApplicationContext context;
	private AtomicInteger counter;
	
	@Before
	public void init() {
		context = new ClassPathXmlApplicationContext("oneshot-context.xml");
		counter = context.getBean("counter", AtomicInteger.class);
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
			 
			 assertEquals("traffic", 1, counter.get());
		 } finally {
			 context.close();
		 }
	}

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
			 
			 assertEquals("enable after first run has no effect", 1, counter.get());

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
			 
			 assertEquals("started twice, but only enabled once", 1, counter.get());
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
			 assertEquals("started twice, with enable", 1, counter.get());
		 } finally {
			 context.close();
		 }
	}

}
