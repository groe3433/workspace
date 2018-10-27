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

package com.nwcg.icbs.yantra.ue;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.yfs.japi.ue.YFSOrderRepricingUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The "SAVE" button on Incident Transfer Details Screen calls changeOrder. 
 * The changeOrder API calls reorderPrice API. 
 * This gets called on reorderPrice API User Exit.   
 * 
 * @author Lightwellinc
 * @version 1.0
 * @date November 5, 2014
 */
public class NWCGBeforeOrderRepricingUE implements YFSOrderRepricingUE {

	/**
	 * inDoc Example:
	 * <Order DocumentType="0008.ex" EnterpriseCode="NWCG" IsNewOrder="N" OrderHeaderKey="20141015143706154311841" OrderNo="0000717502" SellerOrganizationCode="NWCG">
     * 		<OrderReleases/>
     * 		<OrderLines>
     * 			<OrderLine HasRepricingQuantityChanged="N" OrderLineKey="20141104141259154345550" PrimeLineNo="1" SubLineNo="1">
     * 				<Item CostCurrency="" CountryOfOrigin="" CustomerItem=""
     * 					CustomerItemDesc="" ECCNNo="" HarmonizedCode="" ISBN=""
     * 					ItemDesc="VALVE - FOOT, 1 1/2&quot; NH-F W/STRAINER"
     * 					ItemID="000212" ItemShortDesc="VALVE - FOOT, 1 1/2&quot; NH-F W/STRAINER"
     * 					ItemWeight="1.88" ItemWeightUOM="LBS"
     * 					ManufacturerItem="" ManufacturerItemDesc="" ManufacturerName="" NMFCClass="" NMFCCode=""
     * 					NMFCDescription="" ProductClass="Supply" ProductLine="Water Handling" ScheduleBCode=""
     * 					SupplierItem="" SupplierItemDesc=""
     * 					TaxProductCode="Durable-10" UPCCode="" UnitCost="0.00" UnitOfMeasure="EA"/>
     * 				<ModificationTypes/>
     * 			</OrderLine>
     * 		</OrderLines>
     * 		<OverallTotals GrandCharges="0.00" GrandDiscount="0.00"
     * 			GrandTax="0.00" GrandTotal="0.00" HdrCharges="0.00"
     * 			HdrDiscount="0.00" HdrTax="0.00" HdrTotal="0.00" LineSubTotal="0.00"/>
     *		<ModificationTypes>
     *			<ModificationType ImpactsPricing="N" Level="ORDER" Name="ADD_LINE"/>
     *		</ModificationTypes>
     * </Order>
     * 
     * Objective here is to populate the 'LinePriceInfo' element with the Latest Issued Price on the UnitPrice attribute. 
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws YFSUserExitException
	 */
	public Document orderReprice(YFSEnvironment env, Document inDoc) throws YFSUserExitException {
		System.out.println("@@@@@ Entering com.nwcg.icbs.yantra.ue.NWCGBeforeOrderRepricingUE::beforeOrderRepricing @@@@@");
		
		try {
			NodeList nlinDocOrder = inDoc.getElementsByTagName("Order");
			if(nlinDocOrder.getLength() > 0) {
				Node nodeinDocOrder = nlinDocOrder.item(0);
				Element eleminDocOrder = (Element)nodeinDocOrder;
				String strDocumentType = eleminDocOrder.getAttribute("DocumentType");
				// Check for Incident Transfer "0008.ex" document type. 
				if(strDocumentType.equals("0008.ex")) {
					NodeList listinDocModificationType = inDoc.getElementsByTagName("ModificationType");
					// Modification Indicator will be on the first ModificationType element. 
					if(listinDocModificationType.getLength() > 0) {
						Node nodeinDocModificationType = listinDocModificationType.item(0);
						Element eleminDocModificationType = (Element)nodeinDocModificationType;	
						String strModificationName = eleminDocModificationType.getAttribute("Name");
						// need to check for "ADD_LINE" because otherwise the code will trigger on a "Remove" action. 
						if(strModificationName.equals("ADD_LINE")) {
							NodeList nlinDocOrderLine = inDoc.getElementsByTagName("OrderLine");
							// This check is so that the code doesn't trigger if it is a new order with NO order lines. 
							if(nlinDocOrderLine.getLength() > 0) {
								String strIncidentNo = "";
								String strYear = "";
								String strItemID = "";
								String strShipNode = "";
								String strTrackableId = "";
								
								// set an input for the API getOrderDetails based on OrderHeaderKey to get the order, use a new template NWCGBeforeOrderRepricingUE_getOrderDetails to get what we need here. 
								String strOrderHeaderKey = eleminDocOrder.getAttribute("OrderHeaderKey");
								System.out.println("@@@@@ OrderHeaderKey of the Incident Transfer :: " + strOrderHeaderKey);
								Document inDoc1 = createGetOrderDetailsDocument(env, strOrderHeaderKey);
									
								// call getOrderDetails to get ShipNode, ExtnIncidentNo, ExtnIncidentYear, ExtnTrackableId, and ItemID
								Document outDoc1 = CommonUtilities.invokeAPI(env, "NWCGBeforeOrderRepricingUE_getOrderDetails", "getOrderDetails", inDoc1);
								
								NodeList nloutDoc1Order = outDoc1.getElementsByTagName("Order");
								if(nloutDoc1Order.getLength() > 0) {
									Node nodeoutDoc1Order = nloutDoc1Order.item(0);
									Element elemoutDoc1Order = (Element)nodeoutDoc1Order;
									strShipNode = elemoutDoc1Order.getAttribute("ShipNode");
									System.out.println("@@@@@ strShipNode :: " + strShipNode);
								}
								NodeList nloutDoc1Extn = outDoc1.getElementsByTagName("Extn");
								if(nloutDoc1Extn.getLength() > 0) {
									Node nodeoutDoc1Extn = nloutDoc1Extn.item(0);
									Element elemoutDoc1Extn = (Element)nodeoutDoc1Extn;
									strIncidentNo = elemoutDoc1Extn.getAttribute("ExtnIncidentNo");
									System.out.println("@@@@@ strIncidentNo :: " + strIncidentNo);
									strYear = elemoutDoc1Extn.getAttribute("ExtnIncidentYear");
									System.out.println("@@@@@ strYear :: " + strYear);
								}
								for(int i = 0; i < nlinDocOrderLine.getLength(); i++) {
									System.out.println("@@@@@ OrderLine: " + i);
									Node nodeinDocOrderLine = nlinDocOrderLine.item(i);
									Element eleminDocOrderLine = (Element)nodeinDocOrderLine;
									String strOrderLineKey = eleminDocOrderLine.getAttribute("OrderLineKey");
									Node nodeoutDoc1OrderLine = XPathUtil.getNode(outDoc1.getDocumentElement(), "/Order/OrderLines/OrderLine[@OrderLineKey='" + strOrderLineKey + "']");
									Element elemoutDoc1OrderLine = (Element)nodeoutDoc1OrderLine;
									NodeList nloutDoc1OrderLineExtn = elemoutDoc1OrderLine.getElementsByTagName("Extn");
									if(nloutDoc1OrderLineExtn.getLength() > 0) {
										Node nodeoutDoc1OrderLineExtn = nloutDoc1OrderLineExtn.item(0);
										Element elemoutDoc1OrderLineExtn = (Element)nodeoutDoc1OrderLineExtn;
										strTrackableId = elemoutDoc1OrderLineExtn.getAttribute("ExtnTrackableId");
										System.out.println("@@@@@ strTrackableId :: " + strTrackableId);
									}
									NodeList nlindocOrderLineItem = eleminDocOrderLine.getElementsByTagName("Item");
									if(nlindocOrderLineItem.getLength() > 0) {
										Node nodeindocOrderLineItem = nlindocOrderLineItem.item(0);
										Element elemindocOrderLineItem = (Element)nodeindocOrderLineItem;
										strItemID = elemindocOrderLineItem.getAttribute("ItemID");
										System.out.println("@@@@@ strItemID :: " + strItemID);
									}
						
									// set an input for the service NWCGGetOriginalIssuePrice based on ExtnIncidentNo, ExtnIncidentYear, ShipNode, ExtnTrackableId, ItemID. 
									Document indoc2 = createNWCGGetOriginalIssuePriceDocument(env, strIncidentNo, strYear, strItemID, strShipNode, strTrackableId);
									
									// call NWCGGetOriginalIssuePrice to get the Last Issued Price. 
									Document outDoc2 = CommonUtilities.invokeService(env, "NWCGGetOriginalIssuePrice", indoc2);
			
									String strLastIssuePrice = "";
									NodeList nloutDoc2 = outDoc2.getElementsByTagName("NWCGGetOriginalIssuePrice");
									if(nloutDoc2.getLength() > 0) {
										Node nodeoutDoc2 = nloutDoc2.item(0);
										Element elemoutDoc2 = (Element)nodeoutDoc2;
										strLastIssuePrice = elemoutDoc2.getAttribute("LastIssuedPrice");
										System.out.println("@@@@@ strLastIssuePrice :: " + strLastIssuePrice);
									}
											
									// Set the LinePriceInfo element with UnitPrice attribute containing the Last Issued Price for the item on the original input document. 
									Element eleminDocLinePriceInfo = inDoc.createElement("LinePriceInfo");
									eleminDocOrderLine.appendChild(eleminDocLinePriceInfo);
									eleminDocLinePriceInfo.setAttribute("UnitPrice", strLastIssuePrice);
								}
							}
						}
					}
				} else {
					// Do nothing if Document type is not 0008.ex (i.e. Incident Transfer)
				}
			} 
		} catch(Exception ex) {
			System.out.println("!!!!! Caught General Exception: " + ex);
		}
		
		System.out.println("@@@@@ Exiting com.nwcg.icbs.yantra.ue.NWCGBeforeOrderRepricingUE::beforeOrderRepricing @@@@@");
		return inDoc;
	}
	
