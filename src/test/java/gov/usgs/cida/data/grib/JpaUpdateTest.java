package gov.usgs.cida.data.grib;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Properties;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.ChannelInterceptor;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.ChannelInterceptorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class JpaUpdateTest {

	private ConfigurableApplicationContext context;
	
	@Before
	public void init() {
		context = new ClassPathXmlApplicationContext("jpa-update-context.xml");
	}
	
	@Test
	public void testTxManager() throws Exception {
		EntityManagerFactory emf = context.getBean("emf", EntityManagerFactory.class);

		System.out.println("EMF properties");
		Map<String,Object> props = emf.getProperties();
		for (Map.Entry<String, Object> entry : props.entrySet()) {
			System.out.printf("\t%s: %s\n",  entry.getKey(), entry.getValue());
		}
	}
	
	@Test
	public void testStart() throws Exception {
		
		 try {
		 
			 System.out.printf("loaded\n");
			 
			 context.start();
			 
			 Thread.sleep(8*1000);
			 
			 assertTrue("survived", true);
			 
			 QueueChannel errorChannel = context.getBean("errorChannel", QueueChannel.class);
			 Message<?> msg = errorChannel.receive(1);

			 assertNull("error message", msg);
			 
			 context.stop();
			 
		 } finally {
			 context.close();
		 }
	}
	
	@Test
	public void updateConnected() throws Exception {
		EntityManagerFactory emf = context.getBean("emf", EntityManagerFactory.class);
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction t = em.getTransaction();
		t.begin();
		
		TypedQuery<ArchiveConfig> q = em.createQuery("select s from ArchiveConfig s where rfcCode = 155 ", ArchiveConfig.class);
				
		ArchiveConfig arc = q.getSingleResult();
		
		arc.addHistory("steam heat");
		
		t.commit();
	}

	@Test
	public void updateDetached() throws Exception {
		EntityManagerFactory emf = context.getBean("emf", EntityManagerFactory.class);
		
		EntityManager em = emf.createEntityManager();
		
		EntityTransaction t = em.getTransaction();
		
		t.begin();
		
		TypedQuery<ArchiveConfig> q = em.createQuery("select s from ArchiveConfig s where rfcCode = 156 ", ArchiveConfig.class);
		
		ArchiveConfig arc = q.getSingleResult();
		
		em.detach(arc);
		t.commit();
		
		
		
		t.begin();
		arc = em.merge(arc);
		arc.addHistory("detached heat");
		t.commit();
	}

}
