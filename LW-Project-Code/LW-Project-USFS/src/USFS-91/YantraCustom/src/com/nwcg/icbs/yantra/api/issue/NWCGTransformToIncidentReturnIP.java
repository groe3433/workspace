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

package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * This class formats the xml as required by the insertIncidentRecord API
 * Algo:
 * 1. Fetches all the shipment lines
 * 2. Extract the OrderLineKey
 * 3. Extract the UnitPrice from OrderLineKey and add attribute to Shipment Line
 * 4. Add new attributes IncidentNo (OrderNo) and CacheId to the ShipmentLine
 * 5. return back the XML document  
 */
public class NWCGTransformToIncidentReturnIP implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGTransformToIncidentReturnIP.class);

	private Properties props = null;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	public Document modifyRecords(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGTransformToIncidentReturnIP.modifyRecords @@@@@");
		Element root = inXML.getDocumentElement();
		NodeList shipmentLineList = null;
		String shipNode = "";
		if (root != null) {
			shipmentLineList = root.getElementsByTagName("ShipmentLine");
			shipNode = root.getAttribute("ShipNode");
		}
		env.setApiTemplate("getOrderLineDetails",
				"NWCGTransformToIncidentReturnIP_getOrderLineDetails");
		if (shipmentLineList != null) {
			logger.verbose("@@@@@ NWCGTransformToIncidentReturnIP total Shipment Lines "
					+ shipmentLineList.getLength());
			for (int index = 0; index < shipmentLineList.getLength(); index++) {
				Element shipmentLine = (Element) shipmentLineList.item(index);
				String strOrderLineKey = shipmentLine
						.getAttribute("OrderLineKey");
				String strUnitPrice = "";
				String strIncidentNo = "";
				String strIncidentYear = "";
				logger.verbose("@@@@@ NWCGTransformToIncidentReturnIP got the order line key "
						+ strOrderLineKey);
				// get the order line details
				if (strOrderLineKey != null && (!strOrderLineKey.equals(""))) {
					// if no order line key we wont fetch the cost, rest of the
					// attribute will remain the same
					Document olDetailDoc = XMLUtil
							.createDocument("OrderLineDetail");
					Element root_olDetail = null;
					if (olDetailDoc != null) {
						root_olDetail = olDetailDoc.getDocumentElement();
						root_olDetail.setAttribute("OrderLineKey",
								strOrderLineKey);
					}
					// get the order line details output
					Document goldDoc = CommonUtilities.invokeAPI(env,
							"getOrderLineDetails", olDetailDoc);
					if (goldDoc != null) {
						// extract the unit price and incident number
						strUnitPrice = XPathUtil.getString(goldDoc,
								"/OrderLine/LinePriceInfo/@UnitPrice");
						strIncidentNo = XPathUtil.getString(goldDoc,
								"/OrderLine/Order/Extn/@ExtnIncidentNo");
						strIncidentYear = XPathUtil.getString(goldDoc,
								"/OrderLine/Order/Extn/@ExtnIncidentYear");
						logger.verbose("@@@@@ NWCGTransformToIncidentReturnIP getOrderLineDetails o/p "
								+ XMLUtil.getXMLString(goldDoc));
						if (strUnitPrice == null)
							strUnitPrice = "";
					}
				}
				shipmentLine.setAttribute("UnitPrice", strUnitPrice);
				shipmentLine.setAttribute("CacheID", shipNode);
				shipmentLine.setAttribute("IncidentNo", strIncidentNo);
				shipmentLine.setAttribute("IncidentYear", strIncidentYear);
				logger.verbose("@@@@@ NWCGTransformToIncidentReturnIP shipmentLine o/p "
						+ shipmentLine);
			}
		}
		env.clearApiTemplate("getOrderLineDetails");
		logger.verbose("@@@@@ NWCGTransformToIncidentReturnIP Returning "
				+ XMLUtil.getXMLString(inXML));
		logger.verbose("@@@@@ Exiting NWCGTransformToIncidentReturnIP.modifyRecords @@@@@");
		return inXML;
	}
}
