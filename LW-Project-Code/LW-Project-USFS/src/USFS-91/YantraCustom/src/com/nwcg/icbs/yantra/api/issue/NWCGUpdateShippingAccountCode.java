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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class updates the shipping account code field on the shipment details.
 * 
 * Related to CR 870 Issue: When an issue is in a status of 'shipped' and the
 * shipping account code is edited it is not updating the shipping account code
 * field on the shipment details.
 * 
 * @author Oxford Consulting Group
 * @since March 22, 2013
 * @version
 */
public class NWCGUpdateShippingAccountCode implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGUpdateShippingAccountCode.class);

	private Properties myProperties = null;

	public void setProperties(Properties props) throws Exception {
		this.myProperties = props;
	}
	
	/**
	 * This method checked the changeOrder Document and if in a status of
	 * 'shipped' updates the shipping account code from the changeOrder
	 * inputDoc.
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document updateShipmentAccountCode(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGUpdateShippingAccountCode.updateShipmentAccountCode @@@@@");
		logger.verbose("@@@@@ inputDoc : " + XMLUtil.getXMLString(inputDoc));
		boolean updateShippment = false;
		Document outDoc = null;
		String sNewValue = "";
		NodeList attributesList = inputDoc
				.getElementsByTagName(NWCGConstants.NWCG_CHANGE_ORDER_ATTRIBUTE);
		if (attributesList != null) {
			for (int i = 0; i < attributesList.getLength(); i++) {
				Element attribute = (Element) attributesList.item(i);
				String Name = (String) (attribute
						.getAttribute(NWCGConstants.NWCG_CHANGE_ORDER_ATTRIBUTE_NAME));
				if (Name != null) {
					if (Name.equals(NWCGConstants.EXTN_SHIP_ACCT_CODE)) {
						sNewValue = (String) (attribute
								.getAttribute(NWCGConstants.NWCG_CHANGE_ORDER_ATTRIBUTE_NEWVALUE));
						updateShippment = true;
					}
				}
			}
		}
		if (updateShippment && sNewValue != null) {
			Element order = inputDoc.getDocumentElement();
			// Retrieve orderheaderkey from changeOrder Document to call getShipmentList
			String sOrderHeaderKey = order
					.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			// Get all the shipments
			Document docGetShipmentList = getShipmentListFromOrder(env,
					sOrderHeaderKey);
			logger.verbose("@@@@@ updateShippedRecords :: docGetShipmentList " + XMLUtil.getXMLString(docGetShipmentList));
			NodeList nlShipment = docGetShipmentList
					.getElementsByTagName(NWCGConstants.SHIPMENT_ELEMENT);
			if (nlShipment.getLength() >= 1 && nlShipment != null) {
				logger.verbose("@@@@@ updateShippedRecords :: nlShipment " + nlShipment.getLength());
				// Note: Will only be one Node in the list
				Element elemShipmentTag = (Element) nlShipment.item(0);
				// Retrieve shipmentkey for changeShipment API call
				String sShipmentKey = elemShipmentTag
						.getAttribute(NWCGConstants.SHIPMENT_KEY);
				logger.verbose("@@@@@ updateShippedRecords :: strShipmentKey " + sShipmentKey);
				if (sShipmentKey != null && (!sShipmentKey.equals(""))) {
					// Create changeShipment document
					Document rt_Shipment = XMLUtil.newDocument();
					Element el_Shipment = rt_Shipment
							.createElement(NWCGConstants.SHIPMENT_ELEMENT);
					Element el_Extn = rt_Shipment
							.createElement(NWCGConstants.CUST_EXTN_ELEMENT);
					rt_Shipment.appendChild(el_Shipment);
					el_Shipment.setAttribute(NWCGConstants.SHIPMENT_KEY,
							sShipmentKey);
					el_Extn.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE,
							sNewValue);
					el_Shipment.appendChild(el_Extn);
					// update the shipped record
					logger.verbose("@@@@@ The input xml for ChangeShipment is:-" + XMLUtil.getXMLString(rt_Shipment));
					outDoc = CommonUtilities.invokeAPI(env,
							NWCGConstants.API_CHANGE_SHIPMENT, rt_Shipment);
					logger.verbose("@@@@@ The output xml for ChangeShipment is:-" + XMLUtil.getXMLString(outDoc));
				}
			}

		}
		logger.verbose("@@@@@ Exiting NWCGUpdateShippingAccountCode.updateShipmentAccountCode @@@@@");
		return inputDoc;
	}

	/**
	 * 
	 * Create shipment document and invoke getShipmentList API
	 * 
	 * @param env
	 * @param strOrderHeaderKey
	 * @return
	 * @throws Exception
	 */
	private Document getShipmentListFromOrder(YFSEnvironment env, String strOrderHeaderKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGUpdateShippingAccountCode.getShipmentListFromOrder @@@@@");
		Document rt_Shipment = XMLUtil.getDocument();
		Element el_Shipment = rt_Shipment
				.createElement(NWCGConstants.DOCUMENT_NODE_SHIPMENT);
		rt_Shipment.appendChild(el_Shipment);
		Element el_ShipmentLines = rt_Shipment
				.createElement(NWCGConstants.SHIPMENT_LINES_ELEMENT);
		el_Shipment.appendChild(el_ShipmentLines);
		Element el_ShipmentLine = rt_Shipment
				.createElement(NWCGConstants.SHIPMENT_LINE_ELEMENT);
		el_ShipmentLines.appendChild(el_ShipmentLine);
		el_ShipmentLine.setAttribute("OrderHeaderKey", strOrderHeaderKey);
		logger.verbose("getShipmentListFromOrder :: invoking getShipmentList with OrderHeaderKey " + strOrderHeaderKey);
		logger.verbose("@@@@@ Exiting NWCGUpdateShippingAccountCode.getShipmentListFromOrder @@@@@");
		return CommonUtilities.invokeAPI(env, "NWCGUpdateShippingAccountCode_getShipmentList", "getShipmentList", rt_Shipment);
	}
}