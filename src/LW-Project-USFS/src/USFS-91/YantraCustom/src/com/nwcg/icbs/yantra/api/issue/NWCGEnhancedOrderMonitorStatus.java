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

import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGEnhancedOrderMonitorStatus implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGEnhancedOrderMonitorStatus.class);

	String orderNo;
	String orderHdrKey;
	String incNo;
	String incYr;
	String shippingContactName;
	String shippingContactPhone;
	Hashtable<String, String> htAddrInfo;
	Element elmOrderExtn;
	Element elmPersonInfoShipTo;
	String shipNode;
	
	public void setProperties(Properties arg0) throws Exception {
	}
	
	public NWCGEnhancedOrderMonitorStatus(){
		orderNo = "";
		orderHdrKey = "";
		incNo = "";
		incYr = "";
		shippingContactName = "";
		shippingContactPhone = "";
		shipNode = "";
		htAddrInfo = new Hashtable<String, String>();
	}

	/**
	 * This method will get the UTF lines and will send UpdateNFESResourceRequestReq message
	 * to ROSS
	 * - Get the line from MonitorConsolidation/Order/OrderStatuses/OrderStatus/OrderLine
	 * - Get the OL details and send update message to ROSS if the line is in UTF due to 
	 * 		- User entered UTF quantity equal to Requested Quantity
	 * 			If this line is in UTF status due to partial substitution, then do not send 
	 * 			this to ROSS as this will be sent as part of 'Confirm Shipment'
	 * 		- Line is in UTF status due to Consolidation (non-surviving line)
	 * 
	 * Input XML: Check the file ORDER_MONITOR_EX.xml under template/monitor/extn
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document processUTFAndNonSurvivingLines(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("@@@@@ Entering NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines @@@@@");
		Element elmDocIP = docIP.getDocumentElement();
		// There is only one child to the root element, so going with getFirstChild
		Element elmOrder = (Element) elmDocIP.getElementsByTagName("Order").item(0);
		setOrderAttributes(elmOrder);
		NodeList nlOLs = elmOrder.getElementsByTagName("OrderLine");
		String orderCreateTS = elmOrder.getAttribute("Createts");
		if (nlOLs != null && nlOLs.getLength() > 0){
			for (int i=0; i < nlOLs.getLength(); i++){
				Element elmOL = (Element) nlOLs.item(i);
				NodeList nlOrderLineChilds = elmOL.getChildNodes();
				String reqNo = "";
				for (int childs=0; childs < nlOrderLineChilds.getLength(); childs++){
					Node tmpNode = nlOrderLineChilds.item(childs);
					if ((tmpNode.getNodeType() == Node.ELEMENT_NODE)) {
						if(tmpNode.getNodeName().equals("Extn")) {
							reqNo = ((Element)tmpNode).getAttribute(NWCGConstants.EXTN_REQUEST_NO);
						}
					} 
				} 
				
				String olStatus = elmOL.getAttribute(NWCGConstants.MAX_LINE_STATUS);
				boolean nonSurvivingLine = false;
				if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS)){
					nonSurvivingLine = isNonSurvivingConsolidatedLine(env, reqNo);
				}
				else {
					String olKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
				}
				
				if (nonSurvivingLine){
					NWCGNFESResourceRequest nfesResReq = new NWCGNFESResourceRequest("ro:UpdateNFESResourceRequestReq");
					nfesResReq.setDocAttributes(NWCGAAConstants.ENV_USER_ID, orderHdrKey, "Order", orderNo, null);
					nfesResReq.setMessageOriginator(shipNode);
					nfesResReq.setRequestKey(incNo, incYr, reqNo);
					elmOL.setAttribute("OrderNo", orderNo);
					if (orderCreateTS == null){
						orderCreateTS = "";
					}
					elmOL.setAttribute("OrderCreatets", orderCreateTS);
					nfesResReq.populateFillDtlOrConsolidationDtl(elmOL, null, null);
					nfesResReq.populateAddressDtls(elmOrderExtn, elmPersonInfoShipTo);
					nfesResReq.populateShippingContactDtls(shippingContactName, shippingContactPhone);
					nfesResReq.populateSpecialNeeds();
					
					try {
						CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, 
								XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
					}
					catch (Exception ex){
						logger.error("!!!!! Caught General Exception :: " + ex);
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGEnhancedOrderMonitorStatus::processUTFAndNonSurvivingLines @@@@@");
		return docIP;
	}
	
	/**
	 * This method sets order related attributes to class variables
	 * @param elmOrder
	 */
	private void setOrderAttributes(Element elmOrder){
		logger.verbose("@@@@@ Entering NWCGEnhancedOrderMonitorStatus::setOrderAttributes @@@@@");
		orderNo = elmOrder.getAttribute(NWCGConstants.ORDER_NO);
		orderHdrKey = elmOrder.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		shipNode = elmOrder.getAttribute(NWCGConstants.SHIP_NODE);
		NodeList nlOrderChildNodes = elmOrder.getChildNodes();
		if (nlOrderChildNodes != null && nlOrderChildNodes.getLength() > 0){
			for (int i=0; i < nlOrderChildNodes.getLength(); i++){
				Node tmpNode = nlOrderChildNodes.item(i);
				if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elmIsExtn = (Element) tmpNode;
					if (elmIsExtn.getNodeName().equals("Extn")){
						incNo = elmIsExtn.getAttribute(NWCGConstants.INCIDENT_NO);
						incYr = elmIsExtn.getAttribute(NWCGConstants.INCIDENT_YEAR);
						shippingContactName = elmIsExtn.getAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR);
						shippingContactPhone = elmIsExtn.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR);
						elmOrderExtn = elmIsExtn;
					}
					else if (elmIsExtn.getNodeName().equals("PersonInfoShipTo")){
						elmPersonInfoShipTo = elmIsExtn;
					}
				}
			} 
		} 
		logger.verbose("@@@@@ Exiting NWCGEnhancedOrderMonitorStatus::setOrderAttributes @@@@@");
	}

	/**
	 * Make getOrderLineList with the below input template
	 *  <OrderLine OrderHeaderKey="201004141410074018626">
  			  <Extn ExtnRequestNoQryType="FLIKE" ExtnRequestNo="S-100511"/>
		</OrderLine>
			
		Output template
		<OrderLineList TotalLineList="">
			<OrderLine MaxLineStatus="" OrderLineKey="">
			</OrderLine>
		</OrderLineList>
		If there is more than one entry, then it means that the original line on
		which getOrderLineList is called is a surviving line. Do not send
		UpdateRequest for this line
			
		If there is only one entry, then send Update Request as this is a non surviving line
	 * @param reqNo
	 * @return
	 */
	private boolean isNonSurvivingConsolidatedLine(YFSEnvironment env, String reqNo){
		logger.verbose("@@@@@ Entering NWCGEnhancedOrderMonitorStatus::isNonSurvivingConsolidatedLine @@@@@");
		boolean nonSurvivingLine = false;
		try {
			Document docGetOLListIP = XMLUtil.createDocument("OrderLine");
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			elmGetOL.setAttribute("OrderHeaderKey", orderHdrKey);
			Element elmOLExtn = docGetOLListIP.createElement("Extn");
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute("ExtnRequestNoQryType", "FLIKE");
			elmOLExtn.setAttribute("ExtnRequestNo", reqNo);
			
			Document docGetOLListOP = CommonUtilities.invokeAPI(env, 
											"NWCGEnhancedOrderMonitor_getOrderLineList", 
											"getOrderLineList", docGetOLListIP);
			
			String matchingLines = docGetOLListOP.getDocumentElement().getAttribute("TotalLineList");
			int noOfLines = new Integer(matchingLines).intValue();
			if (noOfLines > 1){
				nonSurvivingLine = false;
			} else {
				nonSurvivingLine = true;
			}
		}
		catch(ParserConfigurationException pce){
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
		}
		catch(Exception e){
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGEnhancedOrderMonitorStatus::isNonSurvivingConsolidatedLine @@@@@");
		return nonSurvivingLine;
	}
}