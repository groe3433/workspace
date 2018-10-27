package com.fanatics.sterling.util;

/**
 * This class contains the utility methods required for the
 * 
 * @(#) CommonUtil.java Created on July 18, 2007 11:30:22 AM Package Declaration: File Name:
 *      CommonUtil.java Package Name: Project name: Type Declaration: Class Name:
 *      CommonUtil Type Comment: (C) Copyright 2006-2007 by owner. All Rights Reserved. This
 *      software is the confidential and proprietary information of the owner.
 *      ("Confidential Information"). Redistribution of the source code or binary form is not
 *      permitted without prior authorization from the owner.
 */
//Java Imports
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Encapsulates set of utility methods
 */
public final class CommonUtil {

	/**
	 * LoggerUtil Instance.
	 */
	private static final YFCLogCategory logger = YFCLogCategory
			.instance(CommonUtil.class);
	// Utility Class - Mask Constructor
	private CommonUtil() {
	}

	/**
	 * Instance of YIFApi used to invoke Sterling Commerce APIs or services.
	 */
	private static YIFApi api;

	static {
		try {
			CommonUtil.api = YIFClientFactory.getInstance().getApi();
		} catch (Exception e) {
			CommonUtil.logger.error("IOM_UTIL_0001", e);
		}
	}

	/**
	 * Stores the object in the environment under a certain key.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @param value
	 *            Object to be stored in the environment under the given key.
	 * @return Previous object stored in the environment with the same key (if present).
	 */
	public static Object setContextObject(YFSEnvironment env, String key,
			Object value) {
		Object oldValue = null;
		Map map = env.getTxnObjectMap();
		if (map != null) {
			oldValue = map.get(key);
		}
		env.setTxnObject(key, value);
		return oldValue;
	}

	/**
	 * Retrieves the object stored in the environment under a certain key.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return Object retrieved from the environment under the given key.
	 */
	public static Object getContextObject(YFSEnvironment env, String key) {
		return env.getTxnObject(key);
	}

	/**
	 * Retrieves the property stored in the environment under a certain key.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return Poperty retrieved from the environment under the given key.
	 */
	public static String getContextProperty(YFSEnvironment env, String key) {
		String value = null;
		Object obj = env.getTxnObject(key);
		if (obj != null) {
			value = obj.toString();
		}
		return value;
	}

	/**
	 * Removes an object from the environment.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return The object stored in the environment under the specified key (if any).
	 */
	public static Object removeContextObject(YFSEnvironment env, String key) {
		Object oldValue = null;
		Map map = env.getTxnObjectMap();
		if (map != null) {
			oldValue = map.remove(key);
		}
		return oldValue;
	}

