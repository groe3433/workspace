package com.nwcg.icbs.yantra.api.receipt;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * @author gAgrawal
 * When receipt line is adjusted to new quantity, system cancels all the existing open move tasks for that Item but does not 
 * create the task for that Item with new adjusted quantity.
 * This custom API create the tasks with new adjusted quantity when that receipt line is adjusted.
 */
public class NWCGUnreceiveReceiptLines implements YIFCustomApi {
	
	private static Logger logger = Logger.getLogger(NWCGUnreceiveReceiptLines.class.getName());
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public Document unReceiveLines(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGUnreceiveReceiptLines::unReceiveLines @@@@@");
		logger.verbose("@@@@@@@@@@ inXML : /n" + XMLUtil.getXMLString(inXML));
		
		// Call unreceiveOrder to adjust the receipt line quantity.
		CommonUtilities.invokeAPI(env, "unreceiveOrder", inXML);

		NodeList lsInReceiptLines = inXML.getElementsByTagName("ReceiptLine");
		String strLocation = inXML.getDocumentElement().getAttribute(
				"LocationId");

		for (int i = 0; i < lsInReceiptLines.getLength(); i++) {
			boolean isCreateMoveRequest = false;
			Element eleReceiptLine = (Element) lsInReceiptLines.item(i);
			String strInventoryStatus = eleReceiptLine
					.getAttribute("InventoryStatus");
			Element eleOutOrderLine = (Element) eleReceiptLine
					.getElementsByTagName("OrderLine").item(0);
			String strNode = eleOutOrderLine.getAttribute("ReceivingNode");
			String strItem = eleReceiptLine.getAttribute("ItemID");
			String strProductClass = eleReceiptLine
					.getAttribute("ProductClass");
			String strUnitOfMeasure = eleReceiptLine
					.getAttribute("UnitOfMeasure");

			// Create MoveRequest Header
			Document mvDoc = XMLUtil.createDocument("MoveRequest");
			Element eleMvDocRoot = mvDoc.getDocumentElement();
			eleMvDocRoot.setAttribute("Node", strNode);
			eleMvDocRoot.setAttribute("FromActivityGroup", "RECEIPT");
			eleMvDocRoot.setAttribute("Release", "Y");
			Element eleMoveRequestLines = mvDoc
					.createElement("MoveRequestLines");
			eleMvDocRoot.appendChild(eleMoveRequestLines);

			// Get NodeInventory
			Document docNodeInv = XMLUtil.createDocument("NodeInventory");
			Element eleNodeInv = docNodeInv.getDocumentElement();
			eleNodeInv.setAttribute("LocationId", strLocation);
			eleNodeInv.setAttribute("Node", strNode);
			Element eleInventory = docNodeInv.createElement("Inventory");
			eleInventory.setAttribute("InventoryStatus", strInventoryStatus);
			eleNodeInv.appendChild(eleInventory);
			Element eleInventoryItem = docNodeInv
					.createElement("InventoryItem");
			eleInventoryItem.setAttribute("ItemID", strItem);
			eleInventoryItem.setAttribute("ProductClass", strProductClass);
			eleInventoryItem.setAttribute("UnitOfMeasure", strUnitOfMeasure);
			eleInventory.appendChild(eleInventoryItem);
			
			// BEGIN - CR 872 - Jan 24, 2013
			
			Document docOut = CommonUtilities.invokeAPI(env, getNodeInventoryTemplate(), "getNodeInventory", docNodeInv);
			logger.verbose("@@@@@@@@@@ docOut : /n" + XMLUtil.getXMLString(docOut));
			
			Element eleLocationInventory = null;
			Element eleItemInventoryDetailList = null;
			
			if(checkIfInventoryExistsAttheLocation(docOut)) {
				if((docOut.getElementsByTagName("LocationInventory")).getLength() > 0) {
					eleLocationInventory  = (Element)docOut.getElementsByTagName("LocationInventory").item(0);
				}
				if(eleLocationInventory != null) {
					if((eleLocationInventory.getElementsByTagName("ItemInventoryDetailList")).getLength() > 0) {
						eleItemInventoryDetailList = (Element)eleLocationInventory.getElementsByTagName("ItemInventoryDetailList").item(0);
					}
				}
			}
					
			NodeList lsItemInventoryDetail = null;
			if(eleItemInventoryDetailList != null) {
				lsItemInventoryDetail = eleItemInventoryDetailList.getElementsByTagName("ItemInventoryDetail");
			}
			
			// END - CR 872 - Jan 24, 2013
			
			if (lsItemInventoryDetail != null && lsItemInventoryDetail.getLength() > 0) {
				for (int k = 0; k < lsItemInventoryDetail.getLength(); k++) {
					double dlPendout = 0;
					double dlQuantity = 0;
					String strQuant = "";
					Element eleItemInventoryDetail = (Element) lsItemInventoryDetail
							.item(k);
					String strPendout = eleItemInventoryDetail
							.getAttribute("PendOutQty");
					String strQuantity = eleItemInventoryDetail
							.getAttribute("Quantity");
					if (strPendout != null && !strPendout.equals(""))
						dlPendout = Double.parseDouble(strPendout);
					if (strQuantity != null && !strQuantity.equals(""))
						dlQuantity = Double.parseDouble(strQuantity);
					double dlNetQuantity = dlQuantity - dlPendout;
					if (dlNetQuantity > 0) {

						// Append moverequestlines in create move request doc.
						strQuant = String.valueOf(dlNetQuantity);

						Element eleMoveRequestLine = mvDoc
								.createElement("MoveRequestLine");
						eleMoveRequestLines.appendChild(eleMoveRequestLine);
						eleMoveRequestLine.setAttribute("ItemId", strItem);
						eleMoveRequestLine.setAttribute("EnterpriseCode",
								"NWCG");
						eleMoveRequestLine.setAttribute("UnitOfMeasure",
								strUnitOfMeasure);
						eleMoveRequestLine.setAttribute("ProductClass",
								strProductClass);
						eleMoveRequestLine.setAttribute("SourceLocationId",
								strLocation);
						eleMoveRequestLine.setAttribute("RequestQuantity",
								strQuant);
						eleMoveRequestLine.setAttribute("InventoryStatus",
								strInventoryStatus);
						// Set call move request flag to y
						isCreateMoveRequest = true;

						NodeList lsSerialDetail = eleItemInventoryDetail
								.getElementsByTagName("SerialDetail");
						if (lsSerialDetail != null
								&& lsSerialDetail.getLength() > 0) {
							String strSerialNo = ((Element) lsSerialDetail
									.item(0)).getAttribute("SerialNo");
							eleMoveRequestLine.setAttribute("SerialNo",
									strSerialNo);
							Element eleTagDetail = (Element) eleItemInventoryDetail
									.getElementsByTagName("TagDetail").item(0);
							if (eleTagDetail != null) {
								Element eleMoveRequestLineTag = mvDoc
										.createElement("MoveRequestLineTag");
								eleMoveRequestLineTag
										.setAttribute(
												"LotAttribute1",
												StringUtil
														.nonNull(eleTagDetail
																.getAttribute("LotAttribute1")));
								eleMoveRequestLineTag
										.setAttribute(
												"LotAttribute2",
												StringUtil
														.nonNull(eleTagDetail
																.getAttribute("LotAttribute2")));
								eleMoveRequestLineTag
										.setAttribute(
												"LotAttribute3",
												StringUtil
														.nonNull(eleTagDetail
																.getAttribute("LotAttribute3")));
								eleMoveRequestLineTag.setAttribute("LotNumber",
										StringUtil.nonNull(eleTagDetail
												.getAttribute("LotNumber")));
								eleMoveRequestLineTag.setAttribute(
										"RevisionNo",
										StringUtil.nonNull(eleTagDetail
												.getAttribute("RevisionNo")));
								eleMoveRequestLine
										.appendChild(eleMoveRequestLineTag);
							}

						}
					}
				}
			}
			logger.verbose("@@@@@@@@@@ mvDoc : "+XMLUtil.getXMLString(mvDoc));
			if (isCreateMoveRequest)
				CommonUtilities.invokeAPI(env, "createMoveRequest", mvDoc);
		}

		logger.verbose("@@@@@@@@@@ inXML : /n" + XMLUtil.getXMLString(inXML));
		logger.verbose("@@@@@ Exiting NWCGUnreceiveReceiptLines::unReceiveLines @@@@@");
		return inXML;
	}

