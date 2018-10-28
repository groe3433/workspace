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

package com.nwcg.icbs.yantra.commCore;

import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * The purpose of this class is to send a Deliver Operations request message to
 * ROSS using the deliver operations web service client side stub classes. So we
 * need to generate the ROSS deliver operations web service and also its
 * corresponding client side stub classes.
 * 
 * @author sdas
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGIBResponseWebService implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGIBResponseWebService.class);

	public Document recieveMsg(YFSEnvironment env, Document doc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGIBResponseWebService::recieveMsg");
		String code = "";
		String distrID = "";
		String actualElemDocStr = "";
		String serviceName = "";
		Document actualElemDoc = null;
		NodeList nl = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Object obj = nl.item(i);
			if (obj instanceof org.apache.xerces.dom.DeferredElementImpl) {
				Element distrIdElem = (Element) obj;
				if (distrIdElem.getNodeName().equals("DistIDElem")) {
					distrID = distrIdElem.getAttribute("DistID");
				} else if (distrIdElem.getNodeName().equals("MessageElem")) {
					NodeList childNL = distrIdElem.getChildNodes();
					for (int j = 0; j < childNL.getLength(); j++) {
						Object childObj = childNL.item(j);
						logger.verbose("@@@@@ obj 1:" + childObj);
						if (childObj instanceof org.apache.xerces.dom.DeferredElementImpl) {
							Element actualElem = (Element) childObj;
							String nodename = actualElem.getNodeName();
							logger.verbose("@@@@@ node name:" + nodename);
							serviceName = nodename.substring(nodename.indexOf(":") + 1);
							logger.verbose("@@@@@ serviceName :" + serviceName);
							actualElemDoc = XMLUtil.getDocumentForElement(actualElem);
							logger.verbose("@@@@@ actualElem :" + XMLUtil.getXMLString(actualElemDoc));
						}
					}
				}
			} else {
				logger.verbose("@@@@@ else 1");
			}
		}
		actualElemDocStr = XMLUtil.getXMLString(actualElemDoc);
		Document opXML = null;
		String latest_msg_key = "";
		if (serviceName.indexOf("Notification") > 0) {
			logger.verbose("@@@@@ Processing any notification message");
			/**
			 * 
			 * We need to build a inbound notification response somewhat like
			 * this. <SOAP-ENV:Envelope
			 * xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
			 * xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
			 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
			 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			 * <SOAP-ENV:Body> <DeliverNotificationResp
			 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			 * xmlns:cdf="http://www.cdf.ca.gov/CAD" xmlns:rscn=
			 * "http://nwcg.gov/services/ross/resource_notification/1.1"
			 * xmlns:ron
			 * ="http://nwcg.gov/services/ross/resource_order_notification/1.1">
			 * <ResponseStatus> <ReturnCode>0</ReturnCode> </ResponseStatus>
			 * </DeliverNotificationResp> </SOAP-ENV:Body> </SOAP-ENV:Envelope>
			 * 
			 * 
			 */
			ResponseStatusType statType = new ResponseStatusType();
			// Only return code is added at this point ResponseMessageType not added at this point.
			statType.setReturnCode(0);
		} else {
			logger.verbose("@@@@@ Processing any request response message");
			/**
			 * we need to build a request response message response somewhat
			 * like this: <soapenv:Envelope
			 * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
			 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
			 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			 * xmlns:q0="http://nwcg.gov/services/ross/resource_order/1.1">
			 * <soapenv:Body> <ns2:DeliverOperationResultsReq
			 * xmlns:ns2="http://nwcg.gov/services/ross/resource_order/1.1"
			 * xmlns:ns3="http://nwcg.gov/services/ross/common_types/1.1">
			 * <ns2:DistributionID>distID</ns2:DistributionID>
			 * <ro:PlaceResourceRequestResp
			 * xmlns:ro="http://nwcg.gov/services/ross/resource_order/1.1">
			 * <ro:ResponseStatus> <ro:ReturnCode>100</ro:ReturnCode>
			 * </ro:ResponseStatus> </ro:PlaceResourceRequestResp>
			 * </ns2:DeliverOperationResultsReq> </soapenv:Body>
			 * </soapenv:Envelope>
			 */
			SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
			SOAPEnvelope envelop = getSOAPEnvelop(soapMessage);
			SOAPBody soapBody = soapMessage.getSOAPBody();
			SOAPElement deliverOpnResultsReqElem = soapBody.addChildElement(envelop.createName("DeliverOperationResultsReq", "ns2", "http://nwcg.gov/services/ross/resource_order/1.1"));
			deliverOpnResultsReqElem.addNamespaceDeclaration("ns3", "http://nwcg.gov/services/ross/common_types/1.1");
			SOAPElement distributionIDElem = getDistIDSOAPElement("distID", envelop, deliverOpnResultsReqElem);
			SOAPElement placeResourceReqRespElem = deliverOpnResultsReqElem.addChildElement(envelop.createName("PlaceResourceRequestResp", "ro", "http://nwcg.gov/services/ross/resource_order/1.1"));
			SOAPElement respStatElem = placeResourceReqRespElem.addChildElement("ResponseStatus", "ro");
			SOAPElement returnCodeElem = respStatElem.addChildElement("ReturnCode");
			returnCodeElem.addTextNode("100");
			DeliverOperationResultsResp resp = getDeliverOperationsResultsResponse(deliverOpnResultsReqElem);
			code = verifydeliverOperationResultsResp(resp);
			// Once we get the DeliverOperationResultsResp we need to update the message store.
			NWCGMessageStore messageStore = NWCGMessageStore.getMessageStore();
			if (code.equals("100")) {
				logger.verbose("Message processed successfully!!!");
				try {
					messageStore.updateMessage(env, distrID, NWCGAAConstants.MESSAGE_DIR_TYPE_IB, actualElemDocStr, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_PROCESSED, NWCGAAConstants.SYSTEM_NAME, latest_msg_key, true, serviceName);
				} catch (Exception e) {
					logger.error("!!!!! Exception thrown while updating message store" + e.getMessage());
					CommonUtilities.raiseAlert(env, NWCGAAConstants.QUEUEID_INBOUND_EXCEPTION, e.getMessage(), actualElemDoc, e, null);
				}
			} else {
				logger.verbose("@@@@@ Message didn't process");
			}
		}
		logger.verbose("@@@@@ Exiting NWCGIBResponseWebService::recieveMsg");
		return null;
	}

	/**
	 * @param code
	 * @param resp
	 * @return
	 */
	private String verifydeliverOperationResultsResp(DeliverOperationResultsResp resp) {
		logger.verbose("@@@@@ Entering NWCGIBResponseWebService::verifydeliverOperationResultsResp");
		String code = "";
		ResponseStatusType respStatType = resp.getResponseStatus();
		int retCode = respStatType.getReturnCode();
		logger.verbose("@@@@@ retCode ::" + retCode);
		logger.verbose("@@@@@ Exiting NWCGIBResponseWebService::verifydeliverOperationResultsResp");
		return code;
	}

	/**
	 * @param deliverOpnResultsReqElem
	 * @return
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	private DeliverOperationResultsResp getDeliverOperationsResultsResponse(SOAPElement deliverOpnResultsReqElem) {
		logger.verbose("@@@@@ In NWCGIBResponseWebService::getDeliverOperationsResultsResponse");
		return null;
	}

	/**
	 * @param distID
	 * @param envelop
	 * @param deliverOpnResultsReqElem
	 * @throws SOAPException
	 */
	private SOAPElement getDistIDSOAPElement(String distID, SOAPEnvelope envelop, SOAPElement deliverOpnResultsReqElem) throws SOAPException {
		logger.verbose("@@@@@ Entering NWCGIBResponseWebService::getDistIDSOAPElement");
		SOAPElement distIDElem = deliverOpnResultsReqElem.addChildElement(envelop.createName("DistributionID", "ro", "http://nwcg.gov/services/ross/resource_order/1.1"));
		distIDElem.addTextNode(distID);
		logger.verbose("@@@@@ Exiting NWCGIBResponseWebService::getDistIDSOAPElement");
		return distIDElem;
	}

	/**
	 * @param soapMessage
	 * @throws SOAPException
	 */
	private SOAPEnvelope getSOAPEnvelop(SOAPMessage soapMessage) throws SOAPException {
		logger.verbose("@@@@@ Entering NWCGIBResponseWebService::getSOAPEnvelop");
		SOAPEnvelope soapEnvelop = soapMessage.getSOAPPart().getEnvelope();
		soapEnvelop.addNamespaceDeclaration("q0", "http://nwcg.gov/services/ross/resource_order/1.1");
		soapEnvelop.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		soapEnvelop.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
		soapEnvelop.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		logger.verbose("@@@@@ Exiting NWCGIBResponseWebService::getSOAPEnvelop");
		return soapEnvelop;
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}