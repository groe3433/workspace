/*
 * @(#)CustomException.java 1.0 29 Oct. 2002
 *
 * Copyright (c) Yantra Corp.
 * Yantra, 1 Park West, Suite B, Tweksbury, MA 01876 U.S.A.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */
package com.nwcg.icbs.yantra.exception.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSException;

/**
 * Base exception of the custom package. Can contain an error message, a message code as defined in ErrorCode class,
 * and nested exception.
 * The nested exception can also be a CustomException, so there can be multiple level of nesting.
 * If a nested exception is used for this class, the nested exception is referenced, not copied/cloned.
 * @version 1.0 29 Oct. 2002
 * @author Michael Chen
 *
 * Modified on 13/12/2004 by Sathya Sankar G
 */
public class CustomException extends YFSException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(CustomException.class.getName());

    //Exception related properties
    private String errorCode = "";

    /**
     * Use this errorArgs to parameterize the Error description
     * for eg To display error description "Order # YSO123 is in an invalid status (Cancelled)".
     * Use errorArgs in CustomException as described below.
     * Create an Object Array with the list of parameter values . These values replaces the place holders
     * in the description
     *     <br><br>
     * Object[] params = new Object[] {"YSO123", "Cancelled"};
     *     <br>
     * CustomException ce = new CustomException(("ER_123",params);
     *     <br><br>
     * and the Resource bundle has the following entry
     * ER_123=Order # {0} is in an invalid status ({1})
     * where {0} and {1} are the place holders that will be replaced by the corresponding values
     * in the errorArgs.
     *
     */
    private Object[] errorArgs = null;

    private Hashtable errorAttributes = null;
    private String errorDescription = "";
    private String errorRelatedInfo = "";
    private Exception nestedException = null;

    /**
     *	Default constructor
     */
    public CustomException() {
    }

    /**
     *	Constructor taking ErrorCode.
     */
    public CustomException(String errorCode) {
        this(errorCode, null, null, null);
    }

    /**
     * Constructor taking ErrorCode & Error Args.
     * Use this errorArgs to parameterize the Error description
     * for eg To display error description "Order # YSO123 is in an invalid status (Cancelled)".
     * Use errorArgs in CustomException as described below.
     * Create an Object Array with the list of parameter values . These values replaces the place holders
     * in the description
     * <br><br>
     * Object[] params = new Object[] {"YSO123", "Cancelled"};
     * <br>
     * CustomException ce = new CustomException(("ER_123",params);
     * <br><br>
     * and the Resource bundle has the following entry
     * ER_123=Order # {0} is in an invalid status ({1})
     * where {0} and {1} are the place holders that will be replaced by the corresponding values
     * in the errorArgs.
     */
    public CustomException(String errorCode, Object[] errorArgs) {
        this(errorCode, errorArgs, null, null);
    }

    /**
     *	Constructor taking a Nested exception.
     */
    public CustomException(Exception nestedException) {
        this(null, (Object []) null, nestedException);
    }

    /**
     *	Constructor taking ErrorCode and a Nested exception.
     */
    public CustomException(String errorCode, Exception nestedException) {
        this(errorCode, null, null, nestedException);
    }

    /**
     *	Constructor taking ErrorCode, Arguments and a Nested exception.
     */
    public CustomException(String errorCode, Object[] errorArgs, Exception nestedException) {
        this(errorCode, errorArgs, null, nestedException);
    }

    /**
     *	Constructor taking ErrorCode, ErrorRelatedMoreInfo and a Nested exception.
     */
    public CustomException(String errorCode, Object[] errorArgs, String errorRelatedInfo,
            Exception nestedException) {
        setErrorDetails(errorCode, errorArgs);
        setErrorRelatedInfo(errorRelatedInfo);
        setNestedException(nestedException);
    }

    /**
     *	Constructor taking ErrorCode, ErrorRelatedMoreInfo and a Nested exception.
     */
    public CustomException(String errorCode, Object[] errorArgs, Hashtable errorAttributes,
            String errorRelatedInfo, Exception nestedException) {
        setErrorDetails(errorCode, errorArgs);
        setErrorAttributes(errorAttributes);
        setErrorRelatedInfo(errorRelatedInfo);
        setNestedException(nestedException);
    }

    /**
     *	Static method to return an Exception's stack trace as String
     *	@param t the exception for which the stack trace to be generated.
     *	@return a String of the stack trace.
     */
    public static String generateStackTraceString(Throwable t) {
        StringWriter s = new StringWriter();
        t.printStackTrace(new PrintWriter(s));
        return s.toString();
    }

    /**
     *	Get stack trace of the exception as String. The output also includes the stack trace for the nested exception,
     *	if exists.
     */
    public String getStackTraceString() {
        StringBuffer buf = new StringBuffer(generateStackTraceString(this));
        /*if (nestedException != null) {
            buf.append("\n------Nested by------\n");
            if (nestedException instanceof CustomException)
                buf.append(((CustomException)nestedException).getStackTraceString());
            else
                buf.append(generateStackTraceString(nestedException));
            buf.append("\n------End nested------\n");
        }*/

        return buf.toString();
    }

    /**
     *	Get error message.
     *	@return error message in the format of "[errorCode] errorCodeDescription --- errorMessage".
     */
    public String getMessage() {
        /*
            <?xml version="1.0" encoding="UTF-8" ?>
            <Errors>
                <!-- One or more Error Elements -->
                <Error ErrorCode="" ErrorDescription="" ErrorRelatedMoreInfo="">

                    <!-- One or More Error attribute elements -->
                    <Attribute Name="" Value="" />
                    <!-- Nested Errors -->
                    <Error ErrorCode="" ErrorDescription="" ErrorRelatedMoreInfo="">
                        <Attribute Name="" Value="" />
                        <Stack/>
                    </Error>
                    <!-- The stack trace as a text node -->
                    <Stack />
                </Error>
            </Errors>
         */
        String message = null;
        Document errorsDoc = null;
        try {
            errorsDoc = XMLUtil.createDocument("Errors");
            getErrorXML(errorsDoc.getDocumentElement());
            message = XMLUtil.serialize(errorsDoc);
        } catch (Exception e) {
            message = "Unable to construct XML for custom exception:" + e.toString();
            e.printStackTrace();
            logger.verbose(message);
        }

        return message;
    }

    /**
     *
     * @param parentElement
     * @return
     */
    public Element getErrorXML(Element parentElement) {
        Element errorElement = null;
        try {
            errorElement = XMLUtil.appendChild(parentElement.getOwnerDocument(), parentElement, "Error", null);

            errorElement.setAttribute("ErrorCode", errorCode);
            errorElement.setAttribute("ErrorDescription", errorDescription);
            errorElement.setAttribute("ErrorRelatedMoreInfo", errorRelatedInfo);

            if (errorAttributes != null) {
                Enumeration keys = errorAttributes.keys();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    Element att = XMLUtil.appendChild(parentElement.getOwnerDocument(), errorElement, "Attribute", null);
                    att.setAttribute("Name", key);
                    att.setAttribute("Value", errorAttributes.get(key).toString());
                }
            }

            if (nestedException != null) {
                if (nestedException instanceof CustomException)
                    ((CustomException) nestedException).getErrorXML(errorElement);
            }

            Element stack = XMLUtil.createTextElement(parentElement.getOwnerDocument(), "Stack", getStackTraceString());
            errorElement.appendChild(stack);

        } catch (Exception e) {
            e.printStackTrace();
            logger.verbose("Unable to construct XML for custom exception: " + e.toString());
        }

        return errorElement;
    }

    /**
     *	Convert to String.
     *	@return getMessage() for itself and nested exception.
     */
    public String toString() {
        return getClass().getName() + ": [" + errorCode + "] " + errorDescription;
    }

    /**
     *	Get error code of the CustomException.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Getter for property errorArgs.
     * @return Value of property errorArgs.
     */
    public Object[] getErrorArgs() {
        return this.errorArgs;
    }

    /**
     * Getter for property errorDescription.
     * @return Value of property errorDescription.
     */
    public String getErrorDescription() {
        return errorDescription;
    }