	private Document getNodeInventoryTemplate() throws Exception {
		logger.verbose("@@@@@ Entering NWCGUnreceiveReceiptLines::getNodeInventoryTemplate @@@@@");
		Document docTempNodeInv = XMLUtil.createDocument("NodeInventory");
		Element eleTempNodeInv = docTempNodeInv.getDocumentElement();
		Element eleTempLocationInventoryList = docTempNodeInv
				.createElement("LocationInventoryList");
		eleTempNodeInv.appendChild(eleTempLocationInventoryList);
		Element eleTempLocationInventory = docTempNodeInv
				.createElement("LocationInventory");
		eleTempLocationInventory.setAttribute("PendOutQty", "");
		eleTempLocationInventory.setAttribute("Quantity", "");
		eleTempLocationInventoryList.appendChild(eleTempLocationInventory);
		Element eleTempItemInventoryDetailList = docTempNodeInv
				.createElement("ItemInventoryDetailList");
		eleTempLocationInventory.appendChild(eleTempItemInventoryDetailList);
		Element eleTempItemInventoryDetail = docTempNodeInv
				.createElement("ItemInventoryDetail");
		eleTempItemInventoryDetail.setAttribute("PendInQty", "");
		eleTempItemInventoryDetail.setAttribute("PendOutQty", "");
		eleTempItemInventoryDetail.setAttribute("Quantity", "");
		eleTempItemInventoryDetailList.appendChild(eleTempItemInventoryDetail);
		Element eleTempTagDetail = docTempNodeInv.createElement("TagDetail");
		eleTempItemInventoryDetail.appendChild(eleTempTagDetail);
		Element eleTempSerialList = docTempNodeInv.createElement("SerialList");
		eleTempItemInventoryDetail.appendChild(eleTempSerialList);
		Element eleTempSerialDetail = docTempNodeInv
				.createElement("SerialDetail");
		eleTempSerialList.appendChild(eleTempSerialDetail);
		logger.verbose("@@@@@ docTempNodeInv getNodeInventory INPUT : " + XMLUtil.getXMLString(docTempNodeInv));
		logger.verbose("@@@@@ Exiting NWCGUnreceiveReceiptLines::getNodeInventoryTemplate @@@@@");
		return docTempNodeInv;
	}
	
	// BEGIN - CR 872 - Jan 24, 2013
	
	private boolean checkIfInventoryExistsAttheLocation(Document docOut) throws Exception {
		logger.verbose("@@@@@ Entering NWCGUnreceiveReceiptLines::checkIfInventoryExistsAttheLocation @@@@@");
		if(docOut.getElementsByTagName("LocationInventory") == null) {
			logger.verbose("@@@@@ Exiting NWCGUnreceiveReceiptLines::checkIfInventoryExistsAttheLocation for false value @@@@@");
			return false;
		}
		logger.verbose("@@@@@ Exiting NWCGUnreceiveReceiptLines::checkIfInventoryExistsAttheLocation for true value @@@@@");
	    return true;
	}	
	
	// END - CR 872 - Jan 24, 2013	
	
}