package gov.usgs.cida.data.grib;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.MessageTransformingHandler;

public class IntegrationTest {

	
	@Test
	public void testLoad() {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("spring-integration-context.xml");
		
		 try {
			 EventDrivenConsumer enricher = context.getBean("rfc-spec-enricher", EventDrivenConsumer.class);
		 
			 // EventDrivenConsumer with handler = MessageTransformingHandler
			 MessageTransformingHandler handler = /* enricher.handler; */ null;
			 HeaderEnricher transformer = null;
			 
			 System.out.printf("enricher %s\n", enricher);
		 } finally {
			 context.close();
		 }
	}
	
	@Test
	public void testEnrich() throws InterruptedException {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("enricher-test-integration-context.xml");
		
		 try {
			 EventDrivenConsumer enricher = context.getBean("rfc-spec-enricher", EventDrivenConsumer.class);
		 
			 // EventDrivenConsumer with handler = MessageTransformingHandler
			 MessageTransformingHandler handler = /* enricher.handler; */ null;
			 HeaderEnricher transformer = null;
			 
			 System.out.printf("enricher %s\n", enricher);
			 
			 Thread.sleep(10 * 1000);
		 } finally {
			 context.close();
		 }
	}

}
