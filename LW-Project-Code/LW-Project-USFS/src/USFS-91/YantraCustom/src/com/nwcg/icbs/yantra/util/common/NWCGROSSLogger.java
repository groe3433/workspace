package com.nwcg.icbs.yantra.util.common;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.yantra.yfc.log.YFCLogCategory;

public class NWCGROSSLogger extends YFCLogCategory {
	
	protected static YFCLogCategory logger = null;
	protected static NWCGROSSLogger nlogger = null;
	
	protected NWCGROSSLogger(String name) {
		super(name);
	}
	public static NWCGROSSLogger instance(Class clazz)
	{
			return instance(clazz.getName());
    }
	public static NWCGROSSLogger instance(String name)
	{
		if ( logger == null ) {
			String agentServer = System.getenv("AGENT_JAVA_SERVER");
			if ( agentServer != null ) {
				logger =  YFCLogCategory.instance("CONSOLE");
				nlogger = new NWCGROSSLogger("CONSOLE");
				logger.verbose("Set logger to CONSOLE, agent = "+agentServer);
			}
			else {
				logger =  YFCLogCategory.instance("NWCGROSSLogger");
				nlogger = new NWCGROSSLogger("NWCGROSSLogger");
				logger.verbose("Set logger to NWCGROSSLogger");
				
			}
		}
	    return nlogger;
	}
	
	public void info(String message){
		logger.verbose(message);
	}
	public void debug(String message){
		logger.debug(message);
	}
	public void error(String message){
		logger.error(message);
	}
	public void verbose(String message){
		logger.verbose(message);
	}
	public void warn(String message){
		logger.warn(message);
	}
	public void timer(String message){
		logger.warn(message);
	}
	public void fine(String message) {
		logger.trace(message);
	}
	public void finer(String message) {
		logger.trace(message);
	}
	public void finest(String message) {
		logger.trace(message);
	}
	public void info(Object message){
		logger.info(message);
	}
	public void debug(Object message){
		logger.debug(message);
	}
	public void error(Object message){
		logger.error(message);
	}
	public void warn(Object message){
		logger.warn(message);
	}
	public void timer(Object message){
		logger.timer(message);
	}
	public void info(String message,Exception ex){
		logger.verbose(message,ex);
	}
	public void debug(String message,Exception ex){
		logger.debug(message,ex);
	}
	public void error(String message,Exception ex){
		logger.error(message,ex);
	}
	public void error(String message,Throwable t){
		logger.error(message,t);
	}
	public void verbose(String message,Exception ex){
		if ( logger.isVerboseEnabled() )
			logger.verbose(message,ex);
	}
	public void warn(String message,Exception ex){
		logger.warn(message,ex);
	}
	public void error(String message, Object[] args, Exception ex) {
		logger.error(message,args,ex);
	}
    public void error( String  msg, Document document) throws Exception {
    	if ( isErrorEnabled() )
    		logger.error((msg + XMLUtil.extractStringFromDocument(document)));
    }
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
	public boolean isVerboseEnabled() {
		return logger.isVerboseEnabled();
	}
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
	public boolean isErrorEnabled() {
		return true;
	}
	public void logp(Level level, String className, String methodName, String message) {
		logger.trace(className+","+methodName+" "+message);
	}
	public void printStackTrace(Exception ex) {
		logger.error("Exception:",ex);
	}
	public void writeTo(SOAPMessage soapMsg) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		soapMsg.writeTo(baos);
		logger.info(baos);
}

}
