package gov.usgs.cida.ncetl.sis;

import gov.usgs.cida.data.grib.ArchiveInfoI;
import gov.usgs.cida.ncetl.jpa.ArchiveConfig;
import gov.usgs.cida.ncetl.jpa.EtlHistoryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.MessagingException;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;

public class ErrorRecorder {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EtlHistoryManager manager;
		
	@Transformer
	public Message<String> recordError(
			MessagingException me,
			@Header(value="rfc", required=false) Integer rfc) {
		
		MessageBuilder<String> mb =  MessageBuilder.withPayload(me.toString());

		Message<?> oops = me.getFailedMessage();
		mb.copyHeaders(oops.getHeaders());

		if (rfc == null) {
			if (oops.getHeaders().containsKey("rfc")) {
				// slightly odd way to handle rfc whether it's Integer or String
				rfc = Integer.getInteger(oops.getHeaders().get("rfc").toString());
			}
		}
		
		if (rfc == null) {
			Throwable x = me.getCause();
			if (x instanceof MessagingException) {
				x = ((MessagingException)x).getCause();
			}
			if (x instanceof ArchiveInfoI) {
				rfc = ((ArchiveInfoI)x).getRfcCode();
			}
		}

		if (rfc == null) {
			logger.warn("Could not find rfc to record output for {}", me);
		} else {
			logger.debug("Recording error outcome for {}", rfc);
			mb.setHeader("rfc", rfc);
			manager.recordError(me.toString(), rfc);
		}
				
		return mb.build();
	}

	@Transformer
	public Message<String> recordError(
			ArchiveInfoI ax) {
		
		MessageBuilder<String> mb =  MessageBuilder.withPayload(ax.toString());

		int rfc = ax.getRfcCode();
		mb.setHeader("rfc", rfc);

		logger.debug("Recording error outcome for {}", rfc);
		manager.recordError(ax.toString(), rfc);
				
		return mb.build();
	}

	@Transformer
	public String recordOutcome(ArchiveConfig cfg, String outcome) {
		manager.recordOk(cfg, outcome);
		
		return outcome;
	}
}
