package gov.usgs.cida.ncetl.sis;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;

public class FileFetcher {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Transformer
	public Message<List<File>> getConfig(ArchiveConfig cfg) {
				
		File inputDir = new File(cfg.getInputDir());
		String fileRegex = cfg.getFileRegex();
		
		// TODO get last run time for this config, rather than assuming one month
		Date lastRun = oneMonthAgo();
		
		AgeFileFilter ageFilter = new AgeFileFilter(lastRun);
        Pattern rfcPattern = Pattern.compile(fileRegex);
        RegexFileFilter patternFilter = new RegexFileFilter(rfcPattern);
		FileFilter filter = new AndFileFilter(ageFilter,patternFilter);
        File[] listFiles = inputDir.listFiles(filter);	
        
        List<File> payload = Arrays.asList(listFiles);
		
		MessageBuilder<List<File>> mb = MessageBuilder.withPayload(payload);
		mb.setHeader("config", cfg);
		
		return mb.build();
	}

	protected String makeOutputFileName(int year, int month, int rfcCode) {
	    String ofn = "QPE." + year + "." + month + "." + rfcCode + ".nc";
		return ofn;
	}

	protected Date oneMonthAgo() {
		DateTime now = new DateTime();
		DateTime lastRun = now.minusMonths(1);
		return lastRun.toDate();
	}
	
	protected int daysInMonth(int year, int month) {
		DateTime dt = new DateTime(year, month, 1, 12, 0);
    	dt = dt.dayOfMonth().withMaximumValue();
    	return dt.getDayOfMonth();
	}
	
	@Splitter
	public List<Message<File>> split(
			List<File> payload,
			@Header(value="config", required=true) ArchiveConfig cfg
	) {
		
		List<Message<File>> value = new ArrayList<Message<File>>(payload.size());
				
        Pattern pat = Pattern.compile(cfg.getFileRegex());

        for (File f : payload) {
        	MessageBuilder<File> mb = MessageBuilder.withPayload(f);
        	mb.setHeader("rfc", cfg.getRfcCode());
        	mb.setHeader("config", cfg);
        	
        	String fn = f.getName();
        	Matcher m = pat.matcher(fn);
        	
        	int year = 0;
        	int month = 0;
        	int day = 0;
        	if (m.matches()) {
        		String year_s = m.group(1);
        		String month_s = m.group(2);
        		String day_s = m.group(3);
        		
        		year = Integer.parseInt(year_s);
        		month = Integer.parseInt(month_s);
        		day = Integer.parseInt(day_s);
        		
        		mb.setHeader("year", Integer.valueOf(year));
        		mb.setHeader("month", Integer.valueOf(month));
        		mb.setHeader("day", Integer.valueOf(day));	// not used
        	} else {
        		// complain
        		logger.warn("File name {} does not match regex {} for RFC {}",
        				new Object[] {fn, pat.toString(), cfg.getRfcCode()});
        	}
        	
        	String ofName = makeOutputFileName(year,month,cfg.getRfcCode());
        	mb.setHeader("outputFile", makeOutputFileName(year,month,cfg.getRfcCode()));
        	
        	mb.setCorrelationId(ofName);
        	mb.setSequenceNumber(day);
        	mb.setSequenceSize(daysInMonth(year, month));
        	
        	value.add(mb.build());
        	
        }
        
        return value;
		
	}
}
