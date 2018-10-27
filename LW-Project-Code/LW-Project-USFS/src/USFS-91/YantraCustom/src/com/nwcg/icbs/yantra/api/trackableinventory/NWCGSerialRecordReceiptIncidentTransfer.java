package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSerialRecordReceiptIncidentTransfer extends NWCGSerialRecordReceipt implements NWCGSerialRecord 
{
	public Document insertSerialRecord(YFSEnvironment env, Document inDoc) throws Exception 
	{
		if(this.preCheckInput(inDoc))
		{
			NWCGSerialRecordProcessor serialProcessor = new NWCGSerialRecordProcessor();
			Map<String, String> receiptMap = new HashMap<String, String>();

			receiptMap.put("ReceiptNo", null);
			receiptMap.put("ReceiptHeaderKey", null);

			Element rootElement = inDoc.getDocumentElement();

			receiptMap.put("OrderType", rootElement.getAttribute("OrderType"));
			receiptMap.put("DocumentType", rootElement.getAttribute("DocumentType"));
			receiptMap.put("TransactionType", this.getTransactionType());
			receiptMap.put("Node", rootElement.getAttribute("Node"));
			receiptMap.put("OrderNo", rootElement.getAttribute("OrderNo"));
			receiptMap.put("OrderHeaderKey", rootElement.getAttribute("OrderHeaderKey"));			
			receiptMap.put("IncidentNo", rootElement.getAttribute("IncidentNo"));
			receiptMap.put("IncidentYear", rootElement.getAttribute("IncidentYear"));

			NodeList receiptLineList = rootElement.getElementsByTagName("ReceiptLine");
			for(int j = 0; j < receiptLineList.getLength(); j++)
			{
				Element receiptLineElement = (Element)receiptLineList.item(j);		
				ArrayList<NWCGTrackableItem> serialList = new ArrayList<NWCGTrackableItem>(); 

				String itemId = receiptLineElement.getAttribute("ItemID");
				String serialNo = receiptLineElement.getAttribute("SerialNo");
				String orderLineKey = receiptLineElement.getAttribute("OrderLineKey");

				if(serialNo != null && serialNo.trim().length() > 0)
				{
					serialList.add(new NWCGTrackableItem(itemId, serialNo, null));	
					NWCGSerialRecordProcessor.insertRow(env, itemId, serialNo, orderLineKey, null, null, null, NWCGTrackableItem.PARENT, receiptMap);
					serialProcessor.insertChildSerialNumbers(env, serialList, orderLineKey, null, receiptMap);
				}
			}
		}
		return inDoc;
	}
}
