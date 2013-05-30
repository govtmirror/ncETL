package gov.usgs.cida.data.grib;

import static org.junit.Assert.*;
import gov.usgs.cida.ncetl.sis.LatchTrigger;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OneshotTest {

	
	@Test
	public void testLoad() {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("oneshot-context.xml");
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 System.out.printf("config-fetcher %s\n", oneshot);
			 assertNotNull("oneshot", oneshot);
		 } finally {
			 context.close();
		 }
	}
	
	@Test
	public void testStart() throws Exception {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("oneshot-context.xml");
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 context.start();
			 Thread.sleep(60*1000);
			 System.out.println("Stopping");
			 context.stop();
		 } finally {
			 context.close();
		 }
	}

	@Test
	public void testGo() throws Exception {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("oneshot-context.xml");
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 context.start();
			 
			 oneshot.enable();
			 
			 Thread.sleep(60*1000);
			 System.out.println("Stopping");
			 context.stop();
		 } finally {
			 context.close();
		 }
	}

	@Test
	public void testGoThenStart() throws Exception {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("oneshot-context.xml");
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 
			 oneshot.enable();
			 
			 context.start();

			 Thread.sleep(60*1000);
			 System.out.println("Stopping");
			 context.stop();
		 } finally {
			 context.close();
		 }
	}

	// fails; pipeline only gets started once.
	@Test
	public void testGoTwice() throws Exception {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("oneshot-context.xml");
		
		 try {
			 LatchTrigger oneshot = context.getBean("oneshot", LatchTrigger.class);
		 
			 assertNotNull("oneshot", oneshot);
			 
			 System.out.println("Starting");
			 
			 oneshot.enable();
			 
			 context.start();

			 Thread.sleep(5*1000);
			 oneshot.enable();
			 Thread.sleep(5*1000);
			 oneshot.enable();
			 Thread.sleep(5*1000);

			 System.out.println("Stopping");
			 context.stop();
		 } finally {
			 context.close();
		 }
	}

}
