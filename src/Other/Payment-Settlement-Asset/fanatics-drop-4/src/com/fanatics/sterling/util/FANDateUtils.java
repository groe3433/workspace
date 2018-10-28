package com.fanatics.sterling.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.yantra.yfs.japi.YFSException;


public class FANDateUtils {

	/**
	 * Gets the future(number of days in future) date in the specified output
	 * format
	 * 
	 * @param outputFormat
	 *            Output format of the future date
	 * @param futureNoOfDays
	 *            Number of days to go in the future compared to current date
	 * @return Future date in the output format specified
	 * @throws UtilException
	 */
	public static java.util.Date getFutureDate(java.util.Date inDate,
			int futureNoOfDays) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inDate);
		cal.add(Calendar.DATE, futureNoOfDays);
		return cal.getTime();
	}
	
	/**
	 * Gets the future(number of days in future) date in the specified output
	 * format
	 * 
	 * @param outputFormat
	 *            Output format of the future date
	 * @param futureNoOfDays
	 *            Number of days to go in the future compared to current date
	 * @return Future date in the output format specified
	 * @throws UtilException
	 */
	public static String getFutureDate(String outputFormat, int futureNoOfDays)
			throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, futureNoOfDays);
		return formatDate(outputFormat, cal.getTime());
	}

	/**
	 * Gets the current date in the default format of "yyyy-MM-dd"
	 * 
	 * @return Current date in "yyyy-MM-dd" format
	 */
	public static String getCurrentDate() {
		return getCurrentDateTime("yyyy-MM-dd");
	}

	/**
	 * Gets the current time in the default format of "YYYY-MM-DD HH:MM:SS"
	 */
	public static String getCurrentDateTime(String inputFormat) {
		return formatDate(inputFormat, new java.util.Date());
	}

	/**
	 * Formats the input date(java.util.Date) in the given output format
	 * 
	 * @param outFormat
	 *            Desired output format of the Date
	 * @param dte
	 *            Date to be formatted to output format
	 * @return Date in the output format specified
	 */
	public static String formatDate(String outFormat, java.util.Date dte) {
		SimpleDateFormat formatter = new SimpleDateFormat(outFormat);
		return formatter.format(dte);
	}
	
	public static String formatDate(String outFormat, TimeZone timeZone,
			java.util.Date dte) {
		SimpleDateFormat formatter = new SimpleDateFormat(outFormat);
		formatter.setTimeZone(timeZone);
		return formatter.format(dte);
	}
	
	public  String formatDate(String inFormat, String outFormat,
			TimeZone timeZone, String strDate) throws Exception {
		try {
			if (strDate.equals(""))
				return "";
			SimpleDateFormat formatter = new SimpleDateFormat(inFormat);
			return formatDate(outFormat, timeZone, formatter.parse(strDate));
		} catch (ParseException e) {
			throw new YFSException(e.getMessage());
		}
	}
	
	public static  String formatDate(String inFormat, String outFormat,
			 String strDate) throws Exception {
		try {
			if (strDate.equals(""))
				return "";
			SimpleDateFormat formatter = new SimpleDateFormat(inFormat);
			return formatDate(outFormat, formatter.parse(strDate));
		} catch (ParseException e) {
			throw new YFSException(e.getMessage());
		}
	}
}
