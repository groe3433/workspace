/*
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */
package com.nwcg.icbs.yantra.util.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Encapsulates set of utility methods used by the USFS ICBS Solution
 * 
 * @author gacharya
 */
public final class CommonUtilities {

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(CommonUtilities.class.getName());
	
	
	/**
	 * Utility Class - Mask Constructor
	 */
	private CommonUtilities() {
	}

	/**
	 * Instance of YIFApi used to invoke Yantra APIs or services.
	 */
	private static YIFApi api = null;

	static {
		try {
			api = YIFClientFactory.getInstance().getApi();
			
		} catch (Exception e) {
			logger.error("IOM_UTIL_0001", e);			
		}		
	}	
	
	/**
	 * Stores the object in the environment under a certain key.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @param value
	 *            Object to be stored in the environment under the given key.
	 * @return Previous object stored in the environment with the same key (if
	 *         present).
	 */
	public static Object setContextObject(YFSEnvironment env, String key,
			Object value) {
		Object oldValue = null;
		Map map = env.getTxnObjectMap();
		if (map != null)
			oldValue = map.get(key);
		env.setTxnObject(key, value);
		return oldValue;
	}

	/**
	 * Retrieves the object stored in the environment under a certain key.
	 * 
	 * @param env
	 *            Yantra Environment Context.
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
	 *            Yantra Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return Poperty retrieved from the environment under the given key.
	 */
	public static String getContextProperty(YFSEnvironment env, String key) {
		String value = null;
		Object obj = env.getTxnObject(key);
		if (obj != null)
			value = obj.toString();
		return value;
	}

	/**
	 * Removes an object from the environment.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return The object stored in the environment under the specified key (if
	 *         any).
	 */
	public static Object removeContextObject(YFSEnvironment env, String key) {
		Object oldValue = null;
		Map map = env.getTxnObjectMap();
		if (map != null)
			oldValue = map.remove(key);
		return oldValue;
	}

	/**
	 * Clears the environment of any user objects stored.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 */
	public static void clearContextObjects(YFSEnvironment env) {
		Map map = env.getTxnObjectMap();
		if (map != null) {
			map.clear();
		}
	}
	
	/**
	 * Invokes a Yantra API.
	 * 
	 * @param env
	 *            Yantra Environment Context.
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
	public static Document invokeAPI(YFSEnvironment env, String templateName,String apiName,
			Document inDoc) throws Exception {
		env.setApiTemplate(apiName,templateName);
		Document returnDoc = api.invoke(env,apiName,inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}
	
	public static Document invokeAPI(YFSEnvironment env, Document template,String apiName,
			Document inDoc) throws Exception {
		env.setApiTemplate(apiName,template);
		Document returnDoc = api.invoke(env,apiName,inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}
	
	public static Document invokeAPI(YFSEnvironment env, String templateName,String apiName,
			String inDoc) throws Exception {
		env.setApiTemplate(apiName,templateName);
		Document returnDoc = api.invoke(env,apiName,YFCDocument.parse(inDoc).getDocument());
		env.clearApiTemplate(apiName);
		return returnDoc;
	}

	/**
	 * Invokes a Yantra API.
	 * 
	 * @param env
	 *            Yantra Environment Context.
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
		return api.invoke(env, apiName, inDoc);
	}

	/**
	 * Invokes a Yantra API.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param apiName
	 *            Name of API to invoke.
	 * @param str
	 *            Input to be passed to the API. Should be a valid XML string.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			String str) throws Exception {
		return api.invoke(env, apiName, YFCDocument.parse(str).getDocument());
	}

	/**
	 * Invokes a Yantra Service.
	 * 
	 * @param env
	 *            Yantra Environment Context.
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
		return api.executeFlow(env, serviceName, inDoc);
	}

	/**
	 * Invokes a Yantra Service.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param str
	 *            Input to be passed to the Service. Should be a valid XML
	 *            String.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env,
			String serviceName, String str) throws Exception {
		return api.executeFlow(env, serviceName, YFCDocument.parse(str)
				.getDocument());
	}

	/**
	 * Retrieves the order line details by invoking the Yantra
	 * getOrderLineDetails API.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param orderLineKey
	 *            OrderLineKey of the order line whose detail has to be fetched.
	 * @param template
	 *            Template to be used.
	 * @throws java.lang.Exception
	 *             If OrderlineKey is invalid.
	 * @return Order Line Details. Same as output of getOrderLineDetails API.
	 */
	public static Document getOrderLineDetails(YFSEnvironment env,
			String orderLineKey, String template) throws Exception {
		// Create Input XML for getOrderLineDetails
		String xml = "<OrderLineDetail OrderLineKey=\"" + orderLineKey + "\"/>";
		if (template != null)
			env.setApiTemplate("getOrderLineDetails", template);

		Document outDoc = invokeAPI(env, "getOrderLineDetails", xml);

		if (template != null)
			env.clearApiTemplate("getOrderLineDetails");
		return outDoc;
	}

	/**
	 * Determines if an order has been put on hold.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param orderHeaderKey
	 *            OrderHeaderKey of the order to check.
	 * @throws java.lang.Exception
	 *             If OrderHeaderKey is invalid.
	 * @return true if Order has been put on hold.
	 */
	public static boolean isOrderOnHold(YFSEnvironment env,
			String orderHeaderKey) throws Exception {
		// We'll see if the output has the status 1100.01XX (All Error Codes
		// would be of the form 1100.01XX).
		Node node = XPathUtil.getNode(getOrderHoldDetails(env, orderHeaderKey),
				NWCGConstants.XPATH_ORDER_DETAILS_ON_HOLD);

		return node != null;
	}

	/**
	 * Retrieves the status of an order line.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param orderLineKey
	 *            OrderLineKey of the order line to check.
	 * @throws java.lang.Exception
	 *             If OrderlineKey is invalid.
	 * @return OrderLine with its status.
	 */
	public static Document getOrderLinesStatus(YFSEnvironment env,
			String orderLineKey) throws Exception {

		// We'll execute getOrderLineStatusList to get the list of statuses the
		// order line is in. We are not using getOrderLineDetails because it
		// does not fire the optimum set of SQLs even with a meagre template.

		env.setApiTemplate("getOrderLineStatusList", YFCDocument.parse(
				NWCGConstants.TEMPLATE_ORDER_LINE_STATUS_LIST).getDocument());

		String xml = "<OrderLineStatus OrderLineKey=\"" + orderLineKey + "\"/>";
		Document lineDetails = invokeAPI(env, "getOrderLineStatusList",
				YFCDocument.parse(xml).getDocument());

		// Clear the template
		env.clearApiTemplate("getOrderLineStatusList");

		return lineDetails;
	}

	/**
	 * Retrieves the Hold Details of an Order.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param orderHeaderKey
	 *            orderHeaderKey of the order line to check.
	 * @throws java.lang.Exception
	 *             If orderHeaderKey is invalid.
	 * @return Order Hold Details.
	 */
	public static Document getOrderHoldDetails(YFSEnvironment env,
			String orderHeaderKey) throws Exception {

		// We'll execute getOrderDetails to get the hold details.

		env.setApiTemplate(NWCGConstants.API_GET_ORDER_DETAILS, YFCDocument.parse(
				NWCGConstants.TEMPLATE_ORDER_DETAILS_HOLD).getDocument());

		String xml = "<Order OrderHeaderKey=\"" + orderHeaderKey + "\"/>";
		Document orderDetails = invokeAPI(env, NWCGConstants.API_GET_ORDER_DETAILS, YFCDocument
				.parse(xml).getDocument());

		// Clear the template
		env.clearApiTemplate(NWCGConstants.API_GET_ORDER_DETAILS);

		return orderDetails;
	}

	/**
	 * 
	 * Returns the clone of an XML Document.
	 * 
	 * @param doc
	 *            Input document to be cloned.
	 * @throws java.lang.Exception
	 *             If uable to clone document.
	 * @return Clone of the document.
	 */
	public static Document cloneDocument(Document doc) throws Exception {
		return YFCDocument.parse(XMLUtil.extractStringFromDocument(doc)).getDocument();
	}

