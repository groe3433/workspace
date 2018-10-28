package com.nwcg.icbs.yantra.api.receipt;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGUpdateIncidentReturnOnAdjustReceipt {

	public Document updateIncidentReturn(YFSEnvironment env, Document inXML)
			throws YFSException, Exception {
		System.out.println(" #@@@@@# In NWCGUpdateIncidentReturnOnAdjustReceipt.updateIncidentReturn ");
		System.out.println(" #@@@@@# Printing updateIncidentReturn InXML : "+ XMLUtil.getXMLString(inXML));
		
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
			System.out.println(" #@@@@@# Printing updateIncidentReturn dReceiptDetail : " + XMLUtil.getXMLString(dReceiptDetail));

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
			System.out.println(" #@@@@@# Printing updateIncidentReturn getIncDetail : "	+ XMLUtil.getXMLString(getIncDetail));
			Document dGetIncDetail = CommonUtilities.invokeService(env,NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE,getIncDetail);
			System.out.println(" #@@@@@# Printing updateIncidentReturn dGetIncDetail : " + XMLUtil.getXMLString(dGetIncDetail));

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
					System.out.println(" #@@@@@# IF Printing outXML : "	+ XMLUtil.getXMLString(outXML));
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
			e.printStackTrace();
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
			System.out.println(" #@@@@@# In invokeModifyIncidentReturn");
			/**
			 * <NWCGIncidentReturn IncidentReturnKey="20130622202031127686531" QuantityRFI="9.0" QuantityReturned="9.0"/>
			 */
			Document updateIncRetDoc = XMLUtil.getDocument("<NWCGIncidentReturn IncidentReturnKey=\"" + incReturnKey + "\" " + QuantityInvStatus + "=\"" 
					+ sQtyUpdated + "\" QuantityReturned=\"" + sQtyUpdated + "\" />");
			System.out.println(" #@@@@@# Printing invokeModifyIncidentReturn.updateIncRetDoc : " + XMLUtil.getXMLString(updateIncRetDoc));
			Document outUpdateIncRetDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE,updateIncRetDoc);
			outReturnedDoc = outUpdateIncRetDoc;
			System.out.println(" #@@@@@# Printing invokeModifyIncidentReturn.outUpdateIncRetDoc : "+ XMLUtil.getXMLString(outUpdateIncRetDoc));
		} catch (Exception e) {
			e.printStackTrace();
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
