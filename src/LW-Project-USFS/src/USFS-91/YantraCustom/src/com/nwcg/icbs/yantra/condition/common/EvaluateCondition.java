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

package com.nwcg.icbs.yantra.condition.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will inspect the input arguments and return true/false based
 * on the condition to be checked. <br>
 * <br>
 * CONFIGURATIONS to be done: <br>
 * Entity : Entity for which the condition is to be
 * used(Order,OrderLine,Shipment); <br>
 * Operator : Conditional Operator(AND/OR to be passed only if XPath2 is
 * passed); <br>
 * Template : (Optional)Template for the api called internally for an
 * entity(getOrderDetails: Order; getOrderLineDetails:OrderLine;
 * getShipmentDetails:Shipment)<br>
 * XPath:as key and its expected value as value of that key. <br>
 * 
 * This class supports dynamic condition check for one or two attributes. For
 * two attributes, it supports AND/OR operations;<br>
 * Ex:1)Entity:Order;XPath:/Order/Extn/@SomeExtendedAttributeName;<br>
 * Value of XPath:New(Or any expected value); <br>
 * This class will invoke getOrderDetails and get the value of extended
 * attribute based on the XPath,compare it will Expected value,returns
 * true/false;<br>
 * 2)Entity:OrderLine;XPath:OrderLine/Order/@OrderType;<br>
 * XPath1ExpectedValue:Modify(Or any expected value); XPath2:
 * OrderLine/Extn/@someExtendedFeild ; <br>
 * XPath2ExpectedValue : Delete(Or any expected value);<br>
 * Operator:AND(OR can also be passed); <br>
 * <br>
 * This class will invoke getOrderLineDetails API, fetch the values at the
 * Xpaths, compares both values based on the OPERATOR passed and returns
 * true/false
 * 
 * 
 * @author ajkumar
 */
