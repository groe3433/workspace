package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateReservationSNo implements YIFCustomApi {

	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub

	}
	/*
	 * Input: 
	 * <ValidationRequest RequestNo="" IncidentNo=""/>
	 * Response:
	 * <ValidationResponse Result="Y/N" Message="Faliure message"/>
	 *  
	 */
	public Document validateSNo(YFSEnvironment env,Document inDoc) throws Exception{
		
		//System.out.println("validateSNo inDoc:::: "+ XMLUtil.getXMLString(inDoc));
		
		Element rootElem = inDoc.getDocumentElement();
		//String strDocumentType = rootElem.getAttribute("DocumentType");
		NodeList nlOrderLines = rootElem.getElementsByTagName("OrderLines");
		// get the orderlines this is help us to remove the orderline elements 
		if(nlOrderLines != null && nlOrderLines.getLength() >= 1)
		{
			Element elemOrderLines = (Element) nlOrderLines.item(0);
			// this will carry all the nodes to be remvoed
			//ArrayList removeList = new ArrayList(20);
			
			NodeList nlOrderLine= elemOrderLines.getElementsByTagName("OrderLine");
			
			if(nlOrderLine != null)
			{
				int iTotal = nlOrderLine.getLength() ;
				
				// for all orderline(s)
				for(int index = 0 ; index < iTotal ; index++)
				{
					Element elemOrderLine = (Element) nlOrderLine.item(index);
					NodeList nlItem = elemOrderLine.getElementsByTagName("Item");
					String strOrderLineKey = elemOrderLine.getAttribute("OrderLineKey");
					//System.out.println("strOrderLineKey-"+ index + " " + strOrderLineKey);
					
					Document getOrderLineDetailsInput = XMLUtil.createDocument("OrderLineDetail");
					getOrderLineDetailsInput.getDocumentElement().setAttribute("OrderLineKey",strOrderLineKey);
					env.setApiTemplate("getOrderLineDetails","NWCGCheckReservationRequestNo_getOrderLineDetails");
					Document returnDoc = CommonUtilities.invokeAPI(env,"getOrderLineDetails",getOrderLineDetailsInput);
					env.clearApiTemplate("getOrderLineDetails");
					System.out.println("returnDoc using NWCGCheckReservationRequestNo_getOrderLineDetails: "+ XMLUtil.getXMLString(returnDoc));
					
					NodeList nlOrderLineExtn = returnDoc.getDocumentElement().getElementsByTagName("Extn");
					int nlLength = nlOrderLineExtn.getLength();
					for(int i=0;i<nlLength;i++){
						Element olElm = (Element) nlOrderLineExtn.item(i);
						String strExtnRequestNo = olElm.getAttribute("ExtnRequestNo");
						//System.out.println("==================================");
						System.out.println("/////strExtnRequestNo/////" + strExtnRequestNo);
						
						
						if(strExtnRequestNo.equals(""))	
						{
							//System.out.println("******************throw ERROR******************");
							throw new NWCGException("NWCG_VALIDATE_REQUEST_NO_003");
						} else {
							System.out.println("do nothing");
						}
						//System.out.println("==================================");
						
					}
					
				}
				
			}
    	}
    				
    	
		return inDoc;

	}
		
}
