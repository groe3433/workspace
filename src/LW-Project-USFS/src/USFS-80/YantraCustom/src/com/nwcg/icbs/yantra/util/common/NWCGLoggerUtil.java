/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
                     LIMITATION OF LIABILITY
THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
*/

package com.nwcg.icbs.yantra.util.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This Log Util can be used as follows:
 *  --------------------------------------------------
 *    Import: 	  import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
 *	  Invocation: NWCGLoggerUtil.Log.severe("msg");
 *  --------------------------------------------------
 * Four parameters that is from Property files are:
 * NWCGLoggerUtil.UseParent:   If set to false, log message will not go to 
 *                             the regular System.out. 
 *                             Note that if it is set to false, WAS won't be
 *                             able to pick it up in the log and trace utility. 
 * NWCGLoggerUtil.LogFileName: Name of the log file. See java.util.Logger for file 
 *                             name pattern. 
 * NWCGLoggerUtil.LogFileSize: Size of the log file when it do rotate. 
 * NWCGLoggerUtil.LogFileTotal:Total number of the log files before it reuse old log. 
 */
public class NWCGLoggerUtil {

	public static java.util.logging.Logger Log;
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS z");

	static {init();}
	private NWCGLoggerUtil() {}
    
    private static void init() {
    
    	Log = java.util.logging.Logger.getLogger(NWCGLoggerUtil.class.getName());

    	//boolean useParent = ResourceUtil.getAsBoolean("NWCGLoggerUtil.UseParent", false);
    	//boolean useParent = true;
    	boolean useParent = Boolean.getBoolean(ResourceUtil.get("NWCGLoggerUtil.UseParent","false"));
    	String fileNamePattern = ResourceUtil.get("NWCGLoggerUtil.LogFileName", "A2A_File%g.log");
    	int fileSize = Integer.parseInt(ResourceUtil.get("NWCGLoggerUtil.LogFileSize", "10000000"));
    	int fileRotate =Integer.parseInt(ResourceUtil.get("NWCGLoggerUtil.LogFileTotal", "50"));  
    	String loglevelStr = ResourceUtil.get("NWCGLoggerUtil.LogLevel");
    	System.out.println("A&A log level in resources.jar!/resources/extn/NWCGAnAEnvironment.properties: "+loglevelStr);
    	Level logLevel = Level.parse(ResourceUtil.get("NWCGLoggerUtil.LogLevel", "INFO")); 
    	
    	// No parent handlers, so default System.out won't print log now. 
    	Log.setUseParentHandlers(useParent);    	
    	Log.setLevel(logLevel);
        
    	try {
    	  FileHandler fd = new FileHandler(fileNamePattern, fileSize, fileRotate, true);
    	  fd.setFormatter(new Formatter() {
    		  public String format(LogRecord record) {
    				if(record.getLevel() ==  Level.SEVERE || record.getLevel() ==  Level.WARNING) {
    					return "[" + getDate() + ":" +record.getLevel()+"] " + "[" +
    			         record.getSourceClassName() + ":" +
    			         record.getSourceMethodName() + "] "  + "[Thread-"+record.getThreadID()+"] "+
    			         record.getMessage() + "\r\n";
    				}
    				else {
    					return getDate() + ":"+ record.getLevel()+ " [Thread-"+record.getThreadID()+"] "+
    			         record.getMessage() + "\r\n";
    				}
    			}
    	    });
		  Log.addHandler(fd);
		}    	
		catch (IOException e) {
		NWCGLoggerUtil.Log.warning("  init Log got error " + e.getMessage());
		  e.printStackTrace();
		}
    }	
    
    public static void printStackTraceToLog(Exception e){
     	java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
    	Log.severe("------\r\n" + sw.toString() + "------\r\n");
    }
    
	public static String getDate() {
		Date date = new Date();
        return sf.format(date);
	}
	
	public static void main(String[] args) {
		NWCGLoggerUtil.Log.warning(" Just a test ");
		NWCGLoggerUtil.Log.severe("Severe");
		NWCGLoggerUtil.Log.info("just info");		
	}
}