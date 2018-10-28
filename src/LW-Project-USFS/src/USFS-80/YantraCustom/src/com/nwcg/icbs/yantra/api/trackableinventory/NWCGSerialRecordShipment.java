package com.nwcg.icbs.yantra.api.trackableinventory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;

public class NWCGSerialRecordShipment 
{
	/**
	 * The intent of this function is to quickly skim through the input XML for a clue 
	 * (i.e. contains a serial number) that qualifies the input to be examined further.
	 * This particular implementation checks if ShipmentTagSerial elements exist.
	 * @param inDoc
	 * @return
	 */
	protected boolean preCheckInput(Document inDoc)
	{
		Element rootElement = inDoc.getDocumentElement();
		NodeList shipmentLineList = rootElement.getElementsByTagName("ShipmentLine");
		for(int i = 0; i < shipmentLineList.getLength(); i++)
		{
			Element shipmentLineElement = (Element)shipmentLineList.item(i);		
			NodeList shipTagSerialList = shipmentLineElement.getElementsByTagName("ShipmentTagSerial");
			if(shipTagSerialList != null && shipTagSerialList.getLength() > 0){
				return true;
			}
		}
		return false;
	}
	
	protected String getTransactionType()
	{
		return NWCGConstants.SERIAL_RECORD_TRANS_TYPE_SHIPMENT;
	}	
}
