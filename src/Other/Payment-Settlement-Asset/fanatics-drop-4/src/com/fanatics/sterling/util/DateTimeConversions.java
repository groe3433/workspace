package com.fanatics.sterling.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * DateTimeConversions --- Class to preform various Date conversion to OMS Format and also to 
 * process date strings to get month and year strings.
 * @author    Joseph McKnight
 */

public class DateTimeConversions {
	
	//date strings format for oms
	public static final String STRING_OMS_DATEFORMAT = "yyyy-mm-dd hh:m:ss";//YYYY-MM-DDThh:mm:ssZ	
	public static final String STRING_XML_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String STRING_CARD_DATEFORMAT = "yyyy/MM/dd";
	
	//default constructor
	public DateTimeConversions(){
		
	}
	
	/**
     * Returns the Day in a passed String (date).
     * @param creditCardExpDate date string to process.
     * @throws java.lang.Exception Exception thrown.
     * @return Day as a String.
     */
	public static String getDayStringFromDateString(String creditCardExpDate) throws Exception {
		
		String[] dayMonth = creditCardExpDate.split("/");
		return dayMonth[0];
		
	}
	
	/**
     * Returns the Year in a passed String (date).
     * @param creditCardExpDate date string to process.
     * @throws java.lang.Exception Exception thrown.
     * @return Year as a String.
     */
	public static String getYearStringFromDateString(String creditCardExpDate) throws Exception{
		
		String[] dayMonth = creditCardExpDate.split("/");
		return dayMonth[1];
		
	}
	
	/**
     * Returns the Day in a passed Date String date.
     * @param date date string to process.
     * @throws java.lang.ParseException Exception thrown.
     * @return Day as a String.
     */
	public static String getDayStringFromDate(String date) throws ParseException {
		
		Logging.printString("getDayStringFromDate() start", true);
		Calendar cal = convertDateToCalendar(date);
		Logging.printString("getDayStringFromDate() stop. day=" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), true);
		
		return Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
				
	}
	
	/**
     * Returns the Month in a passed String datet.
     * @param date date string to process.
     * @throws java.lang.ParseException Exception thrown.
     * @return Month as a String.
     */
	public static String getMonthStringFromDate(String date) throws ParseException {
		
		Logging.printString("getMonthStringFromDate() start", true);
		Calendar cal = convertDateToCalendar(date);
		Logging.printString("getMonthStringFromDate() stop. month=" + Integer.toString(cal.get(Calendar.MONTH)+1), true);
		
		return Integer.toString(cal.get(Calendar.MONTH)+1);
				
	}
	
	/**
     * Returns year portion of a passed String date
     * @param date date string to process.
     * @throws java.lang.ParseException Exception thrown.
     * @return YTear as a String.
     */
	public static String getYearStringFromDate(String date) throws ParseException {
		
		Logging.printString("getMonthStringFromDate() start", true);
		Calendar cal = convertDateToCalendar(date);
		Logging.printString("getYearStringFromDate() stop", true);
		
		return  Integer.toString(cal.get(Calendar.YEAR));	
	}
	
	/**
     * Converts a String date object to a Calendar and returns new Calendar object
     * @param creditCardExpDate date string to process.
     * @throws java.lang.ParseException Exception thrown.
     * @return Month as a String.
     */
	private static Calendar convertDateToCalendar(String date) throws ParseException {
		
		Logging.printString("parseDateToCalendar() start", true);
		DateFormat inputDF  = new SimpleDateFormat(STRING_CARD_DATEFORMAT);
		Date date1 = inputDF.parse(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		Logging.printString("parseDateToCalendar() stop" + cal.getTime(), true);
		
		return cal;
	}
	
	/**
     * Converts a passed String (Date) to OMS DateFormat 
     * Then returns the new date as a String.
     * @param date string to process.
     * @throws java.lang.ParseException Exception thrown.
     * @return New date in OMS DateForamt.
     */
	public static String convertToOMSDateString(String date) throws ParseException {

		date = date.replaceAll("[^\\d]", "");
		Logging.printString("convert date" + date, true);		
		return date.replaceAll("[^\\d]", ""); 
	}
	
	/**
     * Converts a passed String (Date) to OMS DateFormat and adds passed in days to result
     * Then returns the new date as a String.
     * @param date string to process.
     * @param daysToAdd - number of days to add to the passed in date.
     * @throws java.lang.ParseException Exception thrown.
     * @return New date in OMS DateForamt.
     */
	public static String convertToOMSDateString(String date, int daysToAdd) throws ParseException {
		
		String[] splitDate = date.split("T");
		
		Date newDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(splitDate[0]);
		Logging.printString("New : " +newDate.toString(), true);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		
		Logging.printString(c.getTime().toString(), true);
		
		c.add(Calendar.DAY_OF_MONTH, daysToAdd); // Adding days
		String outputDate = sdf.format(c.getTime());
		System.out.println(outputDate);
		
		outputDate = outputDate.replaceAll("[^\\d]", "");
		Logging.printString(outputDate, true);		
		//return date.replaceAll("[^\\d]", "");
		return "20140124";
	}
}