/**
 * 
 * @return
 */
    public Exception getNestedException() {
        return nestedException;
    }

    /**
     * Getter for property errorRelatedInfo.
     * @return Value of property errorRelatedInfo.
     */
    public String getErrorRelatedInfo() {
        return errorRelatedInfo;
    }

    /**
     * Getter for property errorAttributes.
     * @return Value of property errorAttributes.
     */
    public Hashtable getErrorAttributes() {
        return this.errorAttributes;
    }


    public void setErrorDetails(String errorCode, Object [] errorArgs) {
        this.errorCode = errorCode;
        this.errorArgs = errorArgs;
        this.errorDescription = ResourceUtil.resolveMsgCode(errorCode, errorArgs);
    }

    /**
     * Setter for property errorRelatedInfo.
     * @param errorRelatedInfo New value of property errorRelatedInfo.
     */
    public void setErrorRelatedInfo(String errorRelatedInfo) {
        this.errorRelatedInfo = errorRelatedInfo;
    }

    /**
     * Setter for property nestedException.
     * @param nestedException New value of property nestedException.
     */
    public void setNestedException(Exception nestedException) {
        this.nestedException = nestedException;
    }

    /**
     * Setter for property errorAttributes.
     * @param errorAttributes New value of property errorAttributes.
     */
    public void setErrorAttributes(Hashtable errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    public static void main(String[] args) {

        CustomException ce0 = new CustomException("ERR_0001", new Object[] {"DD", "WW"});
        CustomException ce = new CustomException("ERR_00011", new Object[] {"DD", "WW"}, ce0);

        Hashtable ht = new Hashtable();
        ht.put("Att1", "dd");
        ht.put("Att2", "111dd");
        ce.setErrorAttributes(ht);
        System.out.println("***ce.toString()***");
        System.out.println(ce);
        System.out.println("***ce.getMessage()***");
        System.out.println(ce.getMessage());

        System.out.println("***ce.getErrorXML(null)***");
        //System.out.println(XMLUtil.serialize(ce.getErrorXML()));

        Object[] testArgs = {"YSO123", "Cancelled"};
//        ce = new CustomException("ER_123",testArgs);
//        System.out.println(ce.getMessage());

        System.out.println(MessageFormat.format("Order # {0} is in an invalide status ({1})", testArgs));
        //System.out.println(MessageFormat.format("Order # {0} is in an invalide status ({1})", null));


    }

}
