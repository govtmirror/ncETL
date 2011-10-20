package gov.usgs.cida.ncetl.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Partial;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

/**
 *
 * @author jwalker
 */
public class DateIndexer extends HttpServlet {

	public static final String USAGE = "java DateIndexer \"${StartDate}\" \"${EndDate}\"";
    private static final long serialVersionUID = 1L;
    private static final int[] LEAP_DAY =  {2, 29};
    
    enum lengths {
        SECOND(Seconds.ONE, "second", "sec", "s"),
        MINUTE(Minutes.ONE, "minute", "min"), // Don't allow "m" that could mean month
        HOUR(Hours.ONE, "hour", "hr", "h"),
        DAY(Days.ONE, "day", "d"),
        WEEK(Weeks.ONE, "week"),
        MONTH(Months.ONE, "month"),
        QUARTER(Months.FOUR, "quarter", "qtr"),
        YEAR(Years.ONE, "year", "yr", "y"),
        DECADE(Years.years(10), "decade"),
        CENTURY(Years.years(100), "century");
        
        ReadablePeriod per;
        Map<String, lengths> descMap;
        
        lengths(ReadablePeriod base, String... descriptions) {
            this.per = base;
            for(String desc : descriptions) {
                this.descMap.put(desc, this);
            }
        }
        
        lengths getLength(String desc) {
            return SECOND;
        }
    }

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//options.addOption("s", "skip-day", true, "Do not output index for this day");
		String stride = request.getParameter("stride");
		String origin = request.getParameter("origin");
		String end = request.getParameter("end");
		String steps = request.getParameter("steps");
        String stepLength = request.getParameter("stepLength");
		if (null != origin && null != end) {
			DateMidnight startDate = new DateMidnight(origin);
			DateMidnight endDate = new DateMidnight(end);
			DateTimeFieldType[] fields = {DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()};
			Partial leapDay = new Partial(fields, LEAP_DAY);
//			List<Partial> skipDates = new ArrayList<Partial>();  // Hard to parse String to partial
//			if (null != skips) {
//				for(String skip : skips) {
//					skipDates.add(new Partial(skip));
//				}
//			}
			printSuccess(response, getTimesNoStride(startDate, endDate, leapDay));
		}
		else {
			printNotImplemented(response);
		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public static String getTimesNoStride(DateMidnight origin, DateMidnight end, Partial... skipped) {
		StringBuilder strBuild = new StringBuilder();
		MutableDateTime current = new MutableDateTime(origin);
		int index = 0;
		date:
			while (current.isBefore(end)) {
			index++;
			current.addDays(1);
			for (Partial skip : skipped) {
				if (skip.isMatch(current)) {
					continue date; //skip this timestep
				}
			}
			strBuild.append(Integer.toString(index)).append(" ");
		}
		return strBuild.toString();
	}

	private void printNotImplemented(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain;charset=UTF-8");
		response.setStatus(response.SC_NOT_IMPLEMENTED);
		PrintWriter out = response.getWriter();
		out.print("This request is not yet supported, try something more simple");
	}

	private void printSuccess(HttpServletResponse response, String timesNoStride) throws IOException {
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(timesNoStride);
	}

}