	/**
	 * Clears the environment of any user objects stored.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 */
	public static void clearContextObjects(YFSEnvironment env) {
		Map map = env.getTxnObjectMap();
		if (map != null) {
			map.clear();
		}
	}

	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param templateName
	 *            Name of API Output Template that needs to be set
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String templateName,
			String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, templateName);
		Document returnDoc = CommonUtil.api.invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName); 
		return returnDoc;
	}

	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param template
	 *            Output template document for the API
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, Document template,
			String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, template);
		Document returnDoc = CommonUtil.api.invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}

	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			Document inDoc) throws Exception {
		return CommonUtil.api.invoke(env, apiName, inDoc);
	}

	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDocStr
	 *            Input to be passed to the API. Should be a valid XML string.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			String inDocStr) throws Exception {
		return CommonUtil.api.invoke(env, apiName, YFCDocument.parse(inDocStr)
				.getDocument());
	}

	/**
	 * Invokes a Sterling Commerce Service.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the Service.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env,
			String serviceName, Document inDoc) throws Exception {
		return CommonUtil.api.executeFlow(env, serviceName, inDoc);
	}

	/**
	 * Invokes a Sterling Commerce Service.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param inDocStr
	 *            Input to be passed to the Service. Should be a valid XML String.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env,
			String serviceName, String inDocStr) throws Exception {
		return CommonUtil.api.executeFlow(env, serviceName, YFCDocument.parse(
				inDocStr).getDocument());
	}

	/**
	 * Returns the clone of an XML Document.
	 * 
	 * @param doc
	 *            Input document to be cloned.
	 * @throws java.lang.Exception
	 *             If unable to clone document.
	 * @return Clone of the document.
	 */
	public static Document cloneDocument(Document doc) throws Exception {
		return YFCDocument.parse(XMLUtil.getXMLString(doc)).getDocument();
	}

	/**
	 * Returns the clone of an XML Document.
	 * 
	 * @param doc
	 *            Input document to be cloned.
	 * @throws java.lang.Exception
	 *             If unable to clone document.
	 * @return Clone of the document.
	 */
	public static YFCDocument cloneDocument(YFCDocument doc) throws Exception {
		return YFCDocument.parse(doc.getString());
	}

	/**
	 * Method to get resource as InputStream
	 * 
	 * @param resource
	 *            Resource path relative to classpath
	 * @return Resource as InputStream
	 */
	public static InputStream getResourceStream(String resource) {
		return CommonUtil.class.getResourceAsStream(resource);
	}

	/**
	 * Method to create YFS environment with a userid and progrid.
	 * 
	 * @param userID
	 *            UserId used in the context of this environment
	 * @param progID
	 *            ProgId used in the context of this environment
	 * @return YFSEnvironment object containing state information about the underlying
	 *         implementation of YIFApi.
	 * @throws java.lang.Exception
	 *             Exception thrown if unable to create Environment object
	 */
	public static YFSEnvironment createEnvironment(String userID, String progID)
			throws Exception {
		Document doc = XMLUtil.createDocument("YFSEnvironment");
		Element elem = doc.getDocumentElement();
		elem.setAttribute("userId", userID);
		elem.setAttribute("progId", progID);

		return CommonUtil.api.createEnvironment(doc);
	}

	/**
	 * Method to log the properties
	 * 
	 * @param properties
	 *            Properties to be logged
	 * @param log
	 *            LoggerUtil to be used for logging
	 */
	public static void logProperties(Map properties, YFCLogCategory log) {
		Iterator keyIt = properties.keySet().iterator();
		log.verbose("******[Properties List Start]************");
		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			String val = (String) properties.get(key);
			log.verbose("<" + key + ":" + val + ">");

		}
		log.verbose("******[Properties List End]*************");
	}

	/**
	 * Removes the passed Node name from the input document. If no name is passed, it removes all
	 * the nodes.
	 * 
	 * @param node
	 *            Node from which we have to remove the child nodes
	 * @param nodeType
	 *            nodeType e.g. Element Node, Comment Node or Text Node
	 * @param name
	 *            Name of the Child node to be removed
	 */
	public static void removeAll(Node node, short nodeType, String name) {
		if (node.getNodeType() == nodeType
				&& (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);
		} else {
			// Visit the children
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				CommonUtil.removeAll(list.item(i), nodeType, name);
			}
		}

	}

	/**
	 * @return True if 'isnamespaceaware' is activated
	 */
	public static boolean isDocumentNamespaceAware() {
		return "Y".equals(ResourceUtil.get("yantra.document.isnamespaceaware"));
	}

	/**
	 * @param dt
	 *            Input Date object
	 * @return YFCDate in yyyyMMdd'T'HH:mm:ss format.
	 */
	public static String convertToYantraDate(Date dt) {
		YFCDate yDate = new YFCDate(dt);
		return yDate.getString();
	}

	/**
	 * @param nList
	 *            org.w3c.dom.NodeList
	 * @return ArrayList of org.w3c.dom.Node objects
	 */
	public static List getListFromNodeList(NodeList nList) {
		if (null == nList) {
			return null;
		}
		int nodeLength = nList.getLength();
		ArrayList<Node> list = new ArrayList<Node>(nodeLength);
		for (int i = 0; i < nodeLength; i++) {
			list.add(i, nList.item(i));
		}
		return list;
	}

	/**
	 * Description of getYFCDate
	 *  returns the YFCDate for a date string of the format yyyy-MM-dd
	 * @param date
	 *      String date in format yyyy-MM-dd
	 * @return
	 * YFCDate
	 **/
	public static YFCDate getYFCDate(String date) {
		String strReqStartDate = date;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dtReqStartDate = null;
		try {
			dtReqStartDate = sdf.parse(strReqStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String strYFCReqStartDate = sdf1.format(dtReqStartDate);
		Date yFCFormatReqStartDate = null;
		try {
			yFCFormatReqStartDate = sdf1.parse(strYFCReqStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		YFCDate yfcReqStartDate = new YFCDate(yFCFormatReqStartDate);
		return yfcReqStartDate;
	}


	/**
	 * This method is used to converting YFCElement to Element
	 * @param yeleInput
	 * @return
	 * @throws ParserConfigurationException
	 */

	public static Element getElementFromYFCElement(YFCElement yeleInput)
			throws ParserConfigurationException {
		Element eleInput = null;
		if (!YFCObject.isVoid(yeleInput)) {
			eleInput = (Element) yeleInput.getDOMNode();
		}
		return eleInput;
	}

	/**
	 * This method retrieves the XML stored in the Environment variable using the 'Identifier' parameter passed as an argument
	 * @param env
	 * @param strIdentifier
	 * @return outXML
	 * @throws Exception
	 */
	public static Document getXML(YFSEnvironment env, String strIdentifier)
			throws Exception {

		Document outXML = null;

		if (null != strIdentifier) {
			strIdentifier = strIdentifier.trim();
		}
		if (strIdentifier.isEmpty()) {
			throw new Exception();
		}
		outXML = (Document) env.getTxnObject(strIdentifier);

		//clear the identifier xml
		clearXML(env, strIdentifier);

		return outXML;
	}

	/**
	 * This method sets a document from the input XML in the Environment variable using the 'Identifier'
	 * parameter passed as an argument
	 * @param env
	 * @param inXML
	 * @param strIdentifier
	 * @return inXML
	 * @throws Exception
	 */
	public static Document setXML(YFSEnvironment env, Document inXML,
			String strIdentifier) throws Exception {

		//clear the identifier xml
		clearXML(env, strIdentifier);
		Document docToBeStored = null;

		if (null != strIdentifier) {
			strIdentifier = strIdentifier.trim();
		}
		if (strIdentifier.isEmpty()) {
			throw new Exception();
		}

		docToBeStored = inXML;

		env.setTxnObject(strIdentifier, docToBeStored);

		return inXML;
	}

	/**
	 * This method clears the xml in the identifier passed as argument in the transaction
	 * @param env
	 * @param strIdentifier
	 */
	public static void clearXML(YFSEnvironment env, String strIdentifier) {
		env.setTxnObject(strIdentifier, null);
	}

	/**
	 * The following mapping should be achieved.
	 * 	
	 * @param strCurrentStatus
	 * @return
	 */

	/**
	 * Converts a W3C document object into an XML string.
	 * 
	 * @param node
	 *            the input W3C node object
	 * @param indent
	 *            when true, format the XML with indents; otherwise, formats as
	 *            continuous string
	 * @return the incoming XML document as as string; null if the incoming
	 *         document is null, or contains no document element.
	 */
	public final static String toXml(final Node node, boolean indent) {
		//
		XMLSerializer serializer = null;
		OutputFormat format = null;
		StringWriter out = null;
		try {
			if (node == null) {
				return null;
			}// end: if (inXml == null)

			format = new OutputFormat("XML", "UTF-8", indent);
			out = new StringWriter();
			serializer = new XMLSerializer(out, format);

			// Test node type
			switch (node.getNodeType()) {
			case Node.DOCUMENT_NODE: {
				serializer.serialize((Document) node);
				break;
			}// end: case Node.DOCUMENT_NODE:

			case Node.DOCUMENT_FRAGMENT_NODE: {
				serializer.serialize((DocumentFragment) node);
				break;
			}// end: case Node.DOCUMENT_FRAGMENT_NODE:

			case Node.ELEMENT_NODE: {
				serializer.serialize((Element) node);
				break;
			}// end: case Node.ELEMENT_NODE:

			default: {
				final String message = "Cannot serialize node of type ["
						+ node.getClass().getName() + "].";
				throw new IOException(message);
			}// end: default:
			}// end: switch (node.getNodeType())

			return out.toString();
		} catch (IOException ex) {
			return "<Error><![CDATA[" + ex.getMessage() + "]]></Error>";
		} finally {
		}
	}// toXml(Node, boolean):String

	/**
	 * Converts a W3C document object into an XML string, indenting the output
	 * 
	 * @param node
	 *            the document to convert to XML
	 * @return the formatted document as as string
	 */
	public final static String toXml(final Node node) {
		//
		return toXml(node, true);
		//
	}// toXml(Node):String

	/**
	 * Merges the docToBeMerged with the docParent
	 * 
	 * @param docParent
	 * @param docToBeMerged
	 * @return doc
	 * 
	 */
	public static Document getMergedDocument(Document docParent,
			Document docToBeMerged) {
		YFCDocument ydocParent = YFCDocument.getDocumentFor(docParent);
		YFCElement yeleParentDetails = ydocParent.getDocumentElement();
		YFCDocument ydocToBeMergedDetails = YFCDocument
				.getDocumentFor(docToBeMerged);
		YFCElement yeleToBeMergedDetails = ydocToBeMergedDetails
				.getDocumentElement();
		yeleParentDetails.importNode(yeleToBeMergedDetails);
		return ydocParent.getDocument();
	}

	/**
	 * getting the getOrderInvoiceDetails output xml
	 * 
	 * @param env
	 * @param strInvoiceNo
	 * @return docGetOrderInvoiceDetailsOut
	 * @throws Exception
	 * 
	 * Xml Template for getOrderInvoiceDetails api
	 *  <InvoiceDetail>
	 <InvoiceHeader>
	 <CollectionDetails>
	 <CollectionDetail>
	 <PaymentMethod PaymentType="" CreditCardNo=""
	 CreditCardType="" TotalCharged="" SvcNo="">
	 </PaymentMethod>
	 </CollectionDetail>
	 </CollectionDetails>
	 </InvoiceHeader>
	 </InvoiceDetail>
	 * 
	 */
	 
	/**
	 * getting the ExtnInvoice,Invoice and creditmemo Invoice  number from the getOrderInvoiceList
	 * 
	 * 
	 * @param docIn
	 * @return strInvoiceNo xml template for getOrderInvoiceList api
	 *         <OrderInvoiceList> <OrderInvoice InvoiceNo="" InvoiceType="">
	 *         <Extn/> <LineDetails> <LineDetail> <OrderLine> <Extn
	 *         ExtnReleaseNo=""/> </OrderLine> </LineDetail> </LineDetails>
	 *         </OrderInvoice> </OrderInvoiceList>
	 */
	//changed the name to change the return type to Map
	 
	
	/**
	 * getting the ExtnInvoice,Invoice and creditmemo Invoice  number from the getOrderInvoiceList
	 * 
	 * This is used to phase out ExtnReleaseNo
	 * 
	 * @param docIn
	 * @return strInvoiceNo xml template for getOrderInvoiceList api
	 *         <OrderInvoiceList> <OrderInvoice InvoiceNo="" InvoiceType="">
	 *         <Extn/> <LineDetails> <LineDetail> <OrderLine OrderLineKey=""> <Extn
	 *         ExtnReleaseNo=""/> </OrderLine> </LineDetail> </LineDetails>
	 *         </OrderInvoice> </OrderInvoiceList>
	 */
	//changed the name to change the return type to Map
	 /**
	 * This method converts Date format from "yyyy-MM-dd'T'HH:mm:ss" to 
	 * "MM/dd/yyyy" format.
	 * @param strOrderDate
	 * @return OrderDate formatted OrderDate
	 */
	public static String getFormattedDate(String strOrderDate){
		String strDate = null;
		try{
			if(!YFCObject.isVoid(strOrderDate)){
				SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
				strDate = myFormat.format(fromUser.parse(strOrderDate));
				}
		}
		
		catch (ParseException e) {
			e.printStackTrace();
		}
		return strDate;
	}
/**
	 * This method adds the lead days to the input date and returns it in Timestamp format 
	 * @param strOrderDate
	 * @param strLeadDays
	 * @return OrderDate formatted OrderDate
	 */
	public static String getUpdatedDate(String strOrderDate, String strLeadDays){
		
		String strDate = null;
		Integer intLeadDays = new Integer(strLeadDays);
		try{
			if((!YFCObject.isVoid(strOrderDate))&& !YFCObject.isVoid(strLeadDays)){
				SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat calendarFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
				SimpleDateFormat productFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				
				
				java.util.Date temp = fromUser.parse(strOrderDate);
				
				Calendar calUpdatedDate = Calendar.getInstance();
				calUpdatedDate.setTime(temp);
				calUpdatedDate.add(Calendar.DATE, intLeadDays.intValue());	
				
				java.util.Date temp1 = calendarFormat.parse(calUpdatedDate.getTime().toString());
				
				
				strDate = productFormat.format(temp1).toString();

				}
		}
		
		catch (ParseException e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * getting the Current Year
	 * @return strYear
	 */
	public static String  getYear()
	{
		Calendar cal=Calendar.getInstance();
	    int intyear=cal.get(Calendar.YEAR);
	    String strYear = Integer.toString(intyear);
	    return strYear;
	}

	/**
	 * Returns current date-time string in desired format
	 *
	 * @param outputFormat Desired output date-time format
	 * @return          Current date-time string in desired format
	 * @throws IllegalArgumentException for Invalid input
	 * @throws Exception for all others
	 *//*
	public static String getCurrentTime(String outputFormat)
			throws IllegalArgumentException, Exception {
		//Create current date object
		Date currentDateTime = new Date();

		//Apply formatting
		return formatDate(currentDateTime, outputFormat);
	}

	*//**
	 * Converts date object to date-time string
	 * @param inputDate Date object to be converted
	 * @param outputFormat Output format.
	 * Refer to <code>java.text.SimpleDateFormat</code> for date format
	 * codes
	 * @return          Formatted date-time string
	 * @throws IllegalArgumentException for Invalid input
	 * @throws Exception for all others
	 *//*
	public static String formatDate(java.util.Date inputDate,
			String outputFormat) throws IllegalArgumentException, Exception {
		//Validate input date value
		if (inputDate == null) {
			throw new IllegalArgumentException("Input date cannot "
					+ " be null in DateUtils.formatDate method");
		}

		//Validate output date format
		if (outputFormat == null) {
			throw new IllegalArgumentException("Output format cannot"
					+ " be null in DateUtils.formatDate method");
		}

		//Apply formatting
		SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
		return formatter.format(inputDate);
	}
	


	*//**
	 * Method getFutureDate gives the future date in 
	 * yyyyMMddHHmmss format based on the number of days 
	 * passed. It adds the  number of days passed 
	 *  to the current date 
	 *  @param dateFormat
	 * @param numberOfDays
	 * @return authExpirationDate
	 *//*
	public static String getFutureDate(int numberOfDays , String format){
		
		DateFormat dateFormat = new SimpleDateFormat(format);		  
		Calendar cal = Calendar.getInstance();  
		cal.setTime(new Date());  
		cal.add(Calendar.DATE, numberOfDays);  		  
		String authExpirationDate = dateFormat.format(cal.getTime());
		
		return authExpirationDate;
	}
	
	public static String getCommonCodeValue(YFSEnvironment env, String commonCodeType, String commonCodeShortDesc) throws DSWException {
		logger.verbose("getCommonCodeValue - Invoked");
		String codeValue = "";
		try {			
			Document commonCodeInput = XMLUtil.createDocument("CommonCode");
			Element rootElement = commonCodeInput.getDocumentElement();
			rootElement.setAttribute("CodeType", commonCodeType);
			rootElement.setAttribute("CodeShortDescription", commonCodeShortDesc);
			
			Document commonCodeTemplate = XMLUtil.createDocument("CommonCodeList");
			Element rootTemElement = commonCodeTemplate.getDocumentElement();
			Element commonCodeNode = commonCodeTemplate.createElement("CommonCode");
			rootTemElement.appendChild(commonCodeNode);
			
			Document commonCodeOutput = CommonUtilities.invokeLocalAPI(env, "getCommonCodeList", commonCodeInput, commonCodeTemplate);
			NodeList commonCodeNodeList = commonCodeOutput.getElementsByTagName("CommonCode");
			int commonNodeNodeListCount = commonCodeNodeList.getLength();
			for (int i = 0; i < commonNodeNodeListCount; i++) {				
				commonCodeNode = 
					(Element) commonCodeNodeList.item(i);
				String codeShortDescValue = commonCodeNode.getAttribute("CodeShortDescription");				
				if (codeShortDescValue.equalsIgnoreCase(commonCodeShortDesc)) {
					codeValue = commonCodeNode.getAttribute("CodeValue");
				}
			}
		}
		catch(Exception exception) {
			throw new DSWException("Unexpected Exception processing getCommonCodeValue", exception);
			}
		logger.verbose("codeValue = " + codeValue);
		logger.verbose("getCommonCodeValue - Exited");
	return codeValue;
	}
	
	*//**
	    * Returns current date-time string in desired format
	    *
	    * @param outputFormat Desired output date-time format
	    * @return          Current date-time string in desired format
	    * @throws IllegalArgumentException for Invalid input
	    * @throws Exception for all others
	    *//*
	    public static String getCurrentTime(String outputFormat)
	        throws IllegalArgumentException, Exception
	    {
	        //Create current date object
	        Date currentDateTime = new Date();

	        //Apply formatting
	        return formatDate(currentDateTime, outputFormat);
	    }
		    
	    *//**
	     * Converts date object to date-time string
	     * @param inputDate Date object to be converted
	     * @param outputFormat Output format.
	     * Refer to <code>java.text.SimpleDateFormat</code> for date format
	     * codes
	     * @return          Formatted date-time string
	     * @throws IllegalArgumentException for Invalid input
	     * @throws Exception for all others
	     *//*
	     public static String formatDate(
	         java.util.Date inputDate,
	         String outputFormat)
	         throws IllegalArgumentException, Exception
	     {
	         //Validate input date value
	         if (inputDate == null)
	         {
	             throw new IllegalArgumentException("Input date cannot "
	                     + " be null in DateUtils.formatDate method");
	         }

	         //Validate output date format
	         if (outputFormat == null)
	         {
	             throw new IllegalArgumentException("Output format cannot"
	                     + " be null in DateUtils.formatDate method");
	         }

	         //Apply formatting
	         SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
	         return formatter.format(inputDate);
	     }
	     *//**
	      * Converts date object to date-time string in
	      * default date format
	      *
	      * @param inputDate Date object to be converted
	      * @return          Date-time string in default date format
	      * @throws IllegalArgumentException for Invalid input
	      * @throws Exception for all others
	      * @see getDefaultDateFormat
	      *//*
	      public static String convertDate(java.util.Date inputDate)
	          throws IllegalArgumentException, Exception
	      {
	          return formatDate(inputDate, getDefaultDateFormat());
	      }
	      *//**
	       * Returns default date-time string format i.e.
	       * <code>yyyyMMdd'T'HH:mm:ss</code>
	       * @return Default date-time string i.e. yyyyMMdd'T'HH:mm:ss
	       *//*
	       protected static String getDefaultDateFormat()
	       {
	           //Yantra default date-time string format
	           return "yyyyMMdd'T'HH:mm:ss";
	       }
	       *//**
	        * Converts date-time string to Date object.
	        * Date-time string should be in default date format
	        * @author sahmed
	        * @param inputDate Date-time string to be converted
	        * @return          Equivalent date object to input string
	        * @throws IllegalArgumentException for Invalid input
	        * @throws Exception for all others
	        *//*
	        public static java.util.Date convertDate(String inputDate)
	            throws IllegalArgumentException, Exception
	        {

	            //Validate input date value
	            if (inputDate == null)
	            {
	                throw new IllegalArgumentException("Input date cannot "
	                        + " be null in DateUtils.convertDate method");
	            }
	            if (inputDate.indexOf("T") != -1 && inputDate.indexOf("-") == -1)
	            {
	                return convertDate(inputDate, getDefaultDateFormat());
	            }
	            else if(inputDate.indexOf("T") != -1 && inputDate.indexOf("-") != -1)
	            {

	    			return convertDate(inputDate, getDefaultDateFormatISO());
	            }
	            else
	            {

	                return convertDate(inputDate, getShortDefaultDateFormat());
	            }
	        }
	        *//**
	         * Converts date-time string to Date object
	         *
	         * @param inputDate Date-time string to be converted
	         * @param inputDateFormat Format of date-time string.
	         * Refer to <code>java.util.SimpleDateFormat</code> for date
	         * format codes
	         * @return          Equivalent date object to input string
	         * @throws IllegalArgumentException for Invalid input
	         * @throws Exception for all others
	         *//*
	         public static java.util.Date convertDate(
	             String inputDate,
	             String inputDateFormat)
	             throws IllegalArgumentException, Exception
	         {
	             //Validate Input Date value
	             if (inputDate == null)
	             {
	                 throw new IllegalArgumentException(
	                     "Input date cannot be null"
	                         + " in DateUtils.convertDate method");
	             }

	             //Validate Input Date format
	             if (inputDateFormat == null)
	             {
	                 throw new IllegalArgumentException(
	                     "Input date format cannot"
	                         + " be null in DateUtils.convertDate method");
	             }

	     		//Apply formatting
	             SimpleDateFormat formatter =
	                 new SimpleDateFormat(inputDateFormat);

	             ParsePosition position = new ParsePosition(0);
	             return formatter.parse(inputDate, position);
	         }
	         
	         *//**
	     	* Returns default date-time string format i.e.
	     	* <code>yyyyMMdd'T'HH:mm:ss</code>
	     	* @return Default date-time string i.e. yyyyMMdd'T'HH:mm:ss
	     	*//*
	     	protected static String getDefaultDateFormatISO()
	     	{
	     		//Yantra default date-time string format
	     		return "yyyy-MM-dd'T'HH:mm:ss";
	     	}
	     	*//**
	         * Returns short default date string format i.e.
	         * <code>yyyyMMdd</code>
	         *//*
	         protected static String getShortDefaultDateFormat()
	         {
	             //Yantra short default date string format
	             return YYYYMMDD;
	         }
	         
	     *//**
	      * Returns time difference between two date objects
	      *
	      * @param startTime Start time
	      * @param endTime End time.
	      * End time should be greater than Start time.
	      * @return        Time difference in HH:mm:ss.SSS format
	      * @throws IllegalArgumentException for Invalid input
	      * @throws Exception for all others
	      *//*
	      public static String getTimeDifference(
	          Date startTime,
	          Date endTime)
	          throws IllegalArgumentException, Exception
	      {
	          //Validate Start time
	          if (startTime == null)
	          {
	              throw new IllegalArgumentException(
	                  "Start time cannot be"
	                      + " null in DateUtils.getTimeDifference method");
	          }

	          //Validate End time
	          if (endTime == null)
	          {
	              throw new IllegalArgumentException(
	                  "End time cannot be "
	                      + "null in DateUtils.getTimeDifference method");
	          }

	          //Check whether start time is less than end time
	          if (startTime.after(endTime))
	          {
	              throw new IllegalArgumentException(
	                  "End time should be greater than Start time in"
	                      + "  DateUtils.getTimeDifference method");
	          }

	          long longStartTime = startTime.getTime();
	          long longEndTime = endTime.getTime();

	          //Get total difference in milli seconds
	          long difference = longEndTime - longStartTime;

	          long temp = difference;

	          //Get milli seconds
	          long milliseconds = temp % 1000;
	          temp = temp / 1000;

	          //Get seconds
	          long seconds = temp % 60;
	          temp = temp / 60;

	          //Get Minutes
	          long minutes = temp % 60;
	          temp = temp / 60;

	          //Get Hours
	          long hours = temp;

	          //Calculate result
	          String result =
	              addLeadingZeros(hours,2)
	              + ":"
	              + addLeadingZeros(minutes,2)
	              + ":"
	              + addLeadingZeros(seconds,2)
	              + "."
	              + addLeadingZeros(milliseconds,3);

	          //Format result and return
	          return result;
	      }
	      
	  *//**
	   * Adds leading zeros to given value until max length is reached
	   *//*
	  private static String addLeadingZeros(long value, int maxLength)
	  {
	      String result = Long.toString(value);

	      int remaining = maxLength - result.length();
	      for (int index = 0; index < remaining; index++)
	      {
	          result = "0" + result;
	      }
	      return result;
	  }

	  *//**
	   * Returns time rounded to two decimal double value
	   * @author sahmed
	   * @param double value
	   * @return double rounded in $$.$$ format
	   * @throws Exception for all others
	   *//*
	  
		public static double roundTwoDecimals(double d) {
	    	DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	    *//**
		    * Adds specified interval to input date.
		    * @author sahmed
		    * Valid values for Interval are Calendar.YEAR,
		    * Calendar.MONTH, Calendar.DATE, Calender.HOUR etc. See
		    * Calendar API for more information
		    * @param inputDate Input Date
		    * @param interval Interval
		    * @param amount Amount to add(use negative numbers
		    * to subtract
		    * @return Date after addition
		    * @throws IllegalArgumentException for Invalid input
		    * @throws Exception for all others
		    *//*
		    public static Date addToDate(
		        Date inputDate,
		        int interval,
		        int amount)
		        throws IllegalArgumentException, Exception
		    {
		        //Difference in days
		        int differenceInDays = 0;

		        //Validate Input date
		        if (inputDate == null)
		        {
		            throw new IllegalArgumentException(
		                "Input date cannot be"
		                    + " null in DateUtils.addToDate method");
		        }

		        //Get instance of calendar
		        Calendar calendar = Calendar.getInstance();

		        //Set input date to calendar
		        calendar.setTime(inputDate);

		        //Add amount to interval
		        calendar.add(interval, amount);

		        //Return result date;
		        return calendar.getTime();
		    }
		    
		    *//**
			    * Gets the password policy from local commoncode
				* @return Map containing the password policy details
			    * @throws Exception
			    *//*  
		    public static Map<String,String> getLocalPasswordPolicyMap() throws Exception{
		    	logger.verbose("Entering CommonUtilities.getPasswordPolicyMap()");
		    	
		    	HashMap<String,String> map = new HashMap<String,String>();
				Document commonCodeInputDoc = XMLUtil.createDocument("CommonCode");
				Element commonCode = commonCodeInputDoc.getDocumentElement();
				commonCode.setAttribute("CodeType", DSWConstants.CODE_DSW_PWD_POLICY);
				
				if(logger.isVerboseEnabled())
					logger.verbose("getCommonCodeList request:"+XMLUtil.getXMLString(commonCodeInputDoc));
				
				Document commonCodeOutDoc = CommonUtilities.invokeAPI(
						CommonUtilities.createEnvironment("system", "system"),
						"getCommonCodeList.CommonUtilities.xml", "getCommonCodeList",
						commonCodeInputDoc);

				if(logger.isVerboseEnabled())
					logger.verbose("getCommonCodeList response:"+XMLUtil.getXMLString(commonCodeOutDoc));
		    	
				//extract the attributelist and populate the map
				NodeList commonCodeAttributeList = commonCodeOutDoc.getElementsByTagName("CommonCode"); 
				int listSize = commonCodeAttributeList.getLength();
				for(int i=0;i< listSize;i++){
					Element commonAttributeElem = (Element)commonCodeAttributeList.item(i);
					map.put(commonAttributeElem.getAttribute("CodeValue"),
							commonAttributeElem.getAttribute("CodeShortDescription"));
				}
				
				logger.verbose("Leaving CommonUtilities.getPasswordPolicyMap()");
				return map;
		    }
		    
		    public static boolean isNumeric(String s){
			    String pattern= "^[0-9]*$";
			        if(s.matches(pattern)){
			            return true;
			        }
			        return false;   
			}
		    
		    public static boolean isAlphaNumeric(String s){
		        String pattern= "^[a-zA-Z0-9]*$";
		            if(s.matches(pattern)){
		                return true;
		            }
		            return false;   
		    }*/
	
}

