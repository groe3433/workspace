package com.nwcg.icbs.yantra.api.trackableinventory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;

public class NWCGSerialRecordReceipt 
{
	/**
	 * The intent of this function is to quickly skim through the input XML for a clue 
	 * (i.e. contains a serial number) that qualifies the input to be examined further.
	 * This particular implementation checks for first non-empty SerialNo in ReceiptLine element.
	 * @param inDoc
	 * @return
	 */
	protected boolean preCheckInput(Document inDoc)
	{
		Element rootElement = inDoc.getDocumentElement();		
		NodeList receiptLineList = rootElement.getElementsByTagName("ReceiptLine");
		for(int i = 0; i < receiptLineList.getLength(); i++)
		{
			Element receiptLineElement = (Element)receiptLineList.item(i);
			String serialNo = receiptLineElement.getAttribute("SerialNo");
			if(serialNo != null && serialNo.trim().length() > 0){ 
				return true;
			}
		}
		return false;
	}
	
	protected String getTransactionType()
	{
		return NWCGConstants.SERIAL_RECORD_TRANS_TYPE_RECEIPT;
	}
}