	/**
	 * 
	 * @param env
	 * @param strOrderHeaderKey
	 * @return
	 * @throws Exception
	 */
	private Document createGetOrderDetailsDocument(YFSEnvironment env, String strOrderHeaderKey) throws Exception {
		Document inDoc = null;
		inDoc = XMLUtil.newDocument();
		Element elemOrder = inDoc.createElement("Order");
		inDoc.appendChild(elemOrder);
		elemOrder.setAttribute("OrderHeaderKey", strOrderHeaderKey);
		return inDoc;
	}	
	
	/**
	 * 
	 * @param env
	 * @param strIncidentNo
	 * @param strYear
	 * @param strItemID
	 * @param strShipNode
	 * @param strTrackableId
	 * @return
	 * @throws Exception
	 */
	private Document createNWCGGetOriginalIssuePriceDocument(YFSEnvironment env, String strIncidentNo, String strYear, String strItemID, String strShipNode, String strTrackableId) throws Exception {
		Document inDoc = null;
		inDoc = XMLUtil.newDocument();
		Element elemNWCGGetOriginalIssuePrice = inDoc.createElement("NWCGGetOriginalIssuePrice");
		inDoc.appendChild(elemNWCGGetOriginalIssuePrice);
		elemNWCGGetOriginalIssuePrice.setAttribute("IncidentNo", strIncidentNo);
		elemNWCGGetOriginalIssuePrice.setAttribute("Year", strYear);
		elemNWCGGetOriginalIssuePrice.setAttribute("ItemID", strItemID);
		elemNWCGGetOriginalIssuePrice.setAttribute("ShipNode", strShipNode);
		elemNWCGGetOriginalIssuePrice.setAttribute("TrackableId", strTrackableId);
		return inDoc;
	}	
}