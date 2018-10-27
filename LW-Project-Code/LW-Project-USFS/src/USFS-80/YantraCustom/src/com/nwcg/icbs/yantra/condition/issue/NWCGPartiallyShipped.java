/* This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */

package com.nwcg.icbs.yantra.condition.issue;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will check if a order line has been shipped partailly 
 * and return true if requested_qty > issue_qty (possibly backorder situation)
 * 
 * @author jkim
 */
public class NWCGPartiallyShipped implements YCPDynamicConditionEx {

	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Map map = null;

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger();

	/**
	 * Returns true all the key=value conditions evaluates to true as checked
	 * from Resources file.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param name
	 *            Name of the condition as configured in Configurator.
	 * @param mapData
	 *            Properties configured in Configurator. Map contains input XML
	 * @param doc
	 *            Input Document.
	 * @return true if all the key=value conditions evaluates to true as checked
	 *         from Resources file.
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name, Map mapData, Document doc) {

		String orderLineKey = null;
		Document outputXMLDoc = null;
		boolean result = false;

		// defaulting the template for extended attributes only
		String bTemplate = "<OrderLine><Extn/></OrderLine>";

		logger.verbose("JDBG: Entering NWCGPartiallyShipped.evaluateCondition()");
		if(logger.isVerboseEnabled()){
			logger.verbose("JDBG: Entering the NWCGPartiallyShipped ,input document is:\n"+XMLUtil.getXMLString(doc));
		}

		// Get OrderLineKey from map entries
		if (mapData != null && mapData.size() > 0) {
			orderLineKey = mapData.get("OrderLineKey").toString();
			logger.verbose("JDBG: orderLineKey =["+orderLineKey+"]");
		}
		
		try {
			// Check for orderLineKey
			if (orderLineKey != null && !orderLineKey.equals("")) {
				if (bTemplate != null && !bTemplate.equals("")) {
					env.setApiTemplate("getOrderLineDetails", XMLUtil.getDocument(bTemplate));
				}
				// Function to get OrderLineDetails
				outputXMLDoc = getOrderLineDetail(env, mapData, doc);
				logger.verbose("JDBG: outputXMLDoc ===> \n"+XMLUtil.getXMLString(outputXMLDoc));
				String orderedQty = outputXMLDoc.getDocumentElement().getAttribute("OrderedQty");
				Element olExtn = (Element)XMLUtil.getChildNodeByName(outputXMLDoc.getDocumentElement(),"Extn");
				String extnOrigReqQty = olExtn.getAttribute("ExtnOrigReqQty"); //original requested qty
				String extnBackorderedQty = olExtn.getAttribute("ExtnBackorderedQty");
				logger.verbose("JDBG: extnOrigReqQty =["+extnOrigReqQty+"] extnBackorderedQty =["+extnBackorderedQty+"]");

				// condition: if extnOrigReqQty > orderedQty, then it's partially shipped (or Backorder situation)
				if ((Float.parseFloat(orderedQty) > 0) && ( Float.parseFloat(extnOrigReqQty) > Float.parseFloat(orderedQty) ) )
					result = true;
			}
		} catch (Exception ep) {
			logger.error("NWCG_ISSUE_BACKORDER_004",ep);
			result = false;
		}

		if (logger.isVerboseEnabled())
			logger.verbose("JDBG: Result of NWCGPartiallyShipped: " + result);
		logger.verbose("JDBG: Exiting NWCGPartiallyShipped.evaluateCondition()");
		return result;
	}

	/*            
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
			orderLineKey = mapData.get("OrderLineKey").toString();
		}

		if (orderLineKey == null || orderLineKey.length() == 0) {

			throw new RuntimeException(ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH6"));

		} else {

			// Input XML for fetching order details
			inputXMLString = "<OrderLineDetail OrderLineKey='" + orderLineKey + "' />";
			// Parsing the inputXMLString to get document object
			inputXMLDoc = XMLUtil.getDocument(inputXMLString);
			// Calling getOrderDetails
			outputXMLDoc = CommonUtilities.invokeAPI(env, "getOrderLineDetails", inputXMLDoc);
			// Clearing the template from environment
			env.clearApiTemplate("getOrderLineDetails");
		}
		return outputXMLDoc;
	}

	/**
	 * Stores properties configured in configurator.
	 * 
	 * @param map
	 *            Properties configured in Configurator.
	 */
	public void setProperties(Map map) {
		this.map = map;
		CommonUtilities.logProperties(map, logger);
	}
}