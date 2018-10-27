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

package com.nwcg.icbs.yantra.condition.issue;

import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will check if a order line has backordered qty 
 * and return true if it has backordered qty > 0 and issue qty = 0
 * 
 * @author gacharya, drodriguez
 */
public class NWCGOrderLineBackordered implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGOrderLineBackordered.class);
	
	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Map map = null;

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
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document doc) {

		String orderLineKey = null;
		Document outputXMLDoc = null;
		boolean result = false;

		// defaulting the template for extended attributes only
		String bTemplate = "<OrderLine><Extn/></OrderLine>";

		logger.debug("Entering NWCGOrderLineBackordered.evaluateCondition()");
		try {
			logger.verbose("Entering the NWCGOrderLineBackordered ,input document is: "+
					XMLUtil.extractStringFromDocument(doc));
		} catch (TransformerException e) {
			//logger.printStackTrace(e);
		}		

		// Get OrderLineKey from map entries
		if (mapData != null && mapData.size() > 0) {
			orderLineKey = mapData.get(NWCGConstants.ORDER_LINE_KEY).toString();
			logger.debug("orderLineKey =["+orderLineKey+"]");
		}
		
		try {
			// Check for orderLineKey
			if (orderLineKey != null && !orderLineKey.equals(NWCGConstants.EMPTY_STRING)) {
				if (bTemplate != null && !bTemplate.equals(NWCGConstants.EMPTY_STRING)) {
					env.setApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS, XMLUtil.getDocument(bTemplate));
				}
				// Function to get OrderLineDetails
				outputXMLDoc = getOrderLineDetail(env, mapData, doc);
				logger.verbose("outputXMLDoc ===> "+XMLUtil.extractStringFromDocument(outputXMLDoc));
				String orderedQty = outputXMLDoc.getDocumentElement().getAttribute(NWCGConstants.ORDERED_QTY);
				Element olExtn = (Element)XMLUtil.getChildNodeByName(outputXMLDoc.getDocumentElement(),NWCGConstants.EXTN_ELEMENT);
				String extnBackorderedQty = olExtn.getAttribute(NWCGConstants.EXTN_BACKORDER_QTY);
				logger.debug("orderedQty =["+orderedQty+"] extnBackorderedQty =["+extnBackorderedQty+"]");

				// condition: if orderedQty = 0 and extnBackorderedQty > 0, then it's Backorder
				if (Float.parseFloat(orderedQty) == 0.0 && Float.parseFloat(extnBackorderedQty) > 0)
					result = true;
			}
		} 
		catch (TransformerException te) {}
		catch (Exception ep) {
			logger.error("NWCG_ISSUE_BACKORDER_004",ep);
			result = false;
		}
		logger.debug("Result of NWCGOrderLineBackordered: " + result);
		logger.debug("Exiting NWCGOrderLineBackordered.evaluateCondition()");
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
			orderLineKey = mapData.get(NWCGConstants.ORDER_LINE_KEY).toString();
		}

		if (orderLineKey == null || orderLineKey.length() == 0) {

			throw new RuntimeException(ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH6"));

		} else {

			// Input XML for fetching order details
			inputXMLString = "<OrderLineDetail OrderLineKey='" + orderLineKey + "' />";
			// Parsing the inputXMLString to get document object
			inputXMLDoc = XMLUtil.getDocument(inputXMLString);
			// Calling getOrderDetails
			outputXMLDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_ORDER_LINE_DETAILS, inputXMLDoc);
			// Clearing the template from environment
			env.clearApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS);
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