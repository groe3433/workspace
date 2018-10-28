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

package com.nwcg.icbs.yantra.api.receipt;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGUpdateIncidentReturnOnAdjustReceipt {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGUpdateIncidentReturnOnAdjustReceipt.class);

	public Document updateIncidentReturn(YFSEnvironment env, Document inXML)
			throws YFSException, Exception {
		
		/**
		 * Example inXML 
		 * <Receipt IgnoreOrdering="Y" LocationId="RETURN-1" OverrideModificationRules="N"
		 * ReceiptHeaderKey="20130623123843127768822"> 
		 * <ReceiptLines>
		 * <ReceiptLine EnterpriseCode="NWCG" InventoryStatus="RFI" ItemID="000231" OrderNo="" ProductClass="Supply" Quantity="11.0"
		 * ReceiptLineKey="20130623123851127768908" ReceiptLineNo="6" UnitOfMeasure="EA" UnreceiveQuantity="1" YFC_NODE_NUMBER="1"/>
		 * </ReceiptLines> 
		 * <Shipment BuyerOrganizationCode="" DocumentType="0010" EnterpriseCode="NWCG" OrderNo=""
		 * ReceivingNode="AKAKK" SellerOrganizationCode="NWCG"/> 
		 * <Audit ReasonCode="REFURB-ADJ" ReasonText=""/> 
		 * </Receipt>
		 */

		Document outXML = null;
		try {

			Element childElems = null;
			String qty = null;
			String uQty = null;
			String invStatus = null;
			String itemId = null;

			double dQty = 0d;
			double dUQty = 0d;
			double dQtyUpdated = 0d;

			inXML.getDocumentElement().normalize();
			Element RootElem = inXML.getDocumentElement();
			String receiptHeaderKey = RootElem.getAttribute("ReceiptHeaderKey");
			NodeList nlRootChilds = RootElem.getChildNodes();
			for (int rootNode = 0; rootNode < nlRootChilds.getLength(); rootNode++) {
				Node tmpNode = nlRootChilds.item(rootNode);
				if (tmpNode.getNodeName().equals("ReceiptLines")) {
					childElems = (Element) tmpNode;
				}
			}
			NodeList nlReceiptLine = childElems.getElementsByTagName("ReceiptLine");

			if (nlReceiptLine != null && nlReceiptLine.getLength() > 0) {
				for (int i = 0; i < nlReceiptLine.getLength(); i++) {
					Element childElem = (Element) nlReceiptLine.item(i);
					itemId = childElem.getAttribute("ItemID");
					invStatus = childElem.getAttribute("InventoryStatus");
					qty = childElem.getAttribute("Quantity");
					uQty = childElem.getAttribute("UnreceiveQuantity");
				}
			}

			/**
			 * API: getReceiptDetails 
			 * Params: ReceiptHeaderKey
			 */
			Document rdTemplate = XMLUtil.getDocument("<Receipt ReceiptNo=\"\"><Extn ExtnIncidentNo=\"\" /></Receipt>");
			Document inputReceiptDetail = XMLUtil.getDocument("<Receipt ReceiptHeaderKey=\"" + receiptHeaderKey + "\" />");
			Document dReceiptDetail = CommonUtilities.invokeAPI(env,rdTemplate, "getReceiptDetails", inputReceiptDetail);

			/**
			 * Example dReceiptDetail 
			 * <Receipt ReceiptNo="AKAKK004015"> 
			 * 		<Extn ExtnIncidentNo="AK-UYD-000326"/> 
			 * </Receipt>
			 */
			Element receiptElement = (Element) dReceiptDetail.getElementsByTagName("Receipt").item(0);
			String receiptNo = receiptElement.getAttribute("ReceiptNo");
			Element extnElement = (Element) dReceiptDetail.getElementsByTagName("Extn").item(0);
			String incidentNo = extnElement.getAttribute("ExtnIncidentNo");

			/**
			 * Service: NWCGgetIncidentReturnsService 
			 * Params: IncidentNo 
			 * 		   ItemID
			 * <NWCGIncidentReturn IncidentNo="AK-MID-000280" ItemID="000212" />
			 */

			Document getIncDetail = XMLUtil.getDocument("<NWCGIncidentReturn IncidentNo=\""	+ incidentNo + "\" ItemID=\"" + itemId + "\" />");
			Document dGetIncDetail = CommonUtilities.invokeService(env,NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE,getIncDetail);

			/**
			 * Example dGetIncDetail 
			 * <NWCGIncidentReturnList>
			 * 		<NWCGIncidentReturn CacheID="AKAKK" Createprogid="Console" Createts="2013-06-30T16:13:46-04:00" Createuserid="hsemock"
			 * 		DateIssued="2013-06-30T16:13:38-04:00" IncidentNo="AK-MID-000280" IncidentReturnKey="20130630161346129389384" 
			 * 		IncidentYear="2013"	IssueNo="0000698002" ItemID="000212" Lockid="1" Modifyprogid="Console" Modifyts="2013-07-09T16:50:36-04:00"
			 * 		Modifyuserid="nlarson" OverReceipt="N" QuantityNRFI="0.00" QuantityRFI="4.00" QuantityRFIRefurb="0.00"
			 * 		QuantityReturned="4.00" QuantityShipped="4.00" QuantityUnsNwtRefurb="0.00" QuantityUnsNwtReturn="0.00"
			 * 		QuantityUnsRefurb="0.00" QuantityUnsRet="0.00" ReceivedAsComponent="N" TrackableID="" UnitPrice="39.21"
			 * 		isHistory="N"/> 
			 * </NWCGIncidentReturnList>
			 */
			dQty = Double.parseDouble(qty);
			dUQty = Double.parseDouble(uQty);
			dQtyUpdated = dQty - dUQty;
			String sQtyUpdated = Double.toString(dQtyUpdated);

			NodeList returnLines = dGetIncDetail.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
			int returnLineCount = returnLines.getLength();
			if (returnLineCount == 1) {

				Element RetEl = (Element) returnLines.item(0);
				String incReturnKey = RetEl.getAttribute("IncidentReturnKey");

				String QuantityInvStatus = null;
				if (dQty >= dUQty) {
					if (invStatus.equals("RFI")) {
						QuantityInvStatus = "QuantityRFI";
					} else if (invStatus.equals("NRFI")) {
						QuantityInvStatus = "QuantityNRFI";
					} else if (invStatus.equals("UNSERVICE")) {
						QuantityInvStatus = "QuantityUnsRet";
					} else if (invStatus.equals("UNSRV-NWT")) {
						QuantityInvStatus = "QuantityUnsNwtReturn";
					}

					outXML = invokeModifyIncidentReturn(env, incReturnKey,QuantityInvStatus, sQtyUpdated);
				}
			} else {
				if (dQty >= dUQty) {
					StringBuffer stbuf = null;
					if (invStatus.equals("RFI")) {
						stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGUpdateIncidentReturnOnAdjustReceipt' "
										+ "DetailDescription='Update Incident Return Record failed. IncidentNo:"
										+ incidentNo + ", ItemID:" + itemId	+ ", ReceiptNo:" + receiptNo + ", Return Quantity:" + qty 
										+ ", Unreceive Quantity:" + uQty + ". Update QuantityRFI to " + sQtyUpdated + "' ");
					} else if (invStatus.equals("NRFI")) {
						stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGUpdateIncidentReturnOnAdjustReceipt' "
										+ "DetailDescription='Update Incident Return Record failed. IncidentNo:"
										+ incidentNo + ", ItemID:" + itemId	+ ", ReceiptNo:" + receiptNo + ", Return Quantity:" + qty
										+ ", Unreceive Quantity:" + uQty + ". Update QuantityNRFI to " + sQtyUpdated + "' ");
					} else if (invStatus.equals("UNSERVICE")) {
						stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGUpdateIncidentReturnOnAdjustReceipt' "
										+ "DetailDescription='Update Incident Return Record failed. IncidentNo:"
										+ incidentNo + ", ItemID:" + itemId + ", ReceiptNo:" + receiptNo + ", Return Quantity:" + qty
										+ ", Unreceive Quantity:" + uQty + ". Update QuantityUnsRet to " + sQtyUpdated + "' ");
					} else if (invStatus.equals("UNSRV-NWT")) {
						stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGUpdateIncidentReturnOnAdjustReceipt' "
										+ "DetailDescription='Update Incident Return Record failed. IncidentNo:"
										+ incidentNo + ", ItemID:" + itemId + ", ReceiptNo:" + receiptNo + ", Return Quantity:" + qty
										+ ", Unreceive Quantity:" + uQty + ". Update QuantityUnsNwtReturn to " + sQtyUpdated + "' ");
					}
					throwAlert(env, stbuf);
				}
			}
		} catch (Exception e) {
			//logger.printStackTrace(e);
		}
		return outXML;
	}

	/**
	 * Service: NWCGModifyIncidentReturnService 
	 * Description: Updates the Incident Return record for the mentioned IncidentReturnKey 
	 * Params: TransType="RETURNS" 
	 * 		   SequenceKey = SequenceKey (for the ReceiptLineKey[TransactionLineKey]) 
	 * 		   TransQty = ReturnQty - ReturnUnreceiveQty 
	 * 		   TransAmt = TransQty * UnitCost from the Billing Transaction
	 */
	public Document invokeModifyIncidentReturn(YFSEnvironment env,String incReturnKey, String QuantityInvStatus, String sQtyUpdated)
			throws Exception {
		Document outReturnedDoc = null;

		try {
			/**
			 * <NWCGIncidentReturn IncidentReturnKey="20130622202031127686531" QuantityRFI="9.0" QuantityReturned="9.0"/>
			 */
			Document updateIncRetDoc = XMLUtil.getDocument("<NWCGIncidentReturn IncidentReturnKey=\"" + incReturnKey + "\" " + QuantityInvStatus + "=\"" 
					+ sQtyUpdated + "\" QuantityReturned=\"" + sQtyUpdated + "\" />");
			Document outUpdateIncRetDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE,updateIncRetDoc);
			outReturnedDoc = outUpdateIncRetDoc;
		} catch (Exception e) {
			//logger.printStackTrace(e);
		}
		return outReturnedDoc;
	}

	/**
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='AlertType'" + " InboxType='" + NWCGConstants.NWCG_RETURN_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_INCIDENT_RETURN_ERROR + "' />");
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("NWCG_INCIDENT_RETURN_ERROR");
		}
	}

}
