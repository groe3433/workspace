package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSerialRecordReceiptReturn extends NWCGSerialRecordReceipt implements NWCGSerialRecord 
{
	public Document insertSerialRecord(YFSEnvironment env, Document inDoc) throws Exception 
	{
		if(this.preCheckInput(inDoc))
		{
			NWCGSerialRecordProcessor serialProcessor = new NWCGSerialRecordProcessor();
			Map<String, String> receiptMap = new HashMap<String, String>();

			receiptMap.put("OrderNo", null);
			receiptMap.put("OrderType", null);
			receiptMap.put("OrderHeaderKey", null);

			Element rootElement = inDoc.getDocumentElement();
			String receiptHeaderKey = rootElement.getAttribute("ReceiptHeaderKey");

			receiptMap.put("ReceiptHeaderKey", rootElement.getAttribute("ReceiptHeaderKey"));
			receiptMap.put("DocumentType", rootElement.getAttribute("DocumentType"));
			receiptMap.put("TransactionType", this.getTransactionType());
			receiptMap.put("Node", rootElement.getAttribute("ReceivingNode"));
			receiptMap.put("ReceiptNo", rootElement.getAttribute("ReceiptNo"));

			Document opTemplate = XMLUtil.getDocument("<Receipt ReceiptNo=\"\"><Extn ExtnIncidentNo=\"\" ExtnIncidentYear=\"\" /></Receipt>");
			Document ipDoc = XMLUtil.getDocument("<Receipt ReceiptHeaderKey=\"" + receiptHeaderKey + "\" />");
			Document outDoc = CommonUtilities.invokeAPI(env, opTemplate, "getReceiptDetails", ipDoc);

			Element extnElement = (Element)outDoc.getElementsByTagName("Extn").item(0);
			if(extnElement != null)
			{
				receiptMap.put("IncidentNo", extnElement.getAttribute("ExtnIncidentNo"));
				receiptMap.put("IncidentYear", extnElement.getAttribute("ExtnIncidentYear"));
			}

			NodeList receiptLineList = rootElement.getElementsByTagName("ReceiptLine");
			for(int j = 0; j < receiptLineList.getLength(); j++)
			{
				Element receiptLineElement = (Element)receiptLineList.item(j);		
				ArrayList<NWCGTrackableItem> serialList = new ArrayList<NWCGTrackableItem>(); 

				String itemId = receiptLineElement.getAttribute("ItemID");
				String serialNo = receiptLineElement.getAttribute("SerialNo");
				if(serialNo != null && serialNo.trim().length() > 0)
				{
					String receiptLineKey = receiptLineElement.getAttribute("ReceiptLineKey");
					serialList.add(new NWCGTrackableItem(itemId, serialNo, null));
	
					NWCGSerialRecordProcessor.insertRow(env, itemId, serialNo, null, receiptLineKey, null, null, NWCGTrackableItem.PARENT, receiptMap);
					serialProcessor.insertChildSerialNumbers(env, serialList, null, receiptLineKey, receiptMap);
				}
			}
		}
		return inDoc;
	}
}
