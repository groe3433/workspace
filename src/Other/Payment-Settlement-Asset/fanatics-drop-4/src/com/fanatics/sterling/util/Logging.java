package com.fanatics.sterling.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;

/**
 * Logging --- Class to preform various Logging to Console 
 * @author    Joseph McKnight
 */
public class Logging {
		
	static boolean isLive;
	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");
		
	public Logging() {
		
		//isLive = false;
		
		String mode = YFSSystem.getProperty("cybersource.SendToProduction");
		if(mode != null ) {
		
			Logging.printString("In LIve Mode =" + mode, true);
			if(mode.contains("false"))
				isLive = false;
			else
				isLive = true;	
		}
		else
			isLive = false;
			
	}
	
	/**
     * Prints a String to the console
     * @param message to print toi the console    
     */
	public static void printString(String message, boolean isDebugginInfo) {
		
		logger.info(message + "\n");
		
		System.out.println(message + "\n");
		/*
		if(!isLive)
			System.out.println(message + "\n"); //print everything to the console
		else if (isLive && !isDebugginInfo)
			System.out.println(message + "\n"); //use logger and only print non debugging info
			*/
		//else need to output to the Sterling Logger TODO
			
	}
	
	/**
     * Prints a Map to the console by iterating through all ley value String pairs
     * @param map to print to the console
     * @param header - a String message to print to the console before printing passed HashMap     
     */
	@SuppressWarnings("rawtypes")
	public static void displayMap( String header, HashMap<String, String> map )
    {
		printString(header, false);
	    
		StringBuffer dest = new StringBuffer();		
		try{
			if (map != null && !map.isEmpty()){
				Iterator iter = map.keySet().iterator();
				String key, val;
				while (iter.hasNext()) {
					key = (String) iter.next();
					val = (String) map.get( key );
					dest.append( key + "=" + val + "\n" );
				}
			}
			} catch (Exception ex) {
				//if error displaying a doc then proceed as logging should not halt the program execution
			}
		
		printString(dest.toString(), false);		
    }
	
	/**
     * Prints a Map to the console by iterating through all ley value String pairs
     * @param map to print to the console
     * @param header - a String message to print to the console before printing passed HashMap     
     */
	@SuppressWarnings("rawtypes")
	public static void displayMap( String header, Map<String, String> map )
    {
		printString(header, false);
	    
		StringBuffer dest = new StringBuffer();		
		try{
			if (map != null && !map.isEmpty()){
				Iterator iter = map.keySet().iterator();
				String key, val;
				while (iter.hasNext()) {
					key = (String) iter.next();
					val = (String) map.get( key );
					dest.append( key + "=" + val + "\n" );
				}
			}
			} catch (Exception ex) {
				//if error displaying a doc then proceed as logging should not halt the program execution
			}
		
		printString(dest.toString(), false);		
    }
	
	/**
     * Prints a Map to the console by iterating through all key value String pairs
     * @param message to print to the console
     * @param Document - XML document to print as a String
    */
	public static void displayDocument(String message, Document displayDoc)
    {
		try {		
			printString(message, false);	    
			// Note that Utility.nodeToString() is meant to be used for logging
			// or demo purposes only.  As it employs some formatting
			// parameters, parsing the string it returns may not result to a
			// Node object exactly similar to the one passed to it.
			//printString(Utility.nodeToString(displayDoc));
		} catch (Exception ex) {
			//if error displaying a doc then proceed as logging should not halt the program execution
			
		}
    }	


}
