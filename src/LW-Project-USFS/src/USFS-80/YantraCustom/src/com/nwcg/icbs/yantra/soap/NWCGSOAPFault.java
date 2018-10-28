package com.nwcg.icbs.yantra.soap;

import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;

public class NWCGSOAPFault {
	
	public NWCGSOAPFault() {}
	
	public static SOAPFault createParentSOAPFault(String errorMsg) {
		return createParentSOAPFault(null, errorMsg);
	}
	
	public static SOAPFault createParentSOAPFault(SOAPFaultException sfe, String errorMsg){
		
		NWCGLoggerUtil.Log.info("Inside NWCGSoapFault.createParentSOAPFault()");
		SOAPFault retVal = null;
		Element incDocElm = null;
	 	try
        {	
	 		String soapFaultActor = ResourceUtil.get("ICBS.SoapFaultActor",
	 			"http://sundance.lw-lmco.com:9087/ICBSRWebServices/ICBSRInboundAsyncService");
	 		//String soapFaultCode = NWCGAAConstants.SF_CLIENT_AUTH; 		
	 		
    		if (sfe != null) {
    			SOAPFault incomingSoapFault = sfe.getFault();    		
    			Document incSoapFaultDoc = incomingSoapFault.getOwnerDocument();
    			incDocElm = incSoapFaultDoc.getDocumentElement();
    		}
    		//NodeList nl = XMLUtil.getElementsByTagName(incDocElm, );
    		
            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage msg = mf.createMessage();
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
            SOAPBody body = env.getBody();
            retVal = body.addFault();       
            
            Name soapFaultCodeName = env.createName("Server", 
            		"SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");            
            
            retVal.setFaultCode(soapFaultCodeName);
            retVal.setFaultActor(soapFaultActor);
            
            if (StringUtil.isEmpty(errorMsg)) {
            	retVal.setFaultString("ICBSR failed to validate with the VerifySecuritySessionService");
            }else{
            	retVal.setFaultString(errorMsg);
            }           
            
            if (sfe != null) {
	            Detail faultDetail = retVal.addDetail();
	            Document ownerDoc = faultDetail.getOwnerDocument();
	            ownerDoc.importNode(incDocElm, true);
            }
            //faultDetail.setAttribute("xmlns:webM","http://www.webMethods.com/2001/10/soap/encoding");
            //Name detailEntryName = env.createName("exception","webM","http://www.webMethods.com/2001/10/soap/encoding");
            //DetailEntry detailEntry = faultDetail.addDetailEntry(detailEntryName);
			//Node copiedOrderLine = multiApiInput.importNode(currOrderLine, true);
			//Node rossSoapFaultContents = (Node) retVal;
			
			
            //SOAPElement classNameElem = detailEntry.addChildElement("className","webM");
            //classNameElem.addTextNode("com.wm.lang.xml.WMDocumentException");
            //SOAPElement messageElem = detailEntry.addChildElement("message","webM");
            //messageElem.addTextNode("TBD");*/
        }
        catch(SOAPException se)
        {
        	NWCGLoggerUtil.Log.warning("SOAPException in createSOAPFaultException:"+se.getMessage());
            se.printStackTrace();
        }
        catch (Exception e) {
        	NWCGLoggerUtil.Log.warning("Exception in createSOAPFaultException:"+e.getMessage());
            e.printStackTrace();
        }
        NWCGLoggerUtil.Log.info("Leaving NWCGSoapFault.createParentSOAPFault()");
        return retVal;
	}
}