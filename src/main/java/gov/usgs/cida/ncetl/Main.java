package gov.usgs.cida.ncetl;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("jpa-integration-context.xml");
		 
		 context.start();
		 
		 // TODO See one shot test...

	}

}
