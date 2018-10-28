package com.nwcg.icbs.yantra.api.returns;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGReturnItemlist implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	public Document prepareList(YFSEnvironment env, Document inXML)
	throws Exception {
		Document outDoc = XMLUtil.createDocument("ReturnItemList");
		Element eleOutDoc = outDoc.getDocumentElement();
		NodeList ReceiptLineNL = inXML.getElementsByTagName("ReceiptLine");
		int recNLLen = ReceiptLineNL.getLength();
		for(int i=0;i<recNLLen;i++){
			String strItemID = "";
			String strQuant = "";
			String strLPN = "";
			String strKey="";
			Element eleReceiptLine = (Element)ReceiptLineNL.item(i);
			
			if(eleReceiptLine.hasAttribute("PrimarySerialNo"))
			{
				strItemID = eleReceiptLine.getAttribute("PrimarySerialNo");
				strQuant = "1";
			}
			else
			{
				strItemID = eleReceiptLine.getAttribute("ItemID");
				strQuant = eleReceiptLine.getAttribute("QtyReturned");
			}
			if(eleReceiptLine.hasAttribute("LPNNo"))
			{
				strLPN = eleReceiptLine.getAttribute("LPNNo");
			}
			strKey = eleReceiptLine.getAttribute("value");
			Element eleItem = outDoc.createElement("Item");
			eleItem.setAttribute("ItemID", strItemID);
			eleItem.setAttribute("Quant", strQuant);
			eleItem.setAttribute("LPNNo", strLPN);
			eleItem.setAttribute("Key", strKey);
			eleOutDoc.appendChild(eleItem);
		}
		System.out.println("%%%%%%%%%&&&&&&&&&&&########## "+XMLUtil.getXMLString(outDoc));
		return outDoc;
		
	}

}