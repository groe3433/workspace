package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCreateIssue implements YIFCustomApi {

	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public Document createOrder(YFSEnvironment env, Document inDoc) throws Exception{
		
		
		Element order = inDoc.getDocumentElement();
		//process the order header 
		processOrderHeader(inDoc,order);
		
		Element orderLines = (Element)XMLUtil.getChildNodeByName(order,"OrderLines");
		NodeList orderLineList = orderLines.getElementsByTagName("OrderLine");
		
		for(int i=0;i<orderLineList.getLength();i++){
			Element orderLine = (Element)orderLineList.item(i);
			//process the order line
			processOrderLine(inDoc,orderLine);
		}

		Document returnDoc = CommonUtilities.invokeAPI(env,"createOrder",inDoc);
		return returnDoc;
	}
	
	/*
	 * Process the order header and the extended fields associated with the
	 * order header
	 */
	protected void processOrderHeader(Document inDoc,Element order) throws Exception{
		
	}
	

	protected void processOrderLine(Document inDoc,Element orderLine) throws Exception{
		Element orderLineExtn = (Element)XMLUtil.getChildNodeByName(orderLine,"Extn");
		if(null==orderLineExtn){
			// there is no extended field in the input so add it
			orderLineExtn = inDoc.createElement("Extn");
			orderLine.appendChild(orderLineExtn);
		}
		//check for the backorder qty
		String backOrderQty = orderLineExtn.getAttribute("ExtnBackorderedQty");
		if(!StringUtil.isEmpty(backOrderQty)){
			orderLineExtn.setAttribute("ExtnBackOrderFlag","Y");
		}
		
	}

}