	/**
	 * Returns the clone of an XML Document.
	 * 
	 * @param doc
	 *            Input document to be cloned.
	 * @throws java.lang.Exception
	 *             If uable to clone document.
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
		return CommonUtilities.class.getResourceAsStream(resource);
	}

	public static YFSEnvironment createEnvironment(String userID, String progID)
			throws Exception {
		Document doc = XMLUtil.createDocument("YFSEnvironment");
		Element elem = doc.getDocumentElement();
		elem.setAttribute("userId", userID);
		elem.setAttribute("progId", progID);

		return api.createEnvironment(doc);
	}

	/**
	 * Method to log the properties
	 * 
	 * @param properties
	 *            Properties to be logged
	 * @param logger
	 *            Logger to be used for logging
	 * 
	 */
	public static void logProperties(Map properties, Logger logger) {
		Iterator keyIt = properties.keySet().iterator();
		if(logger.isVerboseEnabled())
			logger.verbose("******[Properties List Start]************");
		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			String val = (String) properties.get(key);
			if(logger.isVerboseEnabled())
				logger.verbose("<"+key + ":" + val+">");

		}
		if(logger.isVerboseEnabled())
			logger.verbose("******[Properties List End]*************");
	}

	/**
	 * Removes the passed Node name from the input document. If no name is
	 * passed, it removes all the nodes.
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
				removeAll(list.item(i), nodeType, name);
			}
		}
	}

	public static boolean isDocumentNamespaceAware() {
		return NWCGConstants.YES.equals(ResourceUtil.get("yantra.document.isnamespaceaware"));
	}

	public static String convertToYantraDate(Date dt) {
		YFCDate yDate = new YFCDate(dt);
		return yDate.getString();
	}

	public static List getListFromNodeList(NodeList nList) {
		if (null == nList)
			return null;
		int nodeLength = nList.getLength();
		ArrayList list = new ArrayList(nodeLength);
		for (int i = 0; i < nodeLength; i++) {
			list.add(i, nList.item(i));
		}
		return list;
	}
	
	/**
	 * @returns true if the development mode is set to true in the yantraimpl.properties
	 */
	public static boolean isDevelopmentMode() {
		String devmode = ResourceUtil.get(NWCGConstants.KEY_DEV_MODE);
		return Boolean.getBoolean(devmode);
	}	

	/**
	 * Check for the order line exists with the given request no. and incident no
	 * TODO add the cancel ckeck to ignore s-numbers of cancelled lines 
	 */
	public static boolean checkOrderLineExistsForRequestNo(YFSEnvironment env,String requestNo,String incidentNo,String incidentYear) throws Exception{

		logger.debug("Entering CommonUtilities->checkOrderLineExistsForRequestNo()");
		logger.debug("Input S-Number"+requestNo+" IncidentNo:"+incidentNo);
		
		boolean rFlag = false;
		// do a get order line list and first check if the request no exists
		Document getOrderLineListInput = XMLUtil.createDocument(NWCGConstants.ORDER_LINE);
		Element olExtn = getOrderLineListInput.createElement(NWCGConstants.EXTN_ELEMENT);
		getOrderLineListInput.getDocumentElement().appendChild(olExtn);
		olExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO,requestNo);
		if(!StringUtil.isEmpty(incidentNo)){
			Element order = getOrderLineListInput.createElement(NWCGConstants.ORDER_ELM);
			getOrderLineListInput.getDocumentElement().appendChild(order);
			Element ordExtn = getOrderLineListInput.createElement(NWCGConstants.EXTN_ELEMENT);
			order.appendChild(ordExtn);
			ordExtn.setAttribute(NWCGConstants.INCIDENT_NO,incidentNo);
			//cr 409 ks
			ordExtn.setAttribute(NWCGConstants.INCIDENT_YEAR,incidentYear);
		}
		Document getOrderLineListTemplate = XMLUtil.createDocument("OrderLineList");
		getOrderLineListTemplate.getDocumentElement().setAttribute("TotalLineList",NWCGConstants.EMPTY_STRING);
		//getOrderLineListTemplate.getDocumentElement().setAttribute("TotalNumberOfRecords",NWCGConstants.EMPTY_STRING);
		Element ol = getOrderLineListTemplate.createElement(NWCGConstants.ORDER_LINE);
		getOrderLineListTemplate.getDocumentElement().appendChild(ol);
		ol.setAttribute(NWCGConstants.ORDER_LINE_KEY,NWCGConstants.EMPTY_STRING);
		ol.setAttribute(NWCGConstants.STATUS_ATTR,NWCGConstants.EMPTY_STRING);
		ol.setAttribute(NWCGConstants.MAX_LINE_STATUS, NWCGConstants.EMPTY_STRING);
		logger.verbose("OrderLine List Input: "+XMLUtil.extractStringFromDocument(getOrderLineListInput));
		Document getOrderLineList = CommonUtilities.invokeAPI(env,getOrderLineListTemplate,NWCGConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
		String totalLines = getOrderLineList.getDocumentElement().getAttribute("TotalLineList");
		Element elmOrderLineList = getOrderLineList.getDocumentElement();
		NodeList nlOrderLineList = elmOrderLineList.getElementsByTagName(NWCGConstants.ORDER_LINE);
		// If NodeList size is less than 1, then it means that there are no rows
		// If NodeList size is greater than 1, then check the status
		if (nlOrderLineList.getLength() < 1){
			rFlag = false;
		}
		else if (nlOrderLineList.getLength() > 0){
			boolean obtResult = false;
			for (int i=0; i < nlOrderLineList.getLength() && !obtResult; i++){
				Element elmOrderLine = (Element) nlOrderLineList.item(i);
				if(elmOrderLine.getAttribute(NWCGConstants.MAX_LINE_STATUS).startsWith("9000")){
					rFlag = false;
				}
				else {
					obtResult = true;
					rFlag = true;
				}
			}
		}
		logger.debug("OrderLine exists with the same request number : " + rFlag);
//BEGIN CR 583 ML
		Element root_ele = getOrderLineList.getDocumentElement();
		NodeList nlOrderLine = root_ele.getElementsByTagName(NWCGConstants.ORDER_LINE);
		
		if(!StringUtil.isEmpty(totalLines))
		{			
			//If results are returned, we need to cycle through the list
			//and check the line statuses 
			//If ANY of the line  are not 'canceled', then rFlag is true, else rFlag is false.
			
			for(int ctr = 0; ctr < Integer.parseInt(totalLines); ctr++)
			{			
				Element elemOrderLine = (Element) nlOrderLine.item(ctr);
				String  strLineStatus = elemOrderLine.getAttribute(NWCGConstants.STATUS_ATTR);
				
				if((strLineStatus.indexOf("ancelled") == -1))
				{
					rFlag = true;
					break;
				}
			}
//END CR 583 ML
			logger.verbose("OrderLine List :"+XMLUtil.extractStringFromDocument(getOrderLineList));
			logger.debug("Exiting CommonUtilities->checkOrderLineExistsForRequestNo()");
		}
		return rFlag;
	}
	
	/**
	 * This method gets the item id from the input element
	 * calls the computePriceForItem and gets the pricing details
	 * after getting the details sets the AcquisitionCost in the same document
	 */
	public static void stampAcquisitionCost(YFSEnvironment env,Element docCreateTrackableInventoryIP) 
	{
		Document docComputePriceForItemIP  = null ;
		try
		{
			docComputePriceForItemIP  = XMLUtil.createDocument("ComputePriceForItem");
			Element elemComputePriceForItemIP = docComputePriceForItemIP.getDocumentElement();
			elemComputePriceForItemIP.setAttribute("Currency",NWCGConstants.CURRENCY_USD);
			elemComputePriceForItemIP.setAttribute(NWCGConstants.ITEM_ID,docCreateTrackableInventoryIP.getAttribute(NWCGConstants.ITEM_ID));
			elemComputePriceForItemIP.setAttribute(NWCGConstants.UOM,docCreateTrackableInventoryIP.getAttribute(NWCGConstants.UNIT_OF_MEASURE));
			elemComputePriceForItemIP.setAttribute(NWCGConstants.PRODUCT_CLASS,NWCGConstants.NWCG_ITEM_DEFAULT_PRODUCT_CLASS);
			elemComputePriceForItemIP.setAttribute(NWCGConstants.ORGANIZATION_CODE,NWCGConstants.ENTERPRISE_CODE);
			
			Document docPriceProgram = XMLUtil.createDocument("CommonCode");
			Element elePriceProgram = docPriceProgram.getDocumentElement();
			elePriceProgram.setAttribute("CodeType", NWCGConstants.PRICE_PROGRAM);
			elePriceProgram.setAttribute("CodeValue", NWCGConstants.PROGRAM_NAME);
			String strPriceProgramName = NWCGConstants.NWCG_PRICE_PROGRAM;
			
			Document docPriceProgramName = CommonUtilities.invokeAPI(env,"getCommonCodeList", docPriceProgram);
			Element elePriceProgramName = docPriceProgramName.getDocumentElement();
			
			NodeList nl = elePriceProgramName.getElementsByTagName("CommonCode");
			if(nl != null && nl.getLength() > 0)
			{
				Element elePriceProgramNameValue = (Element) nl.item(0);
				strPriceProgramName = StringUtil.nonNull(elePriceProgramNameValue.getAttribute("CodeShortDescription"));
			}
						
			elemComputePriceForItemIP.setAttribute("PriceProgramName", strPriceProgramName);
			env.setApiTemplate("computePriceForItem","CommonUtilities_computePriceForItem");
			Document docComputePriceForItemOP = CommonUtilities.invokeAPI(env,"computePriceForItem",docComputePriceForItemIP);
			Element elemComputePriceForItemOP = docComputePriceForItemOP.getDocumentElement();
			String strCost = StringUtil.nonNull(XPathUtil.getString(elemComputePriceForItemOP,"/ComputePriceForItem/ItemPriceSet/@ListPrice"));
			docCreateTrackableInventoryIP.setAttribute("AcquisitionCost",strCost);
		}
		catch(com.yantra.yfs.japi.YFSException ex)
		{
			logger.error("CommonUtilities::stampAcquisitionCost failed to get the acquisition cost continuing with the flow.... IP "+XMLUtil.getXMLString(docComputePriceForItemIP),ex);
			logger.verbose("Message :: "+ex.getMessage());
			//ex.printStackTrace();
		}
		catch(Exception e)
		{
			logger.error("CommonUtilities::stampAcquisitionCost failed to get the acquisition cost continuing with the flow.... IP "+XMLUtil.getXMLString(docComputePriceForItemIP),e);
			logger.verbose("Message :: "+e.getMessage());			
			//e.printStackTrace();
		}
		finally
		{
			env.clearApiTemplate("computePriceForItem");
		}
	}
	
	/**
	 * This method returns the owner unit name from the given owner unit id 
	 */
	public static String getOwnerUnitIDName(YFSEnvironment env,String strOwnerUnitID) throws Exception
	{
		Document doc = XMLUtil.createDocument(NWCGConstants.ORGANIZATION);
		Element elem = doc.getDocumentElement();
		elem.setAttribute(NWCGConstants.ORGANIZATION_CODE,strOwnerUnitID);
		env.setApiTemplate(NWCGConstants.API_GET_ORG_LIST,"CommonUtilities_getOrganizationList");
		Document op = CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_ORG_LIST,doc);
		Element elemOP = op.getDocumentElement();
		
		String strOrganizationName = NWCGConstants.EMPTY_STRING;
		
		if (strOwnerUnitID.length() > 0 )
		{
			 NodeList nl = elemOP.getElementsByTagName(NWCGConstants.ORGANIZATION);
			 if(nl != null && nl.getLength() > 0) {
				Element elemOrganization = (Element) nl.item(0);
				strOrganizationName = StringUtil.nonNull(elemOrganization.getAttribute(NWCGConstants.ORGANIZATION_NAME));
			 }
			 env.clearApiTemplate(NWCGConstants.API_GET_ORG_LIST);
		} 		
		return strOrganizationName;
	}
	
	/**
	 * This method returns the item description for the given item id
	 */
	public static String getItemDescription(YFSEnvironment env,String strItemID,String strUOM) throws Exception
	{
		logger.debug("Getting item description for :"+strItemID+" / "+strUOM);
		Document doc = XMLUtil.createDocument(NWCGConstants.ITEM);
		Element elem = doc.getDocumentElement();
		elem.setAttribute(NWCGConstants.ITEM_ID,strItemID);
		elem.setAttribute(NWCGConstants.UNIT_OF_MEASURE,strUOM);
		elem.setAttribute(NWCGConstants.ORGANIZATION_CODE,NWCGConstants.ENTERPRISE_CODE);
		env.setApiTemplate(NWCGConstants.API_GET_ITEM_DETAILS, "CommonUtilities_getItemDetails");
		Document op = CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_ITEM_DETAILS,doc);
		env.clearApiTemplate(NWCGConstants.API_GET_ITEM_DETAILS);
		String strDesc = NWCGConstants.EMPTY_STRING ;
		if(op != null)
		{
			logger.verbose("getItemDescription:: op "+XMLUtil.extractStringFromDocument(op));
			Element elemOP = op.getDocumentElement();
			NodeList nlPrimaryInformation = elemOP.getElementsByTagName(NWCGConstants.PRIMARY_INFORMATION);
			if(nlPrimaryInformation != null)
			{
				Element elemPrimaryInformation = (Element)nlPrimaryInformation.item(0);
				strDesc = StringUtil.nonNull(elemPrimaryInformation.getAttribute(NWCGConstants.SHORT_DESCRIPTION));
			}			
		}
		logger.verbose("CommonUtilities.getItemDescription()::returning "+strDesc);
		return strDesc;
	}
	
	public static Document getAvailableRFIQty (YFSEnvironment env, String strItemID, String strUOM, String strPC, String shipNode, String strItemDesc) throws Exception {
		logger.debug("CommonUtilities.getAvailableRFIQty() begin");
		
		double TotalLocationInvQty = 0.00;
		double TotalDemand = 0.00;
		double TotalAvailRFI = 0.00;
		
		Document getNodeInventoryDoc = getNodeInventoryInputDoc(
				shipNode, NWCGConstants.RFI_STATUS, strItemID);
		logger.verbose("getNodeInventory output: "+XMLUtil.extractStringFromDocument(getNodeInventoryDoc));
		Document getATPDoc = getATPInputDoc(shipNode, strItemID, strPC,
				strUOM, NWCGConstants.ENTERPRISE_CODE);
		logger.verbose("getATPInputDoc output: "+XMLUtil.extractStringFromDocument(getATPDoc));
		String nodeInventoryOpTemplate = "<NodeInventory><LocationInventoryList TotalNumberOfRecords=\"\"><LocationInventory "
				+ "LocationId=\"\" PendInQty=\"\" PendOutQty=\"\" Quantity=\"\" ZoneId=\"\"><InventoryItem ItemID=\"\"><Item "
				+ "ItemID=\"\"></Item></InventoryItem></LocationInventory></LocationInventoryList></NodeInventory>";
		
		String getATPOpTemplate = "<InventoryInformation><Item AdvanceNotificationTime=\"\" AvailableToSell=\"\" "
				+ "CalculateProjectedOnhandQty=\"\" Description=\"\" EndDate=\"\" ItemID=\"\" LeadTime=\"\" "
				+ "OrganizationCode=\"\" PeriodicalLength=\"\" ProcessingTime=\"\" ProductClass=\"\" "
				+ "ShipNode=\"\" ShortDescription=\"\" TagControl=\"\"  TimeSensitive=\"\" TrackedEverywhere=\"\" "
				+ "UnitOfMeasure=\"\"><InventoryTotals><Demands TotalDemand=\"\"><Demand DemandType=\"\" "
				+ "OrganizationCode=\"\" Quantity=\"\"/></Demands></InventoryTotals></Item></InventoryInformation>";

		Document nodeInventoryOutput = null;
		Document atpOutput = null;

		try {
			Document getNodeInventoryOpT = XMLUtil.getDocument(nodeInventoryOpTemplate);	
			Document getATPOpT = XMLUtil.getDocument(getATPOpTemplate);

			nodeInventoryOutput = CommonUtilities.invokeAPI(env,
					getNodeInventoryOpT, "getNodeInventory",
					getNodeInventoryDoc);
			
			logger.verbose("getATP Input: "+XMLUtil.extractStringFromDocument(getATPDoc));
			atpOutput = CommonUtilities.invokeAPI(env, getATPOpT, "getATP", getATPDoc);
			logger.verbose("getATP Output: "+ XMLUtil.extractStringFromDocument(atpOutput));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		if (nodeInventoryOutput != null) {

			NodeList locationInventoryNL = nodeInventoryOutput.getElementsByTagName("LocationInventory");

			if (locationInventoryNL != null	&& locationInventoryNL.getLength() > 0) {
				Node curLocationInventoryNode = null;
				for (int j = 0; j < locationInventoryNL.getLength(); j++) {
					curLocationInventoryNode = locationInventoryNL.item(j);
					Element curLocationInventoryElm = (curLocationInventoryNode instanceof Element) ? (Element) curLocationInventoryNode
							: null;
					if (curLocationInventoryElm == null) continue;

					String strQty = curLocationInventoryElm.getAttribute(NWCGConstants.QUANTITY);

					double dQty = Double.parseDouble(strQty);
					TotalLocationInvQty = TotalLocationInvQty + dQty;
				}
			}
		}
		
		if (atpOutput != null) {
			NodeList inventoryTotalsNL = atpOutput.getElementsByTagName("InventoryTotals");
			if (inventoryTotalsNL != null && inventoryTotalsNL.getLength() == 1) {
				Node curInventoryTotalNode = inventoryTotalsNL.item(0);
				Element curInventoryTotalElm = (curInventoryTotalNode instanceof Element) ? 
						(Element) curInventoryTotalNode : null;
				if (curInventoryTotalElm == null) throw new YFSException("Output template of getATP is missing InventoryInformation/InventoryTotals");
				
				NodeList inventoryDemandsNL = curInventoryTotalElm.getElementsByTagName("Demand");
				if (inventoryDemandsNL != null && inventoryDemandsNL.getLength() > 0) {
					Node curDemandNode = null;
					for (int k = 0; k < inventoryDemandsNL.getLength(); k++) {
						curDemandNode = inventoryDemandsNL.item(k);
						Element curDemandElm = (curDemandNode instanceof Element) ? (Element) curDemandNode : null;
						if (curDemandElm == null) continue;
	
						String strDemand = curDemandElm.getAttribute(NWCGConstants.QUANTITY);
						double dblDemand = Double.parseDouble(strDemand);
						TotalDemand = TotalDemand + dblDemand;
					}
				}
			}				
		}
		TotalAvailRFI = (TotalLocationInvQty - TotalDemand);
		
		
		Document rt_Item = XMLUtil.createDocument(NWCGConstants.ITEM);
		Element el_Item=rt_Item.getDocumentElement();
		// set the data
		el_Item.setAttribute(NWCGConstants.ITEM_ID, strItemID);
		el_Item.setAttribute(NWCGConstants.PRODUCT_CLASS,strPC);
		el_Item.setAttribute(NWCGConstants.SHORT_DESCRIPTION, strItemDesc);
		el_Item.setAttribute(NWCGConstants.UNIT_OF_MEASURE,strUOM);
		el_Item.setAttribute(NWCGConstants.AVAILABLE_QUANTITY, new Double(TotalAvailRFI).toString());

		logger.debug("Item ID: "+strItemID+" PC: "+strPC+" UOM: "+strUOM+" Short Desc: "+strItemDesc);
		logger.debug("Returning an RFI Qty of: "+new Double(TotalAvailRFI).toString());

		return rt_Item;
	}
	
	public  static Document getSupplyDetails(String strItemId, String strUOM, String strPC, String strNode, String strEndDate, YFSEnvironment env) throws Exception {
		logger.debug("CommonUtilities.getSupplyDetails() begin");
		Document rt_getSupplyDetails = XMLUtil.createDocument("getSupplyDetails");
		
		Element el_getSupplyDetails=rt_getSupplyDetails.getDocumentElement();
		// get the item id
		el_getSupplyDetails.setAttribute(NWCGConstants.ITEM_ID,strItemId);
		el_getSupplyDetails.setAttribute(NWCGConstants.ITEM_ORG_CODE,NWCGConstants.ENTERPRISE_CODE);
		el_getSupplyDetails.setAttribute(NWCGConstants.ORGANIZATION_CODE,NWCGConstants.ENTERPRISE_CODE);
		el_getSupplyDetails.setAttribute(NWCGConstants.PRODUCT_CLASS,strPC);
		el_getSupplyDetails.setAttribute(NWCGConstants.SHIP_NODE,strNode);
		el_getSupplyDetails.setAttribute(NWCGConstants.UNIT_OF_MEASURE,strUOM);
		if(strNode == null || strNode.equals(NWCGConstants.EMPTY_STRING))
		{
			el_getSupplyDetails.setAttribute("ConsiderAllNodes",NWCGConstants.YES);
		}
		
		// -- JSK reserved quantity consideration: added strEndDate
		if(strEndDate != null && !strEndDate.equals(NWCGConstants.EMPTY_STRING))
		{
			el_getSupplyDetails.setAttribute(NWCGConstants.END_DATE,strEndDate); //add EndDate only if it's not null/blank
		}
		
		// -- JSK reserved quantity consideration: added strEndDate
		if(strEndDate != null && !strEndDate.equals(NWCGConstants.EMPTY_STRING))
		{
			el_getSupplyDetails.setAttribute(NWCGConstants.END_DATE,strEndDate); //add EndDate only if it's not null/blank
		}
		
		// -- JSK reserved quantity consideration: added strEndDate
		if(strEndDate != null && !strEndDate.equals(NWCGConstants.EMPTY_STRING))
		{
			el_getSupplyDetails.setAttribute(NWCGConstants.END_DATE,strEndDate); //add EndDate only if it's not null/blank
		}
		
		// -- JSK reserved quantity consideration: added strEndDate
		if(strEndDate != null && !strEndDate.equals(NWCGConstants.EMPTY_STRING))
		{
			el_getSupplyDetails.setAttribute(NWCGConstants.END_DATE,strEndDate); //add EndDate only if it's not null/blank
		}
		
		// -- JSK reserved quantity consideration: added strEndDate
		if(strEndDate != null && !strEndDate.equals(NWCGConstants.EMPTY_STRING))
		{
			el_getSupplyDetails.setAttribute(NWCGConstants.END_DATE,strEndDate); //add EndDate only if it's not null/blank
		}
		
		if(rt_getSupplyDetails != null)
		{
			if(logger.isVerboseEnabled()) logger.verbose("calling getSupplyDetails with i/p "+XMLUtil.getXMLString(rt_getSupplyDetails));
		}
		// Jay : Comment out this line once the issue 38346 is fixed, the problem is, when you set the template
		// on getSupplyDetails API it is running into an infinite loop
		//Document returnDoc = CommonUtilities.invokeAPI(env,"CommonUtilities_getSupplyDetails","getSupplyDetails",rt_getSupplyDetails);
		Document returnDoc = CommonUtilities.invokeAPI(env,"getSupplyDetails",rt_getSupplyDetails);
		Element root_ele = returnDoc.getDocumentElement();
		if(returnDoc != null)
		{
			logger.verbose("output getSupplyDetails ==> "+XMLUtil.extractStringFromDocument(returnDoc));
		}
		// get the inventory snap shot
		//String strQty = XPathUtil.getString(root_ele,"/Item/ShipNodes/ShipNode/Supplies/Supply[@SupplyType='ONHAND']/@TotalQuantity");
		// Jay : Changes for CR # 263 
		String strQty = XPathUtil.getString(root_ele,"/Item/ShipNodes/ShipNode/@AvailableQty");
		
		if(strQty == null || strQty.equals(NWCGConstants.EMPTY_STRING))
		{// default it to ZERO
			strQty = "0";
		}
		String strDesc = XPathUtil.getString(root_ele,"/Item/@ShortDescription");
		if(strDesc == null)
		{
			// if no description set it to blank
			strDesc = NWCGConstants.EMPTY_STRING ;
		}
		Document rt_Item = XMLUtil.createDocument(NWCGConstants.ITEM);
		Element el_Item=rt_Item.getDocumentElement();
		// set the data
		el_Item.setAttribute(NWCGConstants.ITEM_ID,strItemId);
		el_Item.setAttribute(NWCGConstants.PRODUCT_CLASS,strPC);
		el_Item.setAttribute(NWCGConstants.SHORT_DESCRIPTION,strDesc);
		el_Item.setAttribute(NWCGConstants.UNIT_OF_MEASURE,strUOM);
		el_Item.setAttribute(NWCGConstants.AVAILABLE_QUANTITY,strQty);
		return rt_Item;
	}
	
	/*
	 * This method checks if the inventory exists for any given set of item and item attributes
	 * returns true if inventory exists else returns false
	 */
	public  static boolean inventoryExistsAtAnyNode(String strItemId, String strUOM, String strPC,String strOrgCode, YFSEnvironment env) throws Exception {
		logger.debug("CommonUtilities.inventoryExistsAtAnyNode() begin");
		Document rt_getSupplyDetails = XMLUtil.createDocument("getSupplyDetails");		
		Element el_getSupplyDetails=rt_getSupplyDetails.getDocumentElement();
		
		// get the item id
		el_getSupplyDetails.setAttribute(NWCGConstants.ITEM_ID,strItemId);
		el_getSupplyDetails.setAttribute(NWCGConstants.ITEM_ORG_CODE,strOrgCode);
		el_getSupplyDetails.setAttribute(NWCGConstants.ORGANIZATION_CODE,strOrgCode);
		el_getSupplyDetails.setAttribute(NWCGConstants.PRODUCT_CLASS,strPC);
		el_getSupplyDetails.setAttribute(NWCGConstants.UNIT_OF_MEASURE,strUOM);
		el_getSupplyDetails.setAttribute("ConsiderAllNodes",NWCGConstants.YES);
		
		if(rt_getSupplyDetails != null)
		{
			logger.verbose("calling getSupplyDetails with i/p "+XMLUtil.extractStringFromDocument(rt_getSupplyDetails));
		}
		// Jay : Comment out this line once the issue 38346 is fixed, the problem is, when you set the template
		// on getSupplyDetails API it is running into an infinite loop
		//Document returnDoc = CommonUtilities.invokeAPI(env,"CommonUtilities_getSupplyDetails","getSupplyDetails",rt_getSupplyDetails);
		Document returnDoc = CommonUtilities.invokeAPI(env,"getSupplyDetails",rt_getSupplyDetails);
		Element root_ele = returnDoc.getDocumentElement();
		if(returnDoc != null)
		{
			logger.verbose("output getSupplyDetails ==> "+XMLUtil.extractStringFromDocument(returnDoc));
			// get the ShipNode element
			NodeList nlShipNode = root_ele.getElementsByTagName(NWCGConstants.SHIP_NODE);
			if(nlShipNode != null && nlShipNode.getLength() > 0)
			{
				int lastIndex = nlShipNode.getLength() ;
				// for all ship nodes
				for(int index=0 ; index < lastIndex; index++)
				{
					Element elemShipNode = (Element) nlShipNode.item(index);
					// if quantity exists
					String strAvailableQty = elemShipNode.getAttribute(NWCGConstants.AVAILABLE_QUANTITY);
					logger.debug("AvailableQuantity Exists ==> "+strAvailableQty);
					// if available qty is blank it is as good as no inventory exists
					if(strAvailableQty != null && (!strAvailableQty.equals(NWCGConstants.EMPTY_STRING)))
					{
						boolean bOP = numberNotEqualsZero(strAvailableQty);
						logger.debug("Supply ==>> verifyNumber "+ bOP);
						if(bOP == true)
							return true;
						else // continue with the Demand
						{
							NodeList nlDemand = elemShipNode.getElementsByTagName("Demand");
							if(nlDemand != null && nlDemand.getLength() > 0)
							{
								// only one element
								Element elemDemand = (Element) nlDemand.item(0);
								String strQty = elemDemand.getAttribute("TotalQuantity");
								bOP = numberNotEqualsZero(strQty);
								logger.debug("Demand ==>> verifyNumber "+ bOP);
								if(bOP == true)
									return true;
							}
							// continue with ExactMatchedDemands
							nlDemand = elemShipNode.getElementsByTagName("ExactMatchedDemands");
							if(nlDemand != null && nlDemand.getLength() > 0)
							{
								// only one element
								Element elemDemand = (Element) nlDemand.item(0);
								String strQty = elemDemand.getAttribute("TotalQuantity");
								bOP = numberNotEqualsZero(strQty);
								logger.debug("ExactMatchedDemands ==>> verifyNumber "+ bOP);
								if(bOP == true)
									return true;
							}
						}
					}// end if qty is not null
				}// end for loop
			}// end if node exists
		}
		return false;
	}
	
	private static boolean numberNotEqualsZero(String num)
	{
		try
		{
			double iQty = Double.parseDouble(num);
			// and quantity does not equals to zero
			// the inventory exists and hence return false
			if(iQty != 0.0)
			{
				return true;
			}// end if quantity != 0
		}
		catch(NumberFormatException ex)
		{
			// do nothing try with int
			int iQty = Integer.parseInt(num);
			// and quantity does not equals to zero
			// the inventory exists and hence return false
			if(iQty != 0)
			{
				return true;
			}// end if quantity != 0
		}
		return false;
	}
	
	/**
	 * Returns a secondary serial for a given serial number
	 */
	public static String getSecondarySerial(YFSEnvironment env, String strSerialNo,String ItemID) throws Exception 
	{
		String strSecSerial = NWCGConstants.EMPTY_STRING;
		// have to reset the api actually midifies the input document
		Document docGetSerialListIP = XMLUtil.createDocument("Serial");
		Element elemGetSerialListIP = docGetSerialListIP.getDocumentElement();
		// will get the secondary serial
		elemGetSerialListIP.setAttribute("SerialNo",strSerialNo);
		if (ItemID.length() > 0)
		{
		 Element el_InvItem=docGetSerialListIP.createElement("InventoryItem");
		 elemGetSerialListIP.appendChild(el_InvItem);
		 el_InvItem.setAttribute(NWCGConstants.ITEM_ID,ItemID);
		}
		// getting all the serials whoes parent is this serial 
		env.setApiTemplate(NWCGConstants.API_GET_SERIAL_LIST,"CommonUtilities_getSerialList");

		Document docGetSerialListOP = CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_SERIAL_LIST,docGetSerialListIP);

		env.clearApiTemplate(NWCGConstants.API_GET_SERIAL_LIST);
		
		Element elemSerialList = docGetSerialListOP.getDocumentElement();
		// get the the secondary serial
		NodeList nlSerial = elemSerialList.getElementsByTagName("Serial");
		if(nlSerial != null && nlSerial.getLength() > 0)
		{
			Element elemSerial = (Element) nlSerial.item(0);
			strSecSerial = elemSerial.getAttribute("SecondarySerial1");
		}		
		return strSecSerial;
	}
	
	/**
	 * Method to copy addresses from one element to another
	 * 
	 * @return Element	
	*/
	public static Element populateAddressDetails(Element fromAddress, Element toAddress) 
	{
		toAddress.setAttribute(NWCGConstants.ADDRESS_LINE_1,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.ADDRESS_LINE_1)));
		toAddress.setAttribute(NWCGConstants.ADDRESS_LINE_2,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.ADDRESS_LINE_2)));
		toAddress.setAttribute(NWCGConstants.ADDRESS_LINE_3,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.ADDRESS_LINE_3)));
		toAddress.setAttribute(NWCGConstants.ADDRESS_LINE_4,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.ADDRESS_LINE_4)));
		toAddress.setAttribute(NWCGConstants.ADDRESS_LINE_5,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.ADDRESS_LINE_5)));
		toAddress.setAttribute(NWCGConstants.ADDRESS_LINE_6,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.ADDRESS_LINE_6)));
		
		toAddress.setAttribute(NWCGConstants.CITY,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.CITY)));
		toAddress.setAttribute(NWCGConstants.STATE,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.STATE)));
		toAddress.setAttribute(NWCGConstants.ZIP_CODE,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.ZIP_CODE)));
		toAddress.setAttribute(NWCGConstants.COMPANY,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.COMPANY)));
		
		toAddress.setAttribute(NWCGConstants.COUNTRY,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.COUNTRY)));
		toAddress.setAttribute(NWCGConstants.FIRST_NAME,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.FIRST_NAME)));
		toAddress.setAttribute(NWCGConstants.LAST_NAME,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.LAST_NAME)));
		toAddress.setAttribute(NWCGConstants.MIDDLE_NAME,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.MIDDLE_NAME)));
		
		toAddress.setAttribute(NWCGConstants.DAY_PHONE,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.DAY_PHONE)));
		toAddress.setAttribute(NWCGConstants.MOBILE_PHONE,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.MOBILE_PHONE)));
		toAddress.setAttribute(NWCGConstants.EMAIL_ID,StringUtil.nonNull(fromAddress.getAttribute(NWCGConstants.EMAIL_ID)));
		
		return toAddress;
	}
	
	public static void raiseTrackableAlert(YFSEnvironment env,Document inDoc) throws NWCGException
	{
		Element root_elem = inDoc.getDocumentElement();
		root_elem.setAttribute("ActiveFlag",NWCGConstants.EMPTY_STRING);
		root_elem.setAttribute("ExceptionType","Insert/Update Trackable Record");
		root_elem.setAttribute("InboxType",NWCGConstants.NWCG_TRACKABLEINVENTORY_QUEUE_ID);
		root_elem.setAttribute("QueueId",NWCGConstants.NWCG_TRACKABLEINVENTORY_INBOX_TYPE);
		root_elem.setAttribute("ActiveFlag",NWCGConstants.EMPTY_STRING);
		//set by the parent
		//root_elem.setAttribute("ApiName",NWCGConstants.EMPTY_STRING);
		//root_elem.setAttribute("DetailDescription",NWCGConstants.EMPTY_STRING);
		try
        {
			CommonUtilities.invokeAPI(env,NWCGConstants.NWCG_CREATE_EXCEPTION, inDoc);
        }
		catch(Exception ex1)
		{
			logger.error("Exception thrown while calling createException",ex1);
			ex1.printStackTrace();
        	throw new NWCGException("NWCG_RETURN_ERROR_WHILE_LOGGING_ALERT");
        }//End catch
	}
	
	public static String changeDateFormat(String strShipByDate)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			Date dShipByDate = sdf.parse(strShipByDate);
			
			SimpleDateFormat sdfTarget = new SimpleDateFormat("yyyy-MM-dd");
			strShipByDate = sdfTarget.format(dShipByDate);
		}
		catch(Exception ex)
		{
			// do nothing 
		}
		return strShipByDate;
	}
	
	public static Document getCreateExceptionInput(SOAPMessage sf){
		
		Document outDoc = null;
		SOAPBody sb = null;
		SOAPFault soapFault = null ;
		
		try {
			try 
			{
				sb = sf.getSOAPBody();
				soapFault = sb.getFault();
			}
			catch (SOAPException e) 
			{
				logger.error("Exception while gettting the SOAPBody " + e);
				e.printStackTrace();
			}
		
			logger.debug("SOAPFault :: getCreateExceptionInput ==> "+ soapFault.toString());
		
			if(soapFault != null )
			{
				Document rt_Inbox = XMLUtil.newDocument();
	            Element el_Inbox = rt_Inbox.createElement("Inbox");
	            rt_Inbox.appendChild(el_Inbox);
	
	            el_Inbox.setAttribute(NWCGConstants.ALERT_DESC, soapFault.getFaultCode());
	            el_Inbox.setAttribute("EnterpriseKey",NWCGAAConstants.ENTERPRISE_KEY);
	            el_Inbox.setAttribute("InboxType", NWCGAAConstants.INBOX_TYPE);
	            el_Inbox.setAttribute("Priority", NWCGAAConstants.PRIORITY);
	            el_Inbox.setAttribute("QueueId", NWCGAAConstants.QUEUEID_FAULT);
	            el_Inbox.setAttribute("DetailDescription", soapFault.getFaultString());
	            
	            Element eleInboxList = rt_Inbox.createElement("InboxReferencesList");
	            el_Inbox.appendChild(eleInboxList);
	            
				Element eleInboxReferencesCode = rt_Inbox.createElement("InboxReferences");
				if(soapFault.getFaultCode() != null)
					eleInboxList.appendChild(eleInboxReferencesCode);
				
				eleInboxReferencesCode.setAttribute("Name", "FaultCode");
				eleInboxReferencesCode.setAttribute("ReferenceType", NWCGAAConstants.REFERENCE_TYPE);
				eleInboxReferencesCode.setAttribute("Value", soapFault.getFaultCode());
				
				Element eleInboxReferenceFaultString = rt_Inbox.createElement("InboxReferences");
				if(soapFault.getFaultString() != null)
					eleInboxList.appendChild(eleInboxReferenceFaultString);
				
				eleInboxReferenceFaultString.setAttribute("Name", "FaultString");
				eleInboxReferenceFaultString.setAttribute("ReferenceType", NWCGAAConstants.REFERENCE_TYPE);
				eleInboxReferenceFaultString.setAttribute("Value", soapFault.getFaultString());
				
				Element eleInboxReference1 = rt_Inbox.createElement("InboxReferences");
				
				if(soapFault.getFaultActor() != null)
					eleInboxList.appendChild(eleInboxReference1);
				
				eleInboxReference1.setAttribute("Name", "FaultActor");
				eleInboxReference1.setAttribute("ReferenceType", NWCGAAConstants.REFERENCE_TYPE);
				eleInboxReference1.setAttribute("Value", soapFault.getFaultActor());
				
				YFSEnvironment  env = CommonUtilities.createEnvironment(NWCGAAConstants.ALERT_USERID, NWCGAAConstants.ALERT_PROGID);
				CommonUtilities.invokeAPI(env, NWCGAAConstants.API_CREATE_EXCEPTION, rt_Inbox);
			}
		} 
		catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException thrown while parsing ==> "+ e,e);
			e.printStackTrace();
		}
		catch(Exception e){
			logger.error("Exception thrown ==> "+ e,e);
			e.printStackTrace();
		}		
		return outDoc;
	}
	
	/**
	 *@author sdas
	 *
	 *@param queueID - ALERT QUEUE ID
	 *@param inDoc - Document which can be put as a reference
	 *@param errDesc - ERROR DESCRIPTION
	 *@param e - Throwable object
	 *@param map - Map object of reference name value pairs.
	 *
	 */
	public static void raiseAlert(YFSEnvironment env,String queueID, String errDesc, Document inDoc, Throwable e,Map map) {
        //boolean addAllAttributesAsRef = false;
       
        try {
            Document rt_Inbox = XMLUtil.newDocument();
            Element el_Inbox = rt_Inbox.createElement("Inbox");
            rt_Inbox.appendChild(el_Inbox);

            if (errDesc.length() > 100){
                el_Inbox.setAttribute(NWCGConstants.ALERT_DESC, errDesc.substring(0, 98));
            }
            else {
                el_Inbox.setAttribute(NWCGConstants.ALERT_DESC, errDesc);
            }
            el_Inbox.setAttribute("EnterpriseKey", NWCGConstants.ENTERPRISE_CODE);
            el_Inbox.setAttribute("InboxType", "ALERT");
            el_Inbox.setAttribute("Priority", "1");
            el_Inbox.setAttribute("QueueId", queueID);
            
            if (null != e) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                String expTrace = writer.getBuffer().substring(0, 1000);
                el_Inbox.setAttribute("DetailDescription", expTrace);
            } else {
            	if (map != null){
                	String detailDesc = (String) map.get(NWCGConstants.ALERT_DESC);
                    el_Inbox.setAttribute("DetailDescription", detailDesc);
            	}
            	else {
            		el_Inbox.setAttribute("DetailDescription", errDesc);
            	}
            }
            
            if (map != null){
            	if (map.containsKey(NWCGConstants.ITEM_ID)){
            		el_Inbox.setAttribute(NWCGConstants.ITEM_ID, (String) map.get(NWCGConstants.ITEM_ID));
            		map.remove(NWCGConstants.ITEM_ID);
            	}
            	
            	if (map.containsKey("AlertType")){
            		el_Inbox.setAttribute("ExceptionType", (String) map.get("AlertType"));
            		map.remove("AlertType");
            	}
            	//<gaurav> populate ship node at the header level of alert
            	if (map.containsKey(NWCGConstants.ALERT_SHIPNODE_KEY)){
            		el_Inbox.setAttribute("ShipnodeKey", (String) map.get(NWCGConstants.ALERT_SHIPNODE_KEY));
            		
            	}
            	
            }

            Element el_InboxReferencesList = rt_Inbox.createElement("InboxReferencesList");
            el_Inbox.appendChild(el_InboxReferencesList);
            
            if(null != map){
            	Iterator k = map.keySet().iterator();
                while (k.hasNext()) {
                  String key = (String) k.next();
                  if (logger.isVerboseEnabled())logger.verbose("Key " + key + "; Value " + (String) map.get(key));
                  Element el_InboxRef = rt_Inbox.createElement("InboxReferences");
                  el_InboxReferencesList.appendChild(el_InboxRef);
                  el_InboxRef.setAttribute("Name",key);
                  el_InboxRef.setAttribute("ReferenceType","TEXT");
                  el_InboxRef.setAttribute("Value",(String) map.get(key));
                }
            }           
            CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION, rt_Inbox);
            
        } catch (Exception e1) {
        	logger.error("Exception thrown while calling createException API",e1);
            e1.printStackTrace();  
        }
    }
	
	public static void raiseAlertAndAssigntoUser(YFSEnvironment env,String queueID, 
			String errDesc, String userId, Document inDoc, Map map) {
		raiseAlertAndAssigntoUser(env, queueID, errDesc, userId, inDoc, null, map);
	}
	
	/**
	 * Creates an alert in the given queueID and assigns the alert
	 * to the given userId. This method no longer displays the first 2000
	 * characters of the given inDoc as an inbox reference.
	 * 
	 *@author drodriguez
	 *
	 *@param queueID - ALERT QUEUE ID
	 *@param inDoc - Document which can be put as a reference
	 *@param errDesc - ERROR DESCRIPTION
	 *@param userId - User Id to assign alert to
	 *@param map - Map object of reference name value pairs.
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void raiseAlertAndAssigntoUser(YFSEnvironment env,String queueID, 
			String errDesc, String userId, Document inDoc, Throwable e, Map map) {
       
        try {
            Document rt_Inbox = XMLUtil.newDocument();
            Element el_Inbox = rt_Inbox.createElement("Inbox");
            rt_Inbox.appendChild(el_Inbox);

            el_Inbox.setAttribute("EnterpriseKey", NWCGConstants.ENTERPRISE_CODE);
            el_Inbox.setAttribute("InboxType", "ALERT");
            el_Inbox.setAttribute("Priority", "1");
            el_Inbox.setAttribute("QueueId", queueID);
            el_Inbox.setAttribute("ExceptionType", (String) map.get(NWCGAAConstants.NAME));
            
            if (e != null) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                String expTrace = writer.getBuffer().substring(0, 1000);
                el_Inbox.setAttribute("DetailDescription", expTrace);
            } else {
            	el_Inbox.setAttribute("DetailDescription", errDesc);
            }
            
            if (map != null) {
	            if (map.containsKey(NWCGConstants.ALERT_SHIPNODE_KEY)) {
	            	String shipNodekey = (String) map.get(NWCGConstants.ALERT_SHIPNODE_KEY);
	            	el_Inbox.setAttribute("ShipnodeKey", shipNodekey);
	            	//map.remove(NWCGConstants.ALERT_SHIPNODE_KEY);
	            }       
	            
	            if (map.containsKey(NWCGConstants.ORDER_NO)) {
	            	String orderNo = (String) map.get(NWCGConstants.ORDER_NO);
	            	el_Inbox.setAttribute(NWCGConstants.ORDER_NO, orderNo);
	            	map.remove(NWCGConstants.ORDER_NO);
	            }
	            
	            if (map.containsKey(NWCGConstants.ORDER_HEADER_KEY)) {
	            	String ohk = (String) map.get(NWCGConstants.ORDER_HEADER_KEY);
	            	el_Inbox.setAttribute(NWCGConstants.ORDER_HEADER_KEY, ohk);
	            	map.remove(NWCGConstants.ORDER_HEADER_KEY);
	            }
	                        
	            if (map.containsKey(NWCGConstants.ALERT_DESC)){
	            	String desc = (String) map.get(NWCGConstants.ALERT_DESC);
	            	if (desc != null && desc.length() > 100){
	            		el_Inbox.setAttribute(NWCGConstants.ALERT_DESC, desc.substring(0, 98));
	            	}
	            	else {
		            	el_Inbox.setAttribute(NWCGConstants.ALERT_DESC, desc);
	            	}
	            	map.remove(NWCGConstants.ALERT_DESC);
	            }
	            else {
	                if (errDesc.length() > 100){
	                	String desc = errDesc.substring(0, 98);
	                    el_Inbox.setAttribute(NWCGConstants.ALERT_DESC, desc);
	                }
	                else {
	                	el_Inbox.setAttribute(NWCGConstants.ALERT_DESC, errDesc);
	                }
	            }
            }
            Element el_InboxReferencesList = rt_Inbox.createElement("InboxReferencesList");
            el_Inbox.appendChild(el_InboxReferencesList);
            
            if(map != null){
            	Iterator k = map.keySet().iterator();
                while (k.hasNext()) {
                  String key = (String) k.next();
                  if (logger.isVerboseEnabled())logger.verbose("Key " + key + "; Value " + (String) map.get(key));
                  Element el_InboxRef = rt_Inbox.createElement("InboxReferences");
                  el_InboxReferencesList.appendChild(el_InboxRef);
                  el_InboxRef.setAttribute("Name",key);
                  String value = (String) map.get(key);
                  
                  if (key.startsWith("Issue Detail Link")) {
                	  el_InboxRef.setAttribute("ReferenceType","URL");
                	  String htmlText = "javascript:openAlertLink('ISUorder.detail','Order','OrderHeaderKey','" + value + "');";
                	  el_InboxRef.setAttribute("Value", htmlText);
                  }
                  else if (key.equalsIgnoreCase(NWCGConstants.INCIDENT_KEY)) {
                	  el_InboxRef.setAttribute("ReferenceType","URL");
                	  String htmlText = "javascript:openAlertLink('NWCGIncident.detail','NWCGIncidentOrder','IncidentKey','" + value + "');";
                	  el_InboxRef.setAttribute("Name", "Incident Detail Link");
                	  el_InboxRef.setAttribute("Value", htmlText);
                  }
                  else if (key.equalsIgnoreCase(NWCGConstants.TO_INCIDENTKEY)) {
                	  el_InboxRef.setAttribute("ReferenceType","URL");
                	  String htmlText = "javascript:openAlertLink('NWCGIncident.detail','NWCGIncidentOrder','IncidentKey','" + value + "');";
                	  el_InboxRef.setAttribute("Name", "Destination Incident Link");
                	  el_InboxRef.setAttribute("Value", htmlText);
                  }
                  else if (key.equalsIgnoreCase(NWCGConstants.FROM_INCIDENTKEY)) {
                	  el_InboxRef.setAttribute("ReferenceType","URL");
                	  String htmlText = "javascript:openAlertLink('NWCGIncident.detail','NWCGIncidentOrder','IncidentKey','" + value + "');";
                	  el_InboxRef.setAttribute("Name", "Source Incident Link");
                	  el_InboxRef.setAttribute("Value", htmlText);
                  }
                  else if (key.equalsIgnoreCase("Incident To Incident Transfer Order Link")) {
                	  el_InboxRef.setAttribute("ReferenceType","URL");
                	  String htmlText = "javascript:openAlertLink('NWTorder.detail','Order','OrderHeaderKey','" + value + "');";
                	  el_InboxRef.setAttribute("Name", "Incident to Incident Transfer Link");
                	  el_InboxRef.setAttribute("Value", htmlText);
                  }
                  else if (key.equalsIgnoreCase("Comment1")) {
                	  el_InboxRef.setAttribute("Name", "Comment1");
                	  el_InboxRef.setAttribute("ReferenceType", "COMMENT");
                	  el_InboxRef.setAttribute("Value", value);
                  }
                  else {
                	  el_InboxRef.setAttribute("ReferenceType","TEXT");
                	  el_InboxRef.setAttribute("Value",(String) map.get(key));    
                  }  
                }
            }         
            
            if (!StringUtil.isEmpty(userId)) {
            	el_Inbox.setAttribute("AssignedToUserId", userId);
            }   

            String createExceptionInput = XMLUtil.extractStringFromDocument(rt_Inbox);
            logger.verbose("Input XML to createException: \n"+createExceptionInput);                        
            
            Document createExceptionOutput = CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION, rt_Inbox);         

            String createExceptionOutputStr = XMLUtil.extractStringFromDocument(createExceptionOutput);
            logger.verbose("Output XML from createException: \n"+createExceptionOutputStr);            	
            
            if (createExceptionOutput != null && 
            		createExceptionOutput.getDocumentElement() != null) {
            	map.put("NewIncidentInboxKey", createExceptionOutput.getDocumentElement().getAttribute("InboxKey"));
            }
        } catch (Exception e1) {
        	logger.error("Exception thrown while calling createException API!", e1);
            e1.printStackTrace(); 
        }
    }
	
	/** 
	 * Determines whether the given shipping cache Id is valid by calling 
	 * the Sterling getOrganizationList API with the 
	 * template/extn/CommonUtilities_getOrganizationList.xml
	 * API output template.
	 * @param cacheId
	 * @return true if the shipping cache is found as an organization, false otherwise.
	 */
	public static boolean isValidShippingCacheId (YFSEnvironment env, String cacheId) {
		boolean retVal = true;
		String getOrganizationListInput = "<Organization OrganizationCode=\""+cacheId+"\"/>";
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeAPI(env, 
					"CommonUtilities_getOrganizationList.xml",
					NWCGConstants.API_GET_ORG_LIST, getOrganizationListInput);
		}
		catch (Exception e) {
			logger.error("Exception thrown while calling getOrganizationList!", e);
			e.printStackTrace();
			return false;
		}		
		if (apiOutputDoc == null) {
			return false;
		}
		
		NodeList nl = apiOutputDoc.getDocumentElement().getElementsByTagName(NWCGConstants.ORGANIZATION);
		if (nl.getLength() == 0) {
			retVal = false;
		}
		return retVal;
	}
	
    /**
	 * Gets the current time in XML format "yyyy-MM-dd'T'HH:mm:ss"
	 * for the YFSEnvironment's current user ID's time zone.
	 */
    public static String getXMLCurrentTime()
	{
		return formatDate( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Calendar.getInstance().getTime() );
	}
    
    /**
	 * Formats the input date(java.util.Date) in the given output format
     * @param outFormat Desired output format of the Date
     * @param dte Date to be formatted to output format
     * @return Date in the output format specified
	 */
    public static String formatDate( String outFormat, java.util.Date dte )
    {
        SimpleDateFormat formatter  = new SimpleDateFormat( outFormat );
        return formatter.format( dte );
    }
    
	/**
	 *  Retrieves the system administrator's user ID associated to the 
	 *  given Shipping Cache ID by calling getCommonCodeList()with 
	 *  common code type NWCG_CACHE_ADMS
	 *  
	 *  @param YFSEnvironment
	 *  @param shippingCacheId
	 *  @return userId 	That the an alert should be assigned to based on the 
	 * 					incident's primary cache ID. If all fails, return empty
	 * 					string. Defaults to "yantra" user, on any error.
	 */
	public static String getAdminUserForCache (YFSEnvironment env, String shippingCacheId) {
		String shippingCacheUserAdmin = NWCGConstants.EMPTY_STRING;
		if (shippingCacheId == null || shippingCacheId.length() < 2){
			return shippingCacheUserAdmin;
		}
		else {
			shippingCacheUserAdmin = "jbilliard";
		}
		try {
			Document doc = XMLUtil.createDocument("CommonCode");
			doc.getDocumentElement().setAttribute("CodeType","NWCG_CACHE_ADMS");
			doc.getDocumentElement().setAttribute("CodeValue",shippingCacheId);
			Document outDoc = CommonUtilities.invokeAPI(env,"getCommonCodeList",doc);
			
			XPathWrapper pathWrapper = new XPathWrapper(outDoc);
			shippingCacheUserAdmin = pathWrapper.getAttribute("/CommonCodeList/CommonCode/@CodeShortDescription");
		}
		catch (Exception e){}
		
		return shippingCacheUserAdmin;
	}
	
	/**
	 *  Retrieves an integer representing the maximum number of request lines
	 *  that ICBSR will allow per PlaceResourceRequestExternalReq message
	 *  
	 *  @param YFSEnvironment
	 *  @return maximumLines
	 */
	public static int getMaximumRequestLines (YFSEnvironment env) {
		String maximumLines = NWCGConstants.EMPTY_STRING;
		int retVal = 50;
		
		try {
			Document apiInputDoc = XMLUtil.createDocument("CommonCode");
			apiInputDoc.getDocumentElement().setAttribute("CodeType","NWCG_PLACEREQST");
			apiInputDoc.getDocumentElement().setAttribute("CodeValue","MAX_RQSTS_PER_MSG");
			Document outDoc = CommonUtilities.invokeAPI(env, "getCommonCodeList", apiInputDoc);
			
			XPathWrapper pathWrapper = new XPathWrapper(outDoc);
			maximumLines = pathWrapper.getAttribute("/CommonCodeList/CommonCode/@CodeShortDescription");
		}
		catch (Exception e) {
			logger.error("Failure to retrieve common code NWCG_PLACEREQST.MAX_RQSTS_PER_MSG, Returning default of 50");
			return retVal;
		}		
		
		try {
			retVal = Integer.parseInt(maximumLines);
		} catch (NumberFormatException e) {
			logger.error("Code Type: NWCG_PLACEREQST Code Value: MAX_RQSTS_PER_MSG not configured properly");
			e.printStackTrace();			
		}
		
		return retVal;
	}	
	
	/**
	 * This method will call NWCGUpdateIncidentNotificationXSLService XSL to convert
	 * to NWCGIncidentOrder format. It will set the appropriate incident number and will
	 * check for mandatory parameters required for creating incident. It will then call
	 * NWCGCreateIncidentOrderService to create the incident
	 * @param env
	 * @param inDoc
	 * @return
	 */
	public static Document createIncident(YFSEnvironment env, Document createIncidentDoc){
		Document createIncDocAfterXSL = null;
		try {
			Element inDocElm = createIncidentDoc.getDocumentElement();
			
			//Extract out the extended customer attributes into a HashMap for later use in the
			//NWCGCreateIncidentOrderService input XML
			HashMap <String, String> nwcgIncidentOrderCustomerExtn = new HashMap<String,String>();
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_AGENCY_ATTR, inDocElm.getAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR));					
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, inDocElm.getAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR));
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_CUSTOMER_TYPE, inDocElm.getAttribute(NWCGConstants.INCIDENT_CUSTOMER_TYPE));
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, inDocElm.getAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR));
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_GACC_ATTR, inDocElm.getAttribute(NWCGConstants.INCIDENT_GACC_ATTR));
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_TYPE, inDocElm.getAttribute(NWCGConstants.INCIDENT_TYPE));
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_TEAM_TYPE, inDocElm.getAttribute(NWCGConstants.INCIDENT_TEAM_TYPE));										
			nwcgIncidentOrderCustomerExtn.put(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, inDocElm.getAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR));
			nwcgIncidentOrderCustomerExtn.put("PersonInfoBillToKey", inDocElm.getAttribute("PersonInfoBillToKey"));

			try {
				logger.verbose("CommonUtilities.createIncident() input to NWCGUpdateIncidentNotificationXSLService");
				logger.verbose(XMLUtil.extractStringFromDocument(createIncidentDoc));
			}
			catch (Exception e) {
				throw e;
			}
			
			createIncDocAfterXSL = CommonUtilities.invokeService(env, 
					NWCGConstants.SVC_UPDT_INCIDENT_NOTIF_XSL_SVC, createIncidentDoc); 	
			
			// Set correct incident no with prefix 0s for number
			String dbgNWCGIncDoc = XMLUtil.extractStringFromDocument(createIncDocAfterXSL);
			logger.verbose("______________Output of NWCGUpdateIncidentNotificationXSLService________");
			logger.verbose(dbgNWCGIncDoc);			
			
			Element createIncDocElmAfterXSL = createIncDocAfterXSL.getDocumentElement();
			String incidentNo = createIncDocElmAfterXSL.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
			int lastIndex = incidentNo.lastIndexOf(NWCGConstants.DASH);
			String seqNum = incidentNo.substring(lastIndex+1);
			seqNum = StringUtil.prepadStringWithZeros(seqNum, 6);
			incidentNo = incidentNo.substring(0, lastIndex+1).concat(seqNum);
			createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incidentNo);
			createIncDocElmAfterXSL.setAttribute(NWCGConstants.MODIFICATION_CODE, "Auto Update From ROSS");
			createIncDocElmAfterXSL.setAttribute(NWCGConstants.MODIFICATION_DESC, "Created: "+CommonUtilities.getXMLCurrentTime());
			
			Set<String> incCustExtnAttribs = nwcgIncidentOrderCustomerExtn.keySet();
			Iterator <String> custExtnAttribNames = incCustExtnAttribs.iterator();
			
			while (custExtnAttribNames.hasNext()) {
				String currKey = custExtnAttribNames.next();
				if (currKey.equals(NWCGConstants.INCIDENT_AGENCY_ATTR)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR, nwcgIncidentOrderCustomerExtn.get(currKey)); 
				} else if (currKey.equals(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, nwcgIncidentOrderCustomerExtn.get(currKey));
				} else if (currKey.equals(NWCGConstants.INCIDENT_DEPARTMENT_ATTR)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, nwcgIncidentOrderCustomerExtn.get(currKey));
				} else if (currKey.equals(NWCGConstants.INCIDENT_GACC_ATTR)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_GACC_ATTR, nwcgIncidentOrderCustomerExtn.get(currKey));
				} else if (currKey.equals(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, nwcgIncidentOrderCustomerExtn.get(currKey));
				} else if (currKey.equals(NWCGConstants.INCIDENT_CUSTOMER_TYPE)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_TYPE, nwcgIncidentOrderCustomerExtn.get(currKey));
				} else if (currKey.equals(NWCGConstants.INCIDENT_TYPE)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_TYPE, nwcgIncidentOrderCustomerExtn.get(currKey));
				} else if (currKey.equals(NWCGConstants.INCIDENT_TEAM_TYPE)) {
					createIncDocElmAfterXSL.setAttribute(NWCGConstants.INCIDENT_TEAM_TYPE, nwcgIncidentOrderCustomerExtn.get(currKey));	
				} else if (currKey.equals("PersonInfoBillToKey")) {
					createIncDocElmAfterXSL.setAttribute("PersonInfoBillToKey", nwcgIncidentOrderCustomerExtn.get(currKey));
				}
			}
			
			//Set LastUpdatedFromROSS
			String lastUpdtFromROSS = createIncDocElmAfterXSL.getAttribute(NWCGConstants.LAST_UPDATED_FROM_ROSS);
			if (lastUpdtFromROSS == null || lastUpdtFromROSS.trim().length() < 2){
				createIncDocElmAfterXSL.setAttribute(NWCGConstants.LAST_UPDATED_FROM_ROSS, CommonUtilities.getXMLCurrentTime());
			}
			
			// Check for mandatory params
			String primCacheId = createIncDocElmAfterXSL.getAttribute(NWCGConstants.PRIMARY_CACHE_ID);
			if (primCacheId == null || primCacheId.length() < 2){
				logger.verbose("Primary Cache ID is null in input xml. Not creating the incident");
			}
			
			// Set PersonInfoDeliverToKey as the empty "NWCG" person info record
			// that's been associated to all incidents since BR1.
			createIncDocElmAfterXSL.setAttribute("PersonInfoDeliverToKey", "20080428145515145359");
			
			dbgNWCGIncDoc = XMLUtil.extractStringFromDocument(createIncDocAfterXSL);
			logger.verbose("______________Input to NWCGCreateIncidentOrderService________");
			logger.verbose(dbgNWCGIncDoc);			
			
			// Call the NWCGCreateIncidentOrderService within the General Repository
			CommonUtilities.invokeService(env, NWCGConstants.SVC_CREATE_INCIDENT_ORDER_SVC, createIncDocAfterXSL);
		}
		catch (Exception e){
			logger.error("CommonUtilities::createIncident, Failed while creating incident",e);
			e.printStackTrace();
			return createIncidentDoc;
		}		

		try {
			logger.verbose("createIncident returning: "+XMLUtil.extractStringFromDocument(createIncDocAfterXSL));
		} catch (TransformerException e) {
			e.printStackTrace();
		}		
		return createIncDocAfterXSL;
	}

	/**
	 * This method checks if primary financial code exists in input xml. If it does not
	 * exists, then it returns a false boolean value
	 * @param env
	 * @param doc
	 * @return boolean True if a primary financial code exists in the IncidentEntities
	 * 					element where the PrimaryInd node has a value of "true" (ignoring case)
	 */
	public static boolean checkPrimaryFinCodeExistence(YFSEnvironment env, Document doc){
		Element rootElm = doc.getDocumentElement();
		// Check primary financial code
		//	UpdateIncidentNotification/Incident/IncidentEntities/IncidentFinancialCodes/FinancialCode/PrimaryInd 
		//	is not NULL and equals TRUE
		boolean obtPrimIndVal = false; // This flag is used to stop recursive calls to IncidentFinancialCodes
		String primaryIndVal = NWCGConstants.EMPTY_STRING;
		NodeList primaryFinCodeNL = rootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_FINANCIAL_CODES_ELEMENT);
		if (primaryFinCodeNL == null || (primaryFinCodeNL.getLength() < 1)){
			logger.error("ICBS could not find any ROSS Financial Codes!");
			return false;
		}
		
		for (int i=0; i < primaryFinCodeNL.getLength() && !obtPrimIndVal; i++){
			Node primaryFinCodeNode = primaryFinCodeNL.item(i);
			NodeList childNL = primaryFinCodeNode.getChildNodes();
			// childNL cannot be NULL as the schema mandates it to have child elements, 
			// so not checking childNL nullability
			for (int j=0; j < childNL.getLength() && !obtPrimIndVal; j++){
				Node primaryIndNode = childNL.item(j);
				// If primary indicator is not true, then the code will loop through the other
				// financial codes until it gets exhausted or a primary indicator value of true
				if (primaryIndNode.getNodeName().equalsIgnoreCase(NWCGConstants.INC_NOTIF_PRIMARYIND_NODE)) {
					primaryIndVal = primaryIndNode.getTextContent();
					if (primaryIndVal.equalsIgnoreCase(NWCGConstants.STR_TRUE)){
						obtPrimIndVal = true;
					}
				}
			} // end of for (int j=0; j < childNL.getLength() && !obtPrimIndVal; j++){
		} // end of for (int i=0; i < primaryFinCodeNL.getLength() && !obtPrimIndVal; i++){

		return obtPrimIndVal;
	}		
	
	/**
	 * Make getOrderLineList with the below input template 
	 * <OrderLine> 
	 *  <Extn ExtnRequestNoQryType="FLIKE" ExtnRequestNo="S-100511"/> 
	 *  <Order> 
	 *   <Extn ExtnIncidentNo="" ExtnIncidentYear="" /> 
	 *  </Order> 
	 * </OrderLine>
	 * 
	 * @param reqNo
	 * @return Document getOrderLineList output XML based on the given template
	 *         specified on parameter opTemplateName
	 */
	public static Document getOLsForABaseReqInAnInc(YFSEnvironment env, 
			String incNo, String incYr, String reqNo, String opTemplateName) {
		Document docGetOLListOP = null;
		try {
			Document docGetOLListIP = XMLUtil.createDocument(NWCGConstants.ORDER_LINE);
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			Element elmOLExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute("ExtnRequestNoQryType", "FLIKE");
			elmOLExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, reqNo);
			Element elmOrder = docGetOLListIP.createElement(NWCGConstants.ORDER_ELM);
			elmGetOL.appendChild(elmOrder);
			Element elmOrderExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmOrder.appendChild(elmOrderExtn);
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_NO, incNo);
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_YEAR, incYr);


			logger.verbose("CommonUtilities::getOLsForABaseReqInAnInc, " +
								   "INPUT XML : " + XMLUtil.extractStringFromDocument(docGetOLListIP));
			
			docGetOLListOP = CommonUtilities.invokeAPI(env, opTemplateName, 
											NWCGConstants.API_GET_ORDER_LINE_LIST, docGetOLListIP);			

			logger.verbose("CommonUtilities::getOLsForABaseReqInAnInc, " +
								   "OUTPUT XML : " + XMLUtil.extractStringFromDocument(docGetOLListOP));

		}
		catch(ParserConfigurationException pce) {
			logger.error("CommonUtilities.getOLsForABaseReqInAnInc(), " + 
						"Parser Configuration Exception : " + pce.getMessage());

		}
		catch(Exception e){
			logger.error("CommonUtilities.getOLsForABaseReqInAnInc(), " + 
							"Exception : " + e.getMessage());
		}
		return docGetOLListOP;
	}	
	
	private static Document getNodeInventoryInputDoc(String shipNode,
			String strInventoryStatus, String strItemID) {
		String getNodeInventoryInputXmlString = "<NodeInventory Node=\""
				+ shipNode + "\"><Inventory InventoryStatus=\""
				+ strInventoryStatus + "\"><InventoryItem ItemID=\""
				+ strItemID + "\"/></Inventory></NodeInventory>";
		Document getNodeInventoryDoc = null;
		try {
			getNodeInventoryDoc = XMLUtil
					.getDocument(getNodeInventoryInputXmlString);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getNodeInventoryDoc;
	}

	private static Document getATPInputDoc(String shipNode, String strItemID,
			String strPC, String strUOM, String strOrganizationCode) {
		String getATPXmlString = "<GetATP  ShipNode=\"" + shipNode
				+ "\" ItemID=\"" + strItemID + "\" ProductClass=\"" + strPC
				+ "\" UnitOfMeasure=\"" + strUOM + "\" OrganizationCode=\""
				+ strOrganizationCode + "\" />";
		Document getATPDoc = null;
		try {
			getATPDoc = XMLUtil.getDocument(getATPXmlString);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getATPDoc;
	}
	
   public static String[] getAddressLines(String ShippingInsStr)
   {
	   String AddrArray[] = new String[25];

	   int cnt = 0;
	   int ncnt = 0;
	   int ecnt = 0;
	   int addrlen = ShippingInsStr.length();
	   String Addr1 = "";
	   
	   if (addrlen > 70)
	   {
	   	  for(int j=0; j<=addrlen-70; j+=70)
	       { 
	         Addr1 = ShippingInsStr.substring(j,j+70);
	         Addr1 = removeNewRow(Addr1);
	         AddrArray[cnt] = Addr1;
	         cnt++;
	         Addr1 = "";
	       }
	   	   ncnt = cnt*70;
	   	   ecnt = addrlen - ncnt;
	       if (ecnt > 0)
	       {
	        Addr1 = ShippingInsStr.substring(ncnt); 
	        Addr1 = removeNewRow(Addr1);
	        AddrArray[cnt] = Addr1;
	       }
	    }
	    else
	    {
	    	ShippingInsStr = removeNewRow(ShippingInsStr);
	    	AddrArray[cnt] = ShippingInsStr;
	    }

     return AddrArray;
   }
   
   // This method is used to remove blank spaces due to press of enter key while entering instructions or notes.
   public static String removeNewRow(String strText)
   {
	   String returnSuffix = "\r\n"; 
	   int suffixIndex = strText.lastIndexOf( returnSuffix );
		while( suffixIndex != -1 ) {
			strText = strText.substring(0,suffixIndex) + " " + strText.substring(suffixIndex+2, strText.length());
			suffixIndex = strText.lastIndexOf( returnSuffix ); // check again for CR and LF
		}
		
		return strText;
	   
   }
   
   public static String getShipNodeFromOrderNo(YFSEnvironment env,String OrderNo)
   {
	   String strShipNode="";
	   try{
		   	Document docGetOrderDetailinput = XMLUtil.createDocument("Order");
			Element eleOrder = docGetOrderDetailinput.getDocumentElement();
			eleOrder.setAttribute("EnterpriseCode", "NWCG");
			eleOrder.setAttribute("OrderNo", OrderNo);
			
			
			Document docGetrOrderDetailTemplate = XMLUtil.createDocument("Order");
			Element eleTempOrder = docGetrOrderDetailTemplate.getDocumentElement();
			eleTempOrder.setAttribute("ShipNode", "");
			
			Document docOutput=CommonUtilities.invokeAPI(env, docGetrOrderDetailTemplate, "getOrderDetails", docGetOrderDetailinput);
			if(docOutput != null)
			{
				Element eleOutputRoot = docOutput.getDocumentElement();
				String strTmp = eleOutputRoot.getAttribute("ShipNode");
				if(strTmp != null && !strTmp.trim().equals(""))
				{
					strShipNode = strTmp;
				}
			}
	   }
	   catch (Exception e) {
			logger.error("getShipNodeFromOrderNo", e);	
	   }
	   
		return strShipNode;
   }
   
   public static String getShipNodeFromIncident(YFSEnvironment env,String IncidentNo ,String Year)
   {
	   	String strShipNode="";
	   	try{
		   	Document docGetIncOrderDetailinput = XMLUtil.createDocument("NWCGIncidentOrder");
			Element eleOrder = docGetIncOrderDetailinput.getDocumentElement();
			eleOrder.setAttribute("IncidentNo", IncidentNo);
			eleOrder.setAttribute("Year", Year);
			
			Document docOutput=CommonUtilities.invokeService(env, "NWCGGetIncidentOrderOnlyService", docGetIncOrderDetailinput);
			if(docOutput != null)
			{
				Element eleOutputRoot = docOutput.getDocumentElement();
				String strTmp = eleOutputRoot.getAttribute("PrimaryCacheId");
				if(strTmp != null && !strTmp.trim().equals(""))
				{
					strShipNode = strTmp;
				}
			}
	   	}
	    catch (Exception e) {
			logger.error("getShipNodeFromIncident", e);	
	   }
		return strShipNode;
   }
   public static String getShipNodeFromDistID(YFSEnvironment env, String distId) throws Exception{
		String strShipNode="";
	   try {
			Document msgStoreIPDoc = XMLUtil.createDocument("NWCGOutboundMessage");
			Element msgStoreIPDocElm = msgStoreIPDoc.getDocumentElement();
			msgStoreIPDocElm.setAttribute(NWCGAAConstants.DIST_ID_ATTR, distId);
			Document msgStoreOPListDoc = CommonUtilities.invokeService(
					env, NWCGAAConstants.GET_OB_MESSAGE_LIST_SERVICE, msgStoreIPDoc); 
			if (msgStoreOPListDoc != null){
				NodeList obMsgNL = msgStoreOPListDoc.getElementsByTagName("NWCGOutboundMessage");
				if (obMsgNL != null && obMsgNL.getLength() > 0){
					Element obMsgStoreElm = (Element) obMsgNL.item(0);
					String entityKey = obMsgStoreElm.getAttribute(NWCGAAConstants.ENTITY_KEY);
					if (entityKey != null && entityKey.length() > 2){
						strShipNode = getShipNodeFromIncidentKey(env, entityKey);
					}
					else {
						NWCGLoggerUtil.Log.warning("NWCGOBRegisterIncidentProcessorHandler::process, " +
								"Incident Key (Entity Key) is not set for distribution id : " + distId);
					}
				}
				else {
					NWCGLoggerUtil.Log.warning("NWCGOBRegisterIncidentProcessorHandler::process, " +
							"There are no entries for distribution id : " + distId);
				}
			}
			else {
				NWCGLoggerUtil.Log.warning("NWCGOBRegisterIncidentProcessorHandler::process, " +
						NWCGAAConstants.GET_OB_MESSAGE_LIST_SERVICE + " service call failed");
			}
		}
		catch(ParserConfigurationException pce){
			NWCGLoggerUtil.Log.warning("NWCGOBRegisterIncidentProcessorHandler::process, " +
					"ParserConfigurationException Message : " + pce.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(pce);
		}
		return strShipNode;
	}
   
   public static String getShipNodeFromIncidentKey(YFSEnvironment env,String IncidentKey)
   {
	   	String strShipNode="";
	   	try{
		   	Document docGetIncOrderDetailinput = XMLUtil.createDocument("NWCGIncidentOrder");
			Element eleOrder = docGetIncOrderDetailinput.getDocumentElement();
			eleOrder.setAttribute("IncidentKey", IncidentKey);
			
			Document docOutput=CommonUtilities.invokeService(env, "NWCGGetIncidentOrderOnlyService", docGetIncOrderDetailinput);
			if(docOutput != null)
			{
				Element eleOutputRoot = docOutput.getDocumentElement();
				String strTmp = eleOutputRoot.getAttribute("PrimaryCacheId");
				if(strTmp != null && !strTmp.trim().equals(""))
				{
					strShipNode = strTmp;
				}
			}
	   	}
	    catch (Exception e) {
			logger.error("getShipNodeFromIncident", e);	
	   }
		return strShipNode;
   }


   /*
    * This method will get the ship node using incident key
    */
	public static HashMap<String, String> getShipNodeIncidentNoandYearFromKey(YFSEnvironment env, String incidentKey) {
		HashMap<String, String> hmInfo = new HashMap<String, String>();
		try {
			Document docGetIncOrderDetailinput = XMLUtil.createDocument("NWCGIncidentOrder");
			Element eleOrder = docGetIncOrderDetailinput.getDocumentElement();
			eleOrder.setAttribute("IncidentKey", incidentKey);

			Document docOutput = CommonUtilities.invokeService(env,
													"NWCGGetIncidentOrderOnlyService",
													docGetIncOrderDetailinput);
			if (docOutput != null) {
				Element eleOutputRoot = docOutput.getDocumentElement();
				hmInfo.put(NWCGConstants.INCIDENT_NO, eleOutputRoot.getAttribute("IncidentNo"));
				hmInfo.put(NWCGConstants.INCIDENT_YEAR, eleOutputRoot.getAttribute("Year"));
				hmInfo.put(NWCGConstants.PRIMARY_CACHE_ID, eleOutputRoot.getAttribute("PrimaryCacheId"));
			}
			else {
				NWCGLoggerUtil.Log.info("CommonUtilities::setShipNodeIncidentNoandYearFromKey, " +
						"There are no entries for incident key : " + incidentKey);
			}
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("CommonUtilities::setShipNodeIncidentNoandYearFromKey, " +
					"Exception while getting shipnode for incident key : " + incidentKey);
			NWCGLoggerUtil.Log.warning("CommonUtilities::setShipNodeIncidentNoandYearFromKey, " + e.getMessage());
			e.printStackTrace();
		}
		return hmInfo;
	}

	/*
	 * This method will get the ship node based on distribution id
	 */
	public static HashMap<String, String> getShipNodeIncidentNoandYearFromDistID(YFSEnvironment env, String distID){
		HashMap<String, String> hmInfo = new HashMap<String, String>();
		try {
			Document msgStoreIPDoc = XMLUtil.createDocument("NWCGOutboundMessage");
			Element msgStoreIPDocElm = msgStoreIPDoc.getDocumentElement();
			msgStoreIPDocElm.setAttribute(NWCGAAConstants.DIST_ID_ATTR, distID);
			Document msgStoreOPListDoc = CommonUtilities.invokeService(
											env, NWCGAAConstants.GET_OB_MESSAGE_LIST_SERVICE, msgStoreIPDoc); 
			if (msgStoreOPListDoc != null){
				NodeList obMsgNL = msgStoreOPListDoc.getElementsByTagName("NWCGOutboundMessage");
				if (obMsgNL != null && obMsgNL.getLength() > 0){
					Element obMsgStoreElm = (Element) obMsgNL.item(0);
					String entityKey = obMsgStoreElm.getAttribute(NWCGAAConstants.ENTITY_KEY);
					if (entityKey != null && entityKey.length() > 2){
						hmInfo = getShipNodeIncidentNoandYearFromKey(env,entityKey);
					}
				}
			}
			else {
				NWCGLoggerUtil.Log.info("CommonUtilities::setShipNodeIncidentNoandYearFromDistID, " +
						"There is no entry for incident key for " + distID);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return hmInfo;
	}

	public static String convertTimeZone(String dateStr, String localeStr) throws Exception {	
		String newDateStr = "";
		if (dateStr != null && dateStr.length() > 0)
		{		
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			
			if (localeStr.equals("en_US_EST")) {
			  sdf1.setTimeZone(TimeZone.getTimeZone("EST"));
			}
			else if (localeStr.equals("en_US_CST")) {
			  sdf1.setTimeZone(TimeZone.getTimeZone("CST"));
			}
			else if (localeStr.equals("en_US_MST")) {
			  sdf1.setTimeZone(TimeZone.getTimeZone("MST"));
			}
			else if (localeStr.equals("en_US_PST")) {
			  sdf1.setTimeZone(TimeZone.getTimeZone("PST"));
			}
			else if (localeStr.equals("en_US_AST")) {
			  sdf1.setTimeZone(TimeZone.getTimeZone("AST"));
			}
			else {
			  sdf1.setTimeZone(TimeZone.getTimeZone("CST"));
			}
			Date tdate = sdf2.parse(dateStr);
			newDateStr = sdf1.format(tdate);
		}
		return newDateStr;
	}	

	/**
	 * Returns the Owner Agency for the given shipping cache ID by 
	 * calling getOrganizationList API
	 * @param shippingCacheId
	 * @return null if nothing is found at /OrganizationList/Organization/Extn/@ExtnOwnerAgency
	 * 			in the output of getOrganizationList
	 */
	public static String getOwnerAgencyFromShippingCacheID (YFSEnvironment env, String shippingCacheId) {
		String getOrganizationListInput = "<Organization OrganizationCode=\""+shippingCacheId+"\"/>";
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeAPI(env, 
					"CommonUtilities_getOrganizationList.xml",
					NWCGConstants.API_GET_ORG_LIST, getOrganizationListInput);
		}
		catch (Exception e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
			return NWCGConstants.EMPTY_STRING;
		}		
		if (apiOutputDoc == null) {
			return NWCGConstants.EMPTY_STRING;
		}
		XPathWrapper pathWrapper = new XPathWrapper(apiOutputDoc);
		String extnOwnerAgency = NWCGConstants.EMPTY_STRING;
		try {
			extnOwnerAgency = pathWrapper.getAttribute("/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
		}
		catch (Exception e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
        
        return extnOwnerAgency;
	}
	
	/**
	 * This method will get the order details based on order header key and template name
	 * @param env
	 * @param strOrderHdrKey
	 * @return
	 * @throws Exception
	 */
	public static Document getOrderDetails(YFSEnvironment env, String strOrderHdrKey, String templateName) throws Exception{
		Document docOrderDtlsOP = null;
		try {
			Document docOrderDtlsIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + strOrderHdrKey + "\"/>");
			docOrderDtlsOP = CommonUtilities.invokeAPI(env, templateName, 
													   NWCGConstants.API_GET_ORDER_DETAILS, docOrderDtlsIP);
		}
		catch(ParserConfigurationException pce){
			logger.error("CommonUtilities::getOrderDetails, ParserConfigurationException : " + pce.getMessage());
			pce.printStackTrace();
			throw pce;
		} catch (SAXException e) {
			logger.error("CommonUtilities::getOrderDetails, SAXException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			logger.error("CommonUtilities::getOrderDetails, IOException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			logger.error("CommonUtilities::getOrderDetails, Exception : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return docOrderDtlsOP;
	}	
	
}