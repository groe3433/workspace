package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.ArrayList;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCreateIssueTrackableListRecord implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * This method will handle all exceptions and log an Alert for them as they occur. 
	 * 
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		System.out.println("@@@@@ Entering NWCGCreateIssueTrackableListRecord::throwAlert @@@@@");
		Message.append(" ExceptionType='" + NWCGConstants.NWCG_RETURN_ALERTTYPE
				+ "'" + " InboxType='" + NWCGConstants.NWCG_RETURN_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_RETURN_QUEUEID + "' />");
		try {
			System.out.println("@@@@@ Trying to log the Alert...");
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION, inTemplate);
			System.out.println("@@@@@ Logged the Alert.");
		} catch (Exception ex1) {
			System.out.println("@@@@@ Caught exception while trying to log the Alert, choosing to do nothing about it...");
			ex1.printStackTrace();
		}
		System.out.println("@@@@@ Exiting NWCGCreateIssueTrackableListRecord::throwAlert @@@@@");
	}	
	
	public Document createIssueTrackableRecord(YFSEnvironment env,
			Document inDoc) throws Exception {

		System.out
				.println("NWCGCreateIssueTrackableListRecord: createIssueTrackableRecord: inDoc "
						+ XMLUtil.getXMLString(inDoc));

		Element rootElement = inDoc.getDocumentElement();
		Element extnElement = (Element) rootElement
				.getElementsByTagName("Extn").item(0);
		NodeList shipmentLineList = rootElement
				.getElementsByTagName("ShipmentLine");

		String docType = rootElement.getAttribute("DocumentType");
		String cacheId = rootElement.getAttribute("ShipNode");
		String incidentNo = extnElement.getAttribute("ExtnIncidentNum");
		String incidentYear = extnElement.getAttribute("ExtnYear");
		String issueTransfer = (docType != null && docType.equals("0001")) ? "ISSUE"
				: "TRANSFER";

		for (int j = 0; j < shipmentLineList.getLength(); j++) {
			Element shipmentLineElement = (Element) shipmentLineList.item(j);
			NodeList shipTagSerialList = shipmentLineElement
					.getElementsByTagName("ShipmentTagSerial");
			ArrayList<NWCGTrackableItem> serialList = new ArrayList<NWCGTrackableItem>();

			String issueNo = shipmentLineElement.getAttribute("OrderNo");
			String itemId = shipmentLineElement.getAttribute("ItemID");
			String itemDesc = shipmentLineElement.getAttribute("ItemDesc");
			String orderHeaderKey = shipmentLineElement
					.getAttribute("OrderHeaderKey");
			String orderLineKey = shipmentLineElement
					.getAttribute("OrderLineKey");

			for (int k = 0; k < shipTagSerialList.getLength(); k++) {
				Element shipTagSerialElement = (Element) shipTagSerialList
						.item(k);
				String serialNo = shipTagSerialElement.getAttribute("SerialNo");
				serialList.add(new NWCGTrackableItem(itemId, serialNo, null));
				insertRow(env, issueNo, docType, incidentNo, incidentYear,
						cacheId, itemId, itemDesc, serialNo, orderHeaderKey,
						orderLineKey, null, null, null, null, null, null, "1",
						issueTransfer);
			}
			getChildSerialNumbers(env, serialList, issueNo, docType,
					incidentNo, incidentYear, cacheId, orderHeaderKey,
					orderLineKey, issueTransfer);
		}
		return inDoc;
	}

	private ArrayList<NWCGTrackableItem> getChildSerialNumbers(
			YFSEnvironment env, ArrayList<NWCGTrackableItem> serialList,
			String issueNo, String docType, String incidentNo,
			String incidentYear, String cacheId, String orderHeaderKey,
			String orderLineKey, String issueTransfer) throws Exception {
		boolean isDuplicateRecord = false;
		ArrayList<NWCGTrackableItem> modSerialList = new ArrayList<NWCGTrackableItem>();
		for (NWCGTrackableItem trackItem : serialList) {
			String strParentSerialNo = trackItem.getSerialNo();
			System.out.println("@@@@@ trackItem.getSerialNo(): " + trackItem.getSerialNo());
			
			Document intermediateDoc = CommonUtilities.invokeAPI(env,
					NWCGConstants.API_GET_SERIAL_LIST, XMLUtil
							.getDocument("<Serial SerialNo=\""
									+ trackItem.getSerialNo() + "\" />"));
			String globalSerialKey = ((Element) intermediateDoc
					.getElementsByTagName("Serial").item(0))
					.getAttribute("GlobalSerialKey");
			Document childSerialsDoc = CommonUtilities.invokeAPI(env,
					NWCGConstants.API_GET_SERIAL_LIST, XMLUtil
							.getDocument("<Serial ParentSerialKey=\""
									+ globalSerialKey + "\" />"));

			NodeList serialChildNodeList = childSerialsDoc.getDocumentElement()
					.getElementsByTagName("Serial");
			if (serialChildNodeList.getLength() > 0) {
				for (int j = 0; j < serialChildNodeList.getLength(); j++) {
					Element serialChildElement = (Element) serialChildNodeList
							.item(j);
					String childSerialNo = serialChildElement
							.getAttribute("SerialNo");
					System.out.println("@@@@@ childSerialNo: " + childSerialNo);

					if(strParentSerialNo == childSerialNo) {
						isDuplicateRecord = true;
						break;
					}
					
					Document trackableItemDoc = getTrackableItem(env,
							childSerialNo);
					Element trackableItem = (Element) trackableItemDoc
							.getDocumentElement().getElementsByTagName(
									"NWCGTrackableItem").item(0);
					String cItemId = trackableItem.getAttribute("ItemID");
					String cItemDesc = trackableItem
							.getAttribute("ItemShortDescription");

					insertRow(
							env,
							issueNo,
							docType,
							incidentNo,
							incidentYear,
							cacheId,
							cItemId,
							cItemDesc,
							childSerialNo,
							orderHeaderKey,
							orderLineKey,
							trackItem.getItemId(),
							trackItem.getSerialNo(),
							NWCGTrackableItem.getParentItemId(trackItem),
							NWCGTrackableItem.getParentSerialNo(trackItem),
							NWCGTrackableItem.getGrandParentItemId(trackItem),
							NWCGTrackableItem.getGrandParentSerialNo(trackItem),
							NWCGTrackableItem.getNodeLevel(trackItem),
							issueTransfer);
					modSerialList.add(new NWCGTrackableItem(cItemId,
							childSerialNo, trackItem));
				}
				if(isDuplicateRecord) {
					StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGCreateIssueTrackableListRecord' " + "DUPLICATE_TRACKABLE_ID_EXITING_NOW");
					throwAlert(env, stbuf);
					break;
				}
				modSerialList.addAll(getChildSerialNumbers(env, modSerialList,
						issueNo, docType, incidentNo, incidentYear, cacheId,
						orderHeaderKey, orderLineKey, issueTransfer));
			}
		}
		return modSerialList;
	}

	private Document getTrackableItem(YFSEnvironment env, String serialNo)
			throws Exception {
		return CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, XMLUtil
						.getDocument("<NWCGTrackableItem SerialNo=\""
								+ serialNo + "\" />"));
	}

	private void insertRow(YFSEnvironment env, String issueNo, String docType,
			String incidentNo, String incidentYear, String cacheId,
			String itemId, String itemDesc, String serialNo,
			String orderHeaderKey, String orderLineKey, String parentItemID1,
			String parentSerialNo1, String parentItemID2,
			String parentSerialNo2, String parentItemID3,
			String parentSerialNo3, String nodeLevel, String issueTransfer)
			throws Exception {
		Document inDocForService = XMLUtil.getDocument();
		Element inDocRootElement = inDocForService
				.createElement("NWCGIssueTrackableList");
		inDocRootElement.setAttribute("IssueNo", issueNo);
		inDocRootElement.setAttribute("DocumentType", docType);
		inDocRootElement.setAttribute("IncidentNo", incidentNo);
		inDocRootElement.setAttribute("IncidentYear", incidentYear);
		inDocRootElement.setAttribute("CacheId", cacheId);
		inDocRootElement.setAttribute("ItemId", itemId);
		inDocRootElement.setAttribute("ItemDescription", itemDesc);
		inDocRootElement.setAttribute("SerialNo", serialNo);
		inDocRootElement.setAttribute("OrderHeaderKey", orderHeaderKey);
		inDocRootElement.setAttribute("OrderLineKey", orderLineKey);
		inDocRootElement.setAttribute("ParentItemID1", parentItemID1);
		inDocRootElement.setAttribute("ParentSerialNo1", parentSerialNo1);
		inDocRootElement.setAttribute("ParentItemID2", parentItemID2);
		inDocRootElement.setAttribute("ParentSerialNo2", parentSerialNo2);
		inDocRootElement.setAttribute("ParentItemID3", parentItemID3);
		inDocRootElement.setAttribute("ParentSerialNo3", parentSerialNo3);
		inDocRootElement.setAttribute("NodeLevel", nodeLevel);
		inDocRootElement.setAttribute("IssuedXfer", issueTransfer);
		inDocForService.appendChild(inDocRootElement);

		CommonUtilities.invokeService(env,
				"NWCGCreateIssueTrackableListService", inDocForService);

		System.out
				.println("NWCGCreateIssueTrackableListRecord: createIssueTrackableRecord: inDocForService "
						+ XMLUtil.getXMLString(inDocForService));
	}
}