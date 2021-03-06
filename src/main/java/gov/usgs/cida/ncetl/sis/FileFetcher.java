package gov.usgs.cida.ncetl.sis;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.usgs.cida.ncetl.jpa.ArchiveConfig;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;

class YearMonthFileFilter implements FileFilter {
    private Pattern rfcPattern;
    private int targetYear;
    private int targetMonth;
    
	private Logger logger = LoggerFactory.getLogger(getClass());

	public YearMonthFileFilter(Pattern rfcPattern, int targetYear,
			int targetMonth) {
		super();
		this.rfcPattern = rfcPattern;
		this.targetYear = targetYear;
		this.targetMonth = targetMonth;
	}

	@Override
	public boolean accept(File pathname) {
		logger.trace("checking {}", pathname.getName());

		Matcher matcher = rfcPattern.matcher(pathname.getName());
		
		if ( ! matcher.matches()) {
			logger.trace("no match with {}", rfcPattern);
			return false;
		}
		
		int y = Integer.parseInt(matcher.group(1));
		int m = Integer.parseInt(matcher.group(2));
		
		logger.trace("checking y m {} {}", y, m);
		
		boolean v = (y == targetYear && m == targetMonth);
		if (v) {
			logger.trace("did not match {} {}", targetYear, targetMonth);
		}
		return v;
	}

}

public class FileFetcher {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private DateTime now = new DateTime();
	
	public FileFilter fileFilterFor(ArchiveConfig cfg) {
		String fileRegex = cfg.getFileRegex();
        final Pattern rfcPattern = Pattern.compile(fileRegex);
        
        Matcher m = rfcPattern.matcher("nil");
        if (m.groupCount() != 3) {
        	throw new RuntimeException("Expected three capturing groups in " + fileRegex + ", got " + m.groupCount());
        }

		DateTime targetDate = startOfPreviousMonth();
		final int targetMonth = targetDate.getMonthOfYear();
		final int targetYear = targetDate.getYear();
		
		logger.info("Searching {} for files of pattern {} in month {}-{}",
				new Object[] {cfg.getInputDir(), fileRegex, targetYear, targetMonth});

		FileFilter monthFilter = new YearMonthFileFilter(rfcPattern, targetYear, targetMonth);

		return monthFilter;
	}
	
	@Transformer
	public Message<List<File>> listInputFiles(ArchiveConfig cfg) throws IOException {
				
		File inputDir = new File(cfg.getInputDir());
        				
		FileFilter monthFilter = fileFilterFor(cfg);
		
        File[] listFiles = inputDir.listFiles( monthFilter );
        if (listFiles == null) {
        	logger.warn("failed to list files in {}", inputDir);
        	listFiles = new File[0];
        }
        logger.info("Found {} files", listFiles.length);
        
        // this sort uses the natural order for files, which will result in ascending time order for files within the same
        // directory as long as the file names differ only by date represented as yyyy-MM-dd format (as required for file
        // name parsing).
        Arrays.sort(listFiles);
        
        List<File> payload = Arrays.asList(listFiles);
        
		MessageBuilder<List<File>> mb = MessageBuilder.withPayload(payload);
		mb.setHeader("config", cfg);
		
		return mb.build();
	}

	protected String makeOutputFileName(int year, int month, int rfcCode) {
	    String ofn = "QPE." + year + "." + month + "." + rfcCode + ".nc";
	    ofn = MessageFormat.format("QPE.{0,number,00}.{1,number,00}.{2,number,000}.nc", year, month, rfcCode);
		return ofn;
	}

	protected Date oneMonthAgo() {
		DateTime lastRun = now.minusMonths(1);
		return lastRun.toDate();
	}
	
	protected DateTime startOfPreviousMonth() {
		DateTime lastMonth = now.minusMonths(1);
		DateTime firstOfMonth = lastMonth.withDayOfMonth(1).withTimeAtStartOfDay();
		return firstOfMonth;
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

		Collections.sort(payload);
		
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
        	mb.setHeader("outputFile", ofName);
        	logger.debug("Set output file for {} to {}", f, ofName);
        	
        	mb.pushSequenceDetails(ofName, day, daysInMonth(year, month));
        	
        	value.add(mb.build());
        	
        }
        
        return value;
		
	}
	
	// for testing
	public void setNow(Date d) {
		now = new DateTime(d);
	}
}
