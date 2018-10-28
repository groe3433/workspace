package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSerialRecordProcessor 
{
	public ArrayList<NWCGTrackableItem> insertChildSerialNumbers(YFSEnvironment env, ArrayList<NWCGTrackableItem> serialList, String orderLineKey, String receiptLineKey, Map<String, String> baseMap) throws Exception
	{
		//System.out.println("NWCGSerialRecordProcessor : insertChildSerialNumbers : serialList : \n" + serialList.toString());		
		ArrayList<NWCGTrackableItem> modSerialList = new ArrayList<NWCGTrackableItem>();		
		for(NWCGTrackableItem trackItem : serialList)
		{
			String serialNo = trackItem.getSerialNo().trim();

			Document opTemplate = XMLUtil.getDocument("<SerialList><Serial GlobalSerialKey=\"\" SerialNo=\"\" /></SerialList>");
			Document ipDoc = XMLUtil.getDocument("<Serial SerialNo=\"" + serialNo + "\" />");			
			Document intermediateDoc = CommonUtilities.invokeAPI(env, opTemplate, NWCGConstants.API_GET_SERIAL_LIST, ipDoc);			

			String globalSerialKey = ((Element)intermediateDoc.getElementsByTagName("Serial").item(0)).getAttribute("GlobalSerialKey");
			Document childSerialsDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_SERIAL_LIST, XMLUtil.getDocument("<Serial ParentSerialKey=\"" + globalSerialKey + "\" />"));

			NodeList serialChildNodeList = childSerialsDoc.getDocumentElement().getElementsByTagName("Serial");
			if (serialChildNodeList.getLength() > 0)
			{
				for(int j = 0; j < serialChildNodeList.getLength(); j++)
				{
					Element serialChildElement = (Element)serialChildNodeList.item(j);
					String childSerialNo = serialChildElement.getAttribute("SerialNo");

					Document trackableItemDoc = getTrackableItem(env, childSerialNo);
					Element trackableItem = (Element)trackableItemDoc.getDocumentElement().getElementsByTagName("NWCGTrackableItem").item(0);
					String cItemId = trackableItem.getAttribute("ItemID");

					insertRow(env, cItemId, childSerialNo, orderLineKey, receiptLineKey, trackItem.getItemId(), trackItem.getSerialNo(), NWCGTrackableItem.getNodeLevel(trackItem), baseMap);
					modSerialList.add(new NWCGTrackableItem(cItemId, childSerialNo, trackItem));
				}
				modSerialList.addAll(insertChildSerialNumbers(env, modSerialList, orderLineKey, receiptLineKey, baseMap));
			}
		}
		//System.out.println("NWCGSerialRecordProcessor : insertChildSerialNumbers : modSerialList : \n" + modSerialList.toString());
		return modSerialList;
	}

	private Document getTrackableItem(YFSEnvironment env, String serialNo) throws Exception
	{
		Document ipDoc = XMLUtil.getDocument("<NWCGTrackableItem SerialNo=\"" + serialNo + "\" />");
		return CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, ipDoc);
	}

	public static Element getChildElementByName(Element parentElement, String childName)
	{
		System.out.println("NWCGSerialRecordProcessor: getChildElementByName: parentElement = " + parentElement + ", childName = " + childName);
		for(Node child = parentElement.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if(child instanceof Element && childName.equals(child.getNodeName())) return (Element) child;
		}
		return null;
	}

	public static void insertRow(YFSEnvironment env, String itemId, String serialNo, String orderLineKey, String receiptLineKey, String parentItemID, String parentSerialNo, String nodeLevel, Map<String, String> baseMap) throws Exception 
	{
		Document inDocForService = XMLUtil.getDocument();
		Element inDocRootElement = inDocForService.createElement("NWCGSerialRecord");
		
		if(baseMap != null)
		{
			inDocRootElement.setAttribute("OrderNo", baseMap.get("OrderNo"));
			inDocRootElement.setAttribute("ReceiptNo", baseMap.get("ReceiptNo"));
			inDocRootElement.setAttribute("OrderType", baseMap.get("OrderType"));
			inDocRootElement.setAttribute("DocumentType", baseMap.get("DocumentType"));
			inDocRootElement.setAttribute("TransactionType", baseMap.get("TransactionType"));
			inDocRootElement.setAttribute("IncidentNo", baseMap.get("IncidentNo"));
			inDocRootElement.setAttribute("IncidentYear", baseMap.get("IncidentYear"));
			inDocRootElement.setAttribute("OrderHeaderKey", baseMap.get("OrderHeaderKey"));
			inDocRootElement.setAttribute("ReceiptHeaderKey", baseMap.get("ReceiptHeaderKey"));
			inDocRootElement.setAttribute("Node", baseMap.get("Node"));
		}
		
		inDocRootElement.setAttribute("SerialNo", serialNo);
		inDocRootElement.setAttribute("ItemId", itemId);
		inDocRootElement.setAttribute("OrderLineKey", orderLineKey);
		inDocRootElement.setAttribute("ReceiptLineKey", receiptLineKey);
		inDocRootElement.setAttribute("ParentItemID", parentItemID);
		inDocRootElement.setAttribute("ParentSerialNo", parentSerialNo);
		inDocRootElement.setAttribute("NodeLevel", nodeLevel);		
		inDocForService.appendChild(inDocRootElement);

		//System.out.println("NWCGSerialRecordProcessor: insertRow: inDocForService\n" + XMLUtil.getXMLString(inDocForService));
		CommonUtilities.invokeService(env, "NWCGCreateSerialRecord", inDocForService);		
	}
}