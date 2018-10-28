package com.nwcg.icbs.yantra.condition.refurb;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRefurbTransferCheck implements YCPDynamicConditionEx 
{
	public boolean evaluateCondition(YFSEnvironment env, String name, Map mapData, Document inDoc) 
	{
//		System.out.println("NWCGRefurbTransferCheck:evaluateCondition:inDoc\n" + XMLUtil.getXMLString(inDoc));
//		System.out.println("NWCGRefurbTransferCheck:evaluateCondition:mapData: " + mapData.toString());

		boolean result = false;
		String orderType = null;
		String documentType = null;

		if(mapData != null && mapData.size() > 0) 
		{
			//mapData may not have DocumentType
			Object docType = mapData.get(NWCGConstants.DOCUMENT_TYPE);
			documentType = (docType != null)? docType.toString() : documentType;
		}

		Element rootElement = inDoc.getDocumentElement();
		documentType = (documentType != null)? documentType : rootElement.getAttribute("DocumentType");
		
//Begin CR383 01102013
                               //PurchaseOrder does not have orderType attribute
                              if(!documentType.equals("0006"))
                              {
                               return true;
                              }
//End CR383 01102013
		
                               //Cache-To-Cache Transfer Order has orderType attribute
                               if(rootElement.getNodeName().equalsIgnoreCase("Shipment"))
		{
			orderType = rootElement.getAttribute("OrderType");
		}
		else if(rootElement.getNodeName().equalsIgnoreCase("Receipt"))
		{
			NodeList receiptLineList = rootElement.getElementsByTagName("ReceiptLine");
			if(receiptLineList != null && receiptLineList.getLength() > 0)
			{
				Element receiptLineFirstElement = (Element)receiptLineList.item(0);
				orderType = ((Element)receiptLineFirstElement.getElementsByTagName("Order").item(0)).getAttribute("OrderType");
			}
		}

		result = ((documentType != null && documentType.equals("0006")) && (orderType != null && orderType.equals(NWCGConstants.ORDER_TYPE_REFURB_TRANSFER)))? true : false;
		System.out.println("NWCGRefurbTransferCheck:evaluateCondition:result: " + result);
		return result;
	}

	public void setProperties(Map map) 
	{}
}