package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSerialRecordShipmentIncidentTransfer extends NWCGSerialRecordShipment implements NWCGSerialRecord 
{
	public Document insertSerialRecord(YFSEnvironment env, Document inDoc) throws Exception 
	{
		if(this.preCheckInput(inDoc))
		{
			NWCGSerialRecordProcessor serialProcessor = new NWCGSerialRecordProcessor();
			Map<String, String> shipmentMap = new HashMap<String, String>();

			shipmentMap.put("ReceiptNo", null);
			shipmentMap.put("ReceiptHeaderKey", null);

			Element rootElement = inDoc.getDocumentElement();

			shipmentMap.put("OrderType", rootElement.getAttribute("OrderType"));
			shipmentMap.put("DocumentType", rootElement.getAttribute("DocumentType"));
			shipmentMap.put("TransactionType", this.getTransactionType());
			shipmentMap.put("Node", rootElement.getAttribute("Node"));
			shipmentMap.put("OrderNo", rootElement.getAttribute("OrderNo"));
			shipmentMap.put("OrderHeaderKey", rootElement.getAttribute("OrderHeaderKey"));			
			shipmentMap.put("IncidentNo", rootElement.getAttribute("IncidentNo"));
			shipmentMap.put("IncidentYear", rootElement.getAttribute("IncidentYear"));

			NodeList shipmentLineList = rootElement.getElementsByTagName("ShipmentLine");
			for(int j = 0; j < shipmentLineList.getLength(); j++)
			{
				Element shipmentLineElement = (Element)shipmentLineList.item(j);		
				NodeList shipTagSerialList = shipmentLineElement.getElementsByTagName("ShipmentTagSerial");
				ArrayList<NWCGTrackableItem> serialList = new ArrayList<NWCGTrackableItem>(); 

				String itemId = shipmentLineElement.getAttribute("ItemID");
				String orderLineKey = shipmentLineElement.getAttribute("OrderLineKey");

				//following ensures that we are dealing with serial numbers only
				for(int k = 0; k < shipTagSerialList.getLength(); k++)
				{
					Element shipTagSerialElement = (Element)shipTagSerialList.item(k);
					String serialNo = shipTagSerialElement.getAttribute("SerialNo");
					serialList.add(new NWCGTrackableItem(itemId, serialNo, null));
					NWCGSerialRecordProcessor.insertRow(env, itemId, serialNo, orderLineKey, null, null, null, NWCGTrackableItem.PARENT, shipmentMap);
				}

				serialProcessor.insertChildSerialNumbers(env, serialList, orderLineKey, null, shipmentMap);
			}
		}
		return inDoc;
	}
}