public class EvaluateCondition implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(EvaluateCondition.class);

	private Map map = null;

	private static String KEY_ENTITY = "Entity";

	private static String KEY_ORDER_LINE = "OrderLine";

	private static String KEY_ORDER = "Order";

	private static String XPATH_VALUE_MAPPING = "XPathValueMapping";

	private static String KEY_SHIPMENT = "Shipment";

	private static String ORDER_LINE_KEY = "OrderLineKey";

	private static String ENTERPRISE_CODE = "EnterpriseCode";

	private static String SHIPMENT_KEY = "ShipmentKey";

	private static String ORDER_HEADER_KEY = "OrderHeaderKey";

	private static String KEY_TEMPLATE = "Template";

	private static String API_ORDERLINE = "getOrderLineDetails";

	private static String API_SHIPMENT = "getShipmentDetails";

	private static String API_ORDER = "getOrderDetails";

	private static String KEY_OPERATOR = "Operator";

	private static String AND_OPERATOR = "AND";

	private static String OR_OPERATOR = "OR";

	private static String CONFIGURED_XPATH_NAME = "XPATH";

	/**
	 * Stores properties configured in configurator.
	 * 
	 * @param map
	 *            Properties configured in Configurator.
	 */
	public void setProperties(Map map) {
		this.map = map;
		// USFSUtilities.logProperties(map, logger);
	}

	/**
	 * Logger Instance.
	 */
	// private static NWCGApplicationLogger logger =
	// NWCGApplicationLogger.instance
	// .getLogger(USFSEvaluateCondition.class.getName());
	/**
	 * Evaluates the dynamic condition based on the attributes passed from the
	 * configurator.
	 * 
	 * @return Returns a boolean depending upon the condition value.
	 * @param env
	 *            Yantra Environment Context.
	 * @param name
	 *            Name of the Dynamic Condition.
	 * @param mapData
	 *            Map contains input XML
	 * @param doc
	 *            Input Document
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document doc) {
		// logger.verbose("Entering USFSDynamicCondition.evaluateCondition()");
		String xPath = null;
		String xPathKeyExpectedValue = null;
		String xPathKeyActualValue = "false";
		String entityName = null;
		Document outputXMLDoc = null;
		// defaulting the template for extended attributes only
		String template = "<OrderLine><Extn/></OrderLine>";
		String operator = null;
		String strXPathValueMapping = null;
		boolean bXPathValueMapping = true;
		boolean result = false;
		int lstSize = 0;
		List lstXPaths = null;
		try {

			// logger.verbose("BEFORE "+template);
			String strTemplate = (String) map.get(KEY_TEMPLATE);
			if (strTemplate != null && (!strTemplate.equals(""))) {
				template = strTemplate;
			}
			// logger.verbose("AFTER "+template);
			entityName = (String) map.get(KEY_ENTITY);
			operator = (String) map.get(KEY_OPERATOR);
			// this flag will identify the behaviour
			// should it be XPath - Value or CONSTANT - XPath ?
			// By default it is set to true, need to pass explictly a false
			// value
			strXPathValueMapping = (String) map.get(XPATH_VALUE_MAPPING);
			// logger.verbose("strXPathValueMapping ==>"+strXPathValueMapping);

			bXPathValueMapping = ((strXPathValueMapping != null && strXPathValueMapping
					.equals("false")) ? false : true);
			// logger.verbose("bXPathValueMapping ==>"+bXPathValueMapping);
			// Getting the XPaths from the Keys of map
			lstXPaths = isKeyXPath(bXPathValueMapping);
			// logger.verbose("lstXPaths ==>"+lstXPaths);
			// Getting the list size
			lstSize = lstXPaths.size();

			// Check for the mandatory attributes
			checkValues(lstSize, entityName, operator);

			// Check for entity type
			if (entityName.equalsIgnoreCase(KEY_ORDER_LINE)) {

				if (template != null) {

					env.setApiTemplate(API_ORDERLINE,
							XMLUtil.getDocument(template));
				}
				// Function to get OrderLineDetails
				outputXMLDoc = getOrderLineDetail(env, mapData, doc);
				// logger.verbose("KEY_ORDER_LINE ===> "+outputXMLDoc);

			} else if (entityName.equalsIgnoreCase(KEY_SHIPMENT)) {

				if (template != null) {

					env.setApiTemplate(API_SHIPMENT,
							XMLUtil.getDocument(template));
				}
				// Function to get Shipment Details
				outputXMLDoc = getShipmentDetail(env, mapData, doc);
				// logger.verbose("KEY_SHIPMENT ===> "+outputXMLDoc);

			} else if (entityName.equalsIgnoreCase(KEY_ORDER)) {

				if (template != null) {

					env.setApiTemplate(API_ORDER, XMLUtil.getDocument(template));
				}
				// Function to get Shipment Details
				outputXMLDoc = getOrderDetail(env, mapData, doc);
				// logger.verbose("KEY_ORDER ===> "+outputXMLDoc);
			}

			for (int lstIndex = 0; lstIndex < lstSize; lstIndex++) {

				// if if is bXPathValueMapping the xpath will be the key
				if (bXPathValueMapping) {
					xPath = (String) lstXPaths.get(lstIndex);
				} else {
					// else it will be a value
					xPath = (String) map.get(lstXPaths.get(lstIndex));
				}
				// logger.verbose("xPath ==>"+xPath);
				// Get the XPath actual value
				Node returnedNode = null;

				if (!bXPathValueMapping) {// if its not XPath value mapping -
											// checking for the nodes if no node
											// returned then the configured
											// value
											// doesnt exists
					returnedNode = XPathUtil.getNode(outputXMLDoc, xPath);
					// logger.verbose("returnedNode ==> "+returnedNode);
					if (returnedNode != null) {
						xPathKeyActualValue = "true";
					}
					xPathKeyExpectedValue = "true";
				} else {
					// else pick up the expected value from the map
					xPathKeyActualValue = XPathUtil.getString(outputXMLDoc,
							xPath);
					// logger.verbose("  xPath == > " +xPath);
					// logger.verbose("  xPathKeyActualValue == > "
					// +xPathKeyActualValue );
					// printDOMTree(outputXMLDoc.getDocumentElement());

					xPathKeyExpectedValue = (String) map.get(lstXPaths
							.get(lstIndex));
				}
				// logger.verbose("  xPathKeyActualValue == > "
				// +xPathKeyActualValue);
				// logger.verbose("  xPathKeyExpectedValue == > "
				// +xPathKeyExpectedValue);
				if (operator != null && operator.length() > 0
						&& xPathKeyActualValue != null
						&& xPathKeyExpectedValue != null) {

					// Compare actual & expected values
					if (operator.equalsIgnoreCase(AND_OPERATOR)) {

						if (xPathKeyActualValue
								.equalsIgnoreCase(xPathKeyExpectedValue)) {
							result = true;
						} else {
							result = false;
							break;
						}

					} else if (operator.equalsIgnoreCase(OR_OPERATOR)) {
						if (xPathKeyActualValue
								.equalsIgnoreCase(xPathKeyExpectedValue)) {
							result = true;
							break;
						}
					}
				} else if (xPathKeyActualValue
						.equalsIgnoreCase(xPathKeyExpectedValue)) {
					result = true;

				}
			}
		} catch (Exception ex) {
			//logger.printStackTrace(ex);
		}
		return result;

	}

	private void checkValues(int lstSize, String entityName, String operator) {

		if (entityName == null || entityName.length() == 0) {

			throw new RuntimeException(
					ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH2"));

		} else if (lstSize == 0) {

			throw new RuntimeException(
					ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH1"));

		} else if (lstSize > 1 && (operator == null || operator.length() == 0)) {

			throw new RuntimeException(
					ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH3"));

		}
	}

	/**
	 * Evaluates the dynamic condition for Order
	 * 
	 * @return Returns a boolean depending upon the condition value.
	 * @param env
	 *            Yantra Environment Context.
	 * @param api
	 *            Instance of YIFApi.
	 * @param mapData
	 *            Map contains input XML
	 * @throws Exception
	 */
	private Document getOrderDetail(YFSEnvironment env, Map mapData,
			Document doc) throws Exception {

		String orderHeaderKey = null;
		String enterpriseCode = null;
		Document inputXMLDoc = null;
		Document outputXMLDoc = null;
		String inputXMLString = null;

		if (mapData != null && mapData.size() > 0) {
			// Get OrderHeaderKey from map entries

			orderHeaderKey = mapData.get(ORDER_HEADER_KEY).toString();
			enterpriseCode = mapData.get(ENTERPRISE_CODE).toString();
			// orderHeaderKey = getKeyValue(mapData, ORDER_HEADER_KEY);
		}

		if (orderHeaderKey == null || orderHeaderKey.length() == 0) {

			throw new RuntimeException(
					ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH4"));

		} else {

			// Input XML for fetching order details
			inputXMLString = "<Order OrderHeaderKey='" + orderHeaderKey
					+ "' EnterpriseCode='" + enterpriseCode + "' />";

			// Parsing the inputXMLString to get document object
			inputXMLDoc = XMLUtil.getDocument(inputXMLString);

			// Calling getOrderDetails
			outputXMLDoc = CommonUtilities.invokeAPI(env, API_ORDER,
					inputXMLDoc);
			// logger.verbose("inputXMLString ==> "+inputXMLString);

			// Clearing the template from environment
			env.clearApiTemplate(API_ORDER);
		}
		return outputXMLDoc;
	}

	/**
	 * Evaluates the dynamic condition for Shipment
	 * 
	 * @return Returns a boolean depending upon the condition value.
	 * @param env
	 *            Yantra Environment Context.
	 * @param api
	 *            Instance of YIFApi.
	 * @param mapData
	 *            Map contains input XML
	 * @throws Exception
	 */
	private Document getShipmentDetail(YFSEnvironment env, Map mapData,
			Document doc) throws Exception {

		String shipmentKey = null;
		Document inputXMLDoc = null;
		Document outputXMLDoc = null;
		String inputXMLString = null;

		// Get ShipmentKey from map entries
		if (mapData != null && mapData.size() > 0) {

			// shipmentKey = getKeyValue(mapData, SHIPMENT_KEY);
			shipmentKey = mapData.get(SHIPMENT_KEY).toString();
		}

		if (shipmentKey == null || shipmentKey.length() == 0) {

			throw new RuntimeException(
					ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH5"));

		} else {

			// Input XML for fetching order details
			inputXMLString = "<Shipment ShipmentKey='" + shipmentKey + "' />";

			// Parsing the inputXMLString to get document object
			inputXMLDoc = XMLUtil.getDocument(inputXMLString);
			// logger.verbose("inputXMLString ==> "+inputXMLString);
			// Calling getShipmentDetails
			outputXMLDoc = CommonUtilities.invokeAPI(env, API_SHIPMENT,
					inputXMLDoc);

			// Clearing the template from environment
			env.clearApiTemplate(API_SHIPMENT);
		}
		return outputXMLDoc;
	}

	/**
	 * Evaluates the dynamic condition for OrderLine
	 * 
	 * @return Returns a boolean depending upon the condition value.
	 * @param env
	 *            Yantra Environment Context.
	 * @param api
	 *            Instance of YIFApi.
	 * @param mapData
	 *            Map contains input XML
	 * @throws Exception
	 */
	private Document getOrderLineDetail(YFSEnvironment env, Map mapData,
			Document doc) throws Exception {

		String orderLineKey = null;
		Document inputXMLDoc = null;
		Document outputXMLDoc = null;
		String inputXMLString = null;

		// Get OrderLineKey from map entries
		if (mapData != null && mapData.size() > 0) {

			// orderLineKey = getKeyValue(mapData, ORDER_LINE_KEY);
			orderLineKey = mapData.get(ORDER_LINE_KEY).toString();
		}

		if (orderLineKey == null || orderLineKey.length() == 0) {

			throw new RuntimeException(
					ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH6"));

		} else {

			// Input XML for fetching order details
			inputXMLString = "<OrderLineDetail OrderLineKey='" + orderLineKey
					+ "' />";
			// logger.verbose("inputXMLString ==> "+inputXMLString);
			// Parsing the inputXMLString to get document object
			inputXMLDoc = XMLUtil.getDocument(inputXMLString);

			// Calling getOrderDetails
			outputXMLDoc = CommonUtilities.invokeAPI(env, API_ORDERLINE,
					inputXMLDoc);

			// Clearing the template from environment
			env.clearApiTemplate(API_ORDERLINE);

		}
		return outputXMLDoc;
	}

	// /**
	// * Fetches the Key value from mapData
	// *
	// * @return Returns a string which is the ley value.
	// * @param keyType
	// * String contains the type of key value to be fetch.
	// * @param mapData
	// * Map contains input XML
	// * @throws Exception
	// */
	private List isKeyXPath(boolean bXPathValueMapping) {

		String propKey = null;

		Set entrySet = map.entrySet();
		Iterator eit = entrySet.iterator();
		Map.Entry e = null;
		List lstXPaths = new ArrayList();

		while (eit.hasNext()) {
			e = (Map.Entry) eit.next();
			propKey = (String) e.getKey();
			// logger.verbose("isKeyXPath  charAt==>"+ (propKey.charAt(0) ==
			// '/') );
			// logger.verbose("isKeyXPath  indexOf==>"+
			// (propKey.indexOf(CONFIGURED_XPATH_NAME) != -1));

			if ( // if it is Xpath - Value mapping, we need to push all the
					// variables
			// with / in it to a List
			(bXPathValueMapping && propKey.charAt(0) == '/') ||
			// else look for configured xpath name
					((!bXPathValueMapping) && (propKey
							.indexOf(CONFIGURED_XPATH_NAME) != -1))) {
				lstXPaths.add(propKey);
			}
		}
		return lstXPaths;
	}
}
