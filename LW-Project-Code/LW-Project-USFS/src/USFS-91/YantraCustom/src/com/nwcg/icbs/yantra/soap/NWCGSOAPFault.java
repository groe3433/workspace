/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

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

import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGSOAPFault {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGSOAPFault.class);

	public NWCGSOAPFault() {
	}

	public static SOAPFault createParentSOAPFault(String errorMsg) {
		return createParentSOAPFault(null, errorMsg);
	}

	public static SOAPFault createParentSOAPFault(SOAPFaultException sfe, String errorMsg) {
		logger.verbose("@@@@@ Entering NWCGSOAPFault::createParentSOAPFault ");

		SOAPFault retVal = null;
		Element incDocElm = null;
		try {
			// NWCGAnAEnvironment.properties = "ICBS.SoapFaultActor"
			//String soapFaultActor = ResourceUtil.get("ICBS.SoapFaultActor", "http://sundance.lw-lmco.com:9087/ICBSRWebServices/ICBSRInboundAsyncService");
			String soapFaultActor = YFSSystem.getProperty("ICBS.SoapFaultActor");
			logger.verbose("@@@@@ soapFaultActor :: " + soapFaultActor);
			
			if (sfe != null) {
				SOAPFault incomingSoapFault = sfe.getFault();
				Document incSoapFaultDoc = incomingSoapFault.getOwnerDocument();
				incDocElm = incSoapFaultDoc.getDocumentElement();
			}
			MessageFactory mf = MessageFactory.newInstance();
			SOAPMessage msg = mf.createMessage();
			SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
			SOAPBody body = env.getBody();
			retVal = body.addFault();
			Name soapFaultCodeName = env.createName("Server", "SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
			retVal.setFaultCode(soapFaultCodeName);
			retVal.setFaultActor(soapFaultActor);
			if (StringUtil.isEmpty(errorMsg)) {
				retVal.setFaultString("ICBSR failed to validate with the VerifySecuritySessionService");
			} else {
				retVal.setFaultString(errorMsg);
			}
			if (sfe != null) {
				Detail faultDetail = retVal.addDetail();
				Document ownerDoc = faultDetail.getOwnerDocument();
				ownerDoc.importNode(incDocElm, true);
			}
		} catch (SOAPException se) {
			logger.error("!!!!! Caught SOAPException : " + se.getMessage(),se);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e.getMessage(),e);
		}
		logger.verbose("@@@@@ Exiting NWCGSOAPFault::createParentSOAPFault ");
		return retVal;
	}
}