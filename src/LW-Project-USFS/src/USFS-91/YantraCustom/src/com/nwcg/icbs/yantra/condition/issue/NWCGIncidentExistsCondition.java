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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will check if the Incident exists and return true if it finds
 * an Incident
 * 
 * @author gacharya, drodriguez
 */
public class NWCGIncidentExistsCondition implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGIncidentExistsCondition.class);
	
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
	 *            Properties configured in Configurator.
	 * @param doc
	 *            Input Document.
	 * @return true if all the key=value conditions evaluates to true as checked
	 *         from Resources file.
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document doc) {
		logger.debug("Entering NWCGIncidentExistsCondition.evaluateCondition()");
		boolean result = true;
		try {
			Document getIncidentOrderListInput = XMLUtil
					.createDocument(NWCGConstants.NWCG_INCIDENT_ORDER);
			Element orderExtn = (Element)XMLUtil.getChildNodeByName(doc.getDocumentElement(),NWCGConstants.EXTN_ELEMENT);
			String incidentNo = orderExtn.getAttribute(NWCGConstants.INCIDENT_NO);
			getIncidentOrderListInput.getDocumentElement().setAttribute(
					"IncidentNumber", incidentNo);
			
			Document getIncidentOrderListOutput = CommonUtilities.invokeService(env,"NWCGGetIncidentOrderList",getIncidentOrderListInput);
			NodeList incidentOrders = getIncidentOrderListOutput.getDocumentElement().getChildNodes();
			if(null==incidentOrders || incidentOrders.getLength() !=1){
				logger.error("Could not find any incident with IncidentNumber:"+incidentNo);
				result = false;
			}
			
		} catch (Exception ep) {
			logger.error("NWCG_INCIDENT_EXISTS_001",ep);
			result = false;
		}
		logger.debug("Result of NWCGIncidentExistsCondition: " + result);
		logger.debug("Exiting NWCGIncidentExistsCondition.evaluateCondition()");
		return result;
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