package gov.usgs.cida.ncetl;

import gov.usgs.cida.ncetl.sis.FileFetcher;

import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.core.PollableChannel;
import org.springframework.integration.store.MessageGroupStore;

public class Main {

	public static final int SECOND = 1000;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		 ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("jpa-integration-context.xml");
		 logger.debug("Loaded context {}", context);
		 
		 if (args.length > 0) {
			 // parse arg as yyyy-mm, use that for target date
			 DateMidnight then = DateMidnight.parse(args[0]);
			 
			 FileFetcher ff = context.getBean(FileFetcher.class);  // there better be only one
			 // send in next month as FF targets month previous to "now"
			 ff.setNow(then.plusMonths(1).toDate());
		 }
		 
		 context.start();
		 logger.info("Started context {}", context);

		 PollableChannel finis = context.getBean("finis", PollableChannel.class);
		 PollableChannel errQueue = context.getBean("error-queue-chan", PollableChannel.class);

		 MessageGroupStore messageGroupStore = context.getBean("message-store",MessageGroupStore.class);

		 // wait for a bit then kick the partial message groups along
		 Thread.sleep(1 * MINUTE);
		 logger.info("First timout expired, will expire message groups (ct {} in {} groups)", 
				 messageGroupStore.getMessageCountForAllMessageGroups(),
				 messageGroupStore.getMessageGroupCount());
		 messageGroupStore.expireMessageGroups(0);
		 
		 
		 // Timeout is a safety measure; we expect to be notified by the aggregated queue, but this may fail if
		 // there are no files found (no files -> no messages, sigh).
		 long timeout = 1 * MINUTE;
		 
		 logger.info("Second timout expired, will wait for final message on {}", 
				 finis);
		 Message<?> msg = finis.receive(timeout);
		 logger.debug("Got final message {}", msg);
		 
		 int exitCode = 0;
		 
		 // check for errors
		 msg = errQueue.receive(1 * SECOND);
		 if (msg != null) {
			 // error should already have been reported by the SiS error channel, but to be safe:
			 logger.info("reporting error {}", msg);
			 exitCode = 1;
		 }

		 // This triggers release of any remaining partial aggregation groups.
		 logger.info("Stopping context {}", context);
		 context.stop();
		 
		 System.exit(exitCode);
	}

}
