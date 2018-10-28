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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.log.YFCLogLevel;

/**
*	Logger helper.
*	@version 1.0 29 Oct. 2002
*	@author Harish Babber
*	@see LogUtil
*/
public class Logger
{
	org.apache.log4j.Logger logger;

	/**
	*	Construct a default Logger
	*/
	public Logger()
	{
		this.logger = LogUtil.getLogger();
	}

	/**
	*	Construct a Logger with specified name
	*	@param name Logger name
	*/
	public Logger(String name)
	{
		LogUtil.initLog4j(null);
		this.logger = LogUtil.getLogger(name);
	}

	/**
	*	Log DEBUG message
	* Raj Thota: Cautions using this method. Please try to use error( String  msg, Document document) instead for performance reasons.
	*/
    public void debug( Object msg )
    {
        if( logger.isEnabledFor(YFCLogLevel.DEBUG) )
            logger.log( YFCLogLevel.DEBUG, msg );
    }
    
    public void beginTimer(String methodName) {
    	if (logger.isEnabledFor(YFCLogLevel.TIMER))
    			logger.log( YFCLogLevel.TIMER, methodName);
    }
    
    public void endTimer(String methodName) {
    	if (logger.isEnabledFor(YFCLogLevel.TIMER))
			logger.log( YFCLogLevel.TIMER, methodName);
}
 
    /**
	*	Log DEBUG  message
	*	@param String message
	*	@param Document document
	*/
    public void debug( String  msg, Document document) throws Exception {
        if( logger.isEnabledFor(YFCLogLevel.DEBUG) ){
            logger.log( YFCLogLevel.DEBUG, (msg + XMLUtil.extractStringFromDocument(document)));
        }
    }
    
    /**
	*	Log DEBUG  message
	*	@param String message
	*	@param Element element
	*/
    public void debug( String  msg, Element document) throws Exception {
        if( logger.isEnabledFor(YFCLogLevel.DEBUG) ){
            logger.log( YFCLogLevel.DEBUG, (msg + XMLUtil.extractStringFromDocument(document.getOwnerDocument())));
        }
    }

	/**
	*	Log VERBOSE message
	* Raj Thota: Cautions using this method. Please try to use error( String  msg, Document document) instead for performance reasons.
	*/
    public void verbose( Object msg )
    {
        if( logger.isEnabledFor(YFCLogLevel.VERBOSE) )
            logger.log( YFCLogLevel.VERBOSE, msg );
    }
     
    
    /**
	*	Log VERBOSE  message
	*	@param String message
	*	@param Document document
	*/
    public void verbose( String  msg, Document document) throws Exception {
        if( logger.isEnabledFor(YFCLogLevel.VERBOSE) ){
            logger.log( YFCLogLevel.VERBOSE, (msg + XMLUtil.extractStringFromDocument(document)) );
        }
    }
    
    /**
	*	Log VERBOSE  message
	*	@param String message
	*	@param Element document
	*/
    public void verbose( String  msg, Element document) throws Exception {
        if( logger.isEnabledFor(YFCLogLevel.VERBOSE) ){
            logger.log( YFCLogLevel.VERBOSE, (msg + XMLUtil.extractStringFromDocument(document.getOwnerDocument())) );
        }
    }
	
    
    /**
	*	Log ERROR message
	*  Raj Thota: Cautions using this method. Please try to use error( String  msg, Document document) instead for performance reasons.
	*/
    public void error( Object msg )
    {
            logger.log( YFCLogLevel.ERROR, msg );
    }
    
      
    /**
	*	Log ERROR  message
	*	@param String message
	*	@param Document document
	*/
    public void error( String  msg, Document document) throws Exception {
        	logger.log( YFCLogLevel.ERROR, (msg + XMLUtil.extractStringFromDocument(document)) );
    }
    
    /**
	*	Log ERROR message
	*	@param String message
	*	@param Element element
	*/
    public void error( String  msg, Element document) throws Exception {
        	logger.log( YFCLogLevel.ERROR, (msg + XMLUtil.extractStringFromDocument(document.getOwnerDocument())) );
    }
    
    /**
	*	Log ERROR message
	*	@param String message
	*	@param Element element
	*/
    public void error(String code, Throwable t) {
    	if (isErrorEnabled())
    		logger.error(ResourceUtil.resolveMsgCode(code), t);
    }
    
    public void error(String code, Object[] args) {
    	if (isErrorEnabled())
    		logger.error(ResourceUtil.resolveMsgCode(code, args));
    }
    
    public void error(String code, Object[] args, Throwable t) {
    	if (isErrorEnabled())
	    	logger.error(ResourceUtil.resolveMsgCode(code, args), t);
    }
    
    /**
	*	Log INFO message
	* 	Raj Thota: Cautions using this method. Please try to use info( String  msg, Document document) instead for performance reasons.
	*/
    public void info( Object msg )
    {
            logger.log( YFCLogLevel.INFO, msg );
    }
    
    /**
	*	Log INFO  message
	*	@param String message
	*	@param Document document
	*/
    public void info( String  msg, Document document) throws Exception {
        	logger.log( YFCLogLevel.INFO, (msg + XMLUtil.extractStringFromDocument(document)) );
    }
    
    /**
	*	Log INFO  message
	*	@param String message
	*	@param Element document
	*/
    public void info( String  msg, Element document) throws Exception {
        if( logger.isEnabledFor(YFCLogLevel.INFO) ){
        	logger.log( YFCLogLevel.INFO, (msg + XMLUtil.extractStringFromDocument(document.getOwnerDocument())) );
        }
    }
    
    
    /**
	*	Get default package level Logger.
	*	@return the default Logger
	*/
	public static Logger getLogger()
	{
		return new Logger();
	}

	/**
	*	Get Logger by name.
	*	@return the Logger with the name.
	*/
	public static Logger getLogger(String name)
	{
		return new Logger(name);
	}

	
    /*  These methods will be used as  code guards that 
	 	will prevent unnecessary logging esp wil save the 
	 	argument generation 
	*/
	public boolean isVerboseEnabled(){
		return logger.isEnabledFor(YFCLogLevel.VERBOSE);
	}
	
	public boolean isDebugEnabled(){
		return logger.isEnabledFor(YFCLogLevel.DEBUG);
	}
	
	public boolean isErrorEnabled(){
		return logger.isEnabledFor(YFCLogLevel.ERROR);
	}
	
	public boolean isInfoEnabled(){
		return logger.isEnabledFor(YFCLogLevel.INFO);
	}
}


