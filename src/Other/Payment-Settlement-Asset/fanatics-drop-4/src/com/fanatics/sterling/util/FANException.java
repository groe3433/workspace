package com.fanatics.sterling.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;


/**
* This class wraps the YFS exception into FAN exception 
* 					required for the FAN Etaildirect Implementation. 
* 
* @(#) FANException.java
* Created on         July 18, 2007
*                    11:30:22 AM
*
* Package Declaration:
* File Name:                  FANException.java
* Package Name:               com.fanatics.sterling.util
* Project name:               Fanatics
* Type Declaration:
* Class Name:                 FANException
* Type Comment:
* 	
*
* @author 
* @version        1.0
* @history        
* 
*
*
* (C) Copyright 2006-2007 by owner.
* All Rights Reserved.
*
* This software is the confidential and proprietary information
* of the owner. ("Confidential Information").
* Redistribution of the source code or binary form is not permitted
* without prior authorization from the owner.
*
*/
public class FANException extends YFSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8817622200536829578L;

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
    
    private String errorCode = "";
    private Hashtable errorAttributes = null;
    private String errorDescription = "";
    private Exception nestedException = null;

    /**
     * Defines a default FANException object
     *
     */
    public FANException(){
    	this(null, null, null);
    }
    
    /**
     * Defines a FANException object with the specified error code
     * @param errorCode
     */
	public FANException(String errorCode) {
        this(errorCode, null, null);
    }
    
	/**
	 * Defines a FANException object with the specified nested exception
	 * @param nestedException
	 */
    public FANException(Exception nestedException) {
        this(null, null, nestedException);
    }
    
    /**
     * Defines a FANException object with the specified error code and nested exception
     * @param errorCode
     * @param nestedException
     */
    public FANException(String errorCode, Exception nestedException) {
        this(errorCode, null, nestedException);
    }

    /**
     * Defines a FANException object with the specified error code and error attributes.
     * @param errorCode
     * @param errorAttributes
     */
    public FANException(String errorCode, Hashtable errorAttributes) {
    	this(errorCode, errorAttributes, null);
    }
    
    /**
     * Defines a FANException object with the specified error code, error attributes and nested exception
     * @param errorCode
     * @param errorAttributes
     * @param nestedException
     */
    public FANException(String errorCode, Hashtable errorAttributes, Exception nestedException) {
    	//super(nestedException);
    	setNestedException(nestedException);
    	setErrorAttributes(errorAttributes);
    	setErrorCode(errorCode);
    	setErrorDescription(ResourceUtil.resolveMsgCode(errorCode));
    	
    }
    

    /**
     * Defines a FANException object with the specified error code, error attributes and nested exception
     * @param errorCode
     * @param errorAttributes
     * @param nestedException
     */
    public FANException(String errorCode,String[] errorArgs,Hashtable errorAttributes, Exception nestedException) {
    	setNestedException(nestedException);
    	setErrorAttributes(errorAttributes);
        setErrorCode(errorCode);
        setErrorDescription(ResourceUtil.resolveMsgCode(errorCode,errorArgs));
        
        
    }

    
    /**
     * Defines a FANException object with the specified error message, error code and error desc
     * @param errorMessage
     * @param errorCode
     * @param errorDesc
    public FANException(String errorMessage, String errorCode, String errorDesc) {
        super(errorMessage, errorCode, errorDesc);
    }
     */

    public Exception getNestedException() {
		return nestedException;
	}

	public void setNestedException(Exception nestedException) {
		this.nestedException = nestedException;
		if(null!=nestedException)
			setStackTrace(nestedException.getStackTrace());
		
	}
	
	public Hashtable getErrorAttributes() {
		return errorAttributes;
	}

	public void setErrorAttributes(Hashtable errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
    
	/**
	 * Returns the stack trace of this exception
	 * @return stack-trace string
	 */
    public String getStackTraceString() {
        StringWriter s = new StringWriter();
        //System.err.println("Printing Stack");
        //printStackTrace();
        printStackTrace(new PrintWriter(s));
        //nestedException.printStackTrace(new PrintWriter(s));
        return s.toString();
    }
    
    /**
     * Returns an error-xml message of this exception
     * @return error-xml message
     */
    public String getMessage() {
        String message = null;
        Document errorsDoc = null;
        try {
            errorsDoc = XMLUtil.createDocument("Errors");
            getErrorXML(errorsDoc.getDocumentElement());
            message = serializeDoc(errorsDoc);
        } catch (Exception e) {
            logger.error("Unable to construct XML for custom exception:" + e.toString());
        }
        return message;
    }
    
    private Element getErrorXML(Element parentElement) {
        Element errorElement = null;
        try {
            errorElement = XMLUtil.appendChild(parentElement.getOwnerDocument(), parentElement, "Error", null);

            errorElement.setAttribute("ErrorCode", errorCode);
            errorElement.setAttribute("ErrorDescription", errorDescription);

            if (errorAttributes != null) {
                Enumeration keys = errorAttributes.keys();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    Element att = XMLUtil.appendChild(parentElement.getOwnerDocument(), errorElement, "Attribute", null);
                    att.setAttribute("Name", key);
                    att.setAttribute("Value", errorAttributes.get(key).toString());
                }
            }

            Element stack = XMLUtil.createTextElement(parentElement.getOwnerDocument(), "Stack", getStackTraceString());
            errorElement.appendChild(stack);

        } catch (Exception e) {
            logger.error("Unable to construct XML for custom exception: " + e.toString());
        }

        return errorElement;
    }
    
    /**
     * Returns a custom String representation of this Exception
     */
    public String toString() {
        return getClass().getName() + ": [" + errorCode + "] " + errorDescription;
    }
    
    /**
     * Custom serializer - TODO: Replace with utils serializer
     * @param doc
     * @return
     */
    public String serializeDoc(Document doc){
        TransformerFactory tranFactory = null;
        Transformer aTransformer = null;
        ByteArrayOutputStream os = null;
		try {
			tranFactory = TransformerFactory.newInstance(); 
			aTransformer = tranFactory.newTransformer();
			
	        os = new ByteArrayOutputStream();
	        Source src = new DOMSource(doc); 
	        Result dest = new StreamResult(os); 
	        aTransformer.transform(src, dest); 
	        
		} catch (TransformerConfigurationException e) {
			logger.error("Error while serializing: "+e);
		} catch (TransformerException e) {
			logger.error("Error while serializing: "+e);
		} 

       return new String(os.toByteArray());
    }

}
