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

import gov.nwcg.services.ross.common_types._1.ApplicationSystemType;
import gov.nwcg.services.ross.common_types._1.SystemTypeSimpleType;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsReq;
import gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderInterface;
import gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderService;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.soap.SOAPElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.ibm.websphere.webservices.soap.IBMSOAPElement;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * @author sdas
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGGetOperationResultsAPI implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGGetOperationResultsAPI.class);

	private Properties props;

	public void setProperties(Properties prop) throws Exception {
		this.props = prop;
	}

	public Document getOperationResults(YFSEnvironment env, Document inDoc) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::getOperationResults ");
		String userName = "";
		SOAPElement sElement = null;
		String distID = inDoc.getDocumentElement().getAttribute("DistributionID");
		String latest_message_key = apiCallToGetLatestMessageKeyForDistID(env, distID);
		logger.verbose("@@@@@ latest_message_key:" + latest_message_key);
		// Get the loginID and Username from Yantra
		String loginID = NWCGAAConstants.USER_NAME;
		String system_name = env.getSystemName();
		logger.verbose("@@@@@ LoginID::" + loginID);
		logger.verbose("@@@@@ system_name::" + system_name);
		// Username should map with loginid
		userName = loginID;
		String pw_key = NWCGTimeKeyManager.createKey(latest_message_key, loginID, system_name);
		logger.verbose("@@@@@ pw_key :" + pw_key);
		String message_name = inDoc.getDocumentElement().getNodeName();
		logger.verbose("@@@@@ message_name:" + message_name);
		String string_token = NWCGJAXRPCWSHandlerUtils.getStringToken(userName, loginID, pw_key, message_name);
		logger.verbose("@@@@@ string token :" + string_token);
		// Make a Yantra API call to get the latest message status for the given distribution ID.
		String latestMsgStatus = apiCallToGetLatestMsgStatusForDistID(env, distID);
		try {
			sElement = createGetOperationsResultsReq(string_token, distID);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e.toString());
			return formReturnResult("-1", e.toString(), latestMsgStatus);
		}
		if (sElement == null)
			return formReturnResult("0", "No operation result found!", latestMsgStatus);
		else {
			IBMSOAPElement ibmSoapElem = (IBMSOAPElement) sElement;
			logger.verbose("@@@@@ soap Elem:" + ibmSoapElem.toXMLString(true));
		}
		Element eleReturned = sElement;
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::getOperationResults ");
		return processReturnedOperationResults(env, eleReturned, distID, latestMsgStatus, latest_message_key);
	}

	/**
	 * @param env
	 * @param distID
	 *            This method transforms the returned XML from ROSS to a format
	 *            that can be displayed on screen to the user.
	 */
	public Document processReturnedOperationResults(YFSEnvironment env,
			Element inElem, String distID, String latestMsgStatus,
			String latest_message_key) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::processReturnedOperationResults ");
		// Element inElem = retDoc.getDocumentElement();

		NodeList nlRetCode = inElem.getElementsByTagName("ReturnCode");
		NodeList nlRetDesc = inElem.getElementsByTagName("Description");
		NodeList nlRespStatus = inElem
				.getElementsByTagName("ro:ResponseStatus");

		Element eleRetCode = (Element) nlRetCode.item(0);
		Element eleRetDesc = (Element) nlRetDesc.item(0);
		Element eleRespStatusElem = (Element) nlRespStatus.item(0);

		String respObjStr = "";
		String respStatusStr = "";

		if (eleRetDesc != null) {
			respObjStr = XMLUtil.getNodeValue(eleRetDesc);
		}

		if (eleRetCode != null) {
			respStatusStr = XMLUtil.getNodeValue(eleRetCode);
		}

		Document outDoc = null;

		try {
			outDoc = XMLUtil.createDocument("GetOperationResultsReq");
		} catch (Exception e) {
			//logger.printStackTrace(e);
		}
		Document docDeliverResp = null;
		try {
			// docDeliverResp =
			// XMLUtil.createDocument("DeliverOperationResults");
			docDeliverResp = XMLUtil
					.getDocument(NWCGAAConstants.DELIVER_OPERATION_RESULTS_ROOT);
		} catch (Exception e) {
			//logger.printStackTrace(e);
		}

		eleRespStatusElem.getParentNode().removeChild(eleRespStatusElem);

		NodeList nl = inElem.getChildNodes();
		Element eleRetDocEleForDelivery = null;

		for (int x = 0; x < nl.getLength(); x++) {
			if (nl.item(x).getNodeType() == 1) {
				eleRetDocEleForDelivery = (Element) nl.item(x);
			}

		}
		if (docDeliverResp != null) {

			Element eleDeliverRoot = docDeliverResp.getDocumentElement();

			if (eleRetDocEleForDelivery != null) {
				eleDeliverRoot.setAttribute("DistributionID", distID);
				Node node = docDeliverResp.importNode(eleRetDocEleForDelivery,
						true);
				eleDeliverRoot.appendChild(node);
			}
		}

		Element eleRoot = outDoc.getDocumentElement();
		eleRoot.setAttribute("DistributionID", distID);
		eleRoot.setAttribute("ReturnedMessage",
				XMLUtil.getElementXMLString(inElem));
		eleRoot.setAttribute("LatestMessageStatus", latestMsgStatus);
		eleRoot.setAttribute("ReturnCode", respStatusStr);
		eleRoot.setAttribute("DescriptionFromRoss", respObjStr);
		eleRoot.setAttribute("DeliverToRossMessage",
				XMLUtil.getXMLString(docDeliverResp));
		eleRoot.setAttribute("LatestMessageKey", latest_message_key);
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::processReturnedOperationResults ");
		return outDoc;
	}

	public static String apiCallToGetLatestMessageKeyForDistID(YFSEnvironment env, String distID) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::apiCallToGetLatestMessageKeyForDistID ");
		String latestMsgKey = "";
		try {
			Document inDoc = XMLUtil
					.getDocument("<NWCGOutboundMessage DistributionID=\""
							+ distID + "\" />");
			Document opDoc = CommonUtilities
					.invokeService(
							env,
							NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME,
							inDoc);
			if (opDoc != null) {
				NodeList nl = opDoc.getElementsByTagName("NWCGOutboundMessage");
				for (int i = 0; i < nl.getLength(); i++) {
					Element elem = (Element) nl.item(i);
					String msgType = elem.getAttribute("MessageType");
					if (!msgType.equals("START".trim())) {
						latestMsgKey = elem.getAttribute("MessageKey");
					}
				}
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : ", e);
		}

		if (!StringUtil.isEmpty(latestMsgKey)) {
			logger.verbose("@@@@@ latestMsgKey :: " + latestMsgKey);
			logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::apiCallToGetLatestMessageKeyForDistID ");
			return latestMsgKey;
		} else { 
			latestMsgKey = ResourceUtil.get("NWCG_DO_NO_AUTH_DB_KEY", "9011917122761465");
			logger.verbose("@@@@@ latestMsgKey :: " + latestMsgKey);
			logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::apiCallToGetLatestMessageKeyForDistID ");
			return latestMsgKey;
		}
	}

	/**
	 * @param env
	 * @param distID
	 */
	public static String apiCallToGetLatestMsgStatusForDistID(YFSEnvironment env, String distID) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::apiCallToGetLatestMsgStatusForDistID ");
		String msgStatus = "";
		try {
			Document inDoc = XMLUtil
					.getDocument("<NWCGOutboundMessage DistributionID=\""
							+ distID + "\" />");
			Document opDoc = CommonUtilities
					.invokeService(
							env,
							NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME,
							inDoc);

			if (opDoc != null) {
				NodeList nl = opDoc.getElementsByTagName("NWCGOutboundMessage");
				for (int i = 0; i < nl.getLength(); i++) {
					Element elem = (Element) nl.item(i);
					String msgType = elem.getAttribute("MessageType");
					if (!msgType.equals("START".trim())) {
						msgStatus = elem.getAttribute("MessageStatus");
					}
				}
			}

			logger.verbose("latest message status :" + msgStatus);
			return msgStatus;
		} catch (Exception e) {
			logger.error("!!!!! Exception thrown in method :", e);
		}

		msgStatus = "NULL";
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::apiCallToGetLatestMsgStatusForDistID ");
		return msgStatus;
	}

	/**
	 * @param string_token
	 */
	private SOAPElement createGetOperationsResultsReq(String string_token, String distID) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::createGetOperationsResultsReq ");
		ResourceOrderInterface port = null;
		SOAPElement soapElem = null;

		logger.verbose("dist ID:" + distID);

		ResourceOrderService locator = new ResourceOrderService();
		port = locator.getResourceOrderPort();

		// Invoke the clientside handler

		callGetOperationsHandler(locator);

		GetOperationResultsReq req = new GetOperationResultsReq();
		req.setDistributionID(distID);
		ApplicationSystemType type = new ApplicationSystemType();
		type.setSystemID(string_token);
		type.setSystemType(SystemTypeSimpleType.ICBS);
		req.setSystemOfOrigin(type);
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::createGetOperationsResultsReq ");
		return soapElem;
	}

	/**
	 * @param locator
	 */
	private void callGetOperationsHandler(ResourceOrderService locator) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::callGetOperationsHandler ");
		QName portQName = new QName(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, NWCGAAConstants.RESPORCE_ORDER_PORT);
		HandlerInfo hInfo = new HandlerInfo();
		hInfo.setHandlerClass(com.nwcg.icbs.yantra.handler.NWCGGetOperationResultsHandler.class);
		// locator.getHandlerRegistry().getHandlerChain(portQName).add(hInfo);
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::callGetOperationsHandler ");
	}

	/**
	 * @param soapElem
	 */
	private void processOperationResults(SOAPElement soapElem) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::processOperationResults 1");
		if (soapElem == null)
			return;

		IBMSOAPElement ibmSoapElem = (IBMSOAPElement) soapElem;
		logger.verbose("In NWCGGetOperationResultsAPI");
		logger.verbose("soap Elem:" + ibmSoapElem.toXMLString(true));
		logger.verbose("soap elem name:" + soapElem.getNodeName());

		Iterator iter = soapElem.getChildElements();
		while (iter.hasNext()) {
			logger.verbose("iter.getClass() is: " + iter.getClass());
			logger.verbose("iter.next() is: " + iter.next());
			SOAPElement childElem = (SOAPElement) iter.next();
			logger.verbose("child node name:" + childElem.getNodeName());
		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::processOperationResults 1");
	}

	/**
	 * @param soapElem
	 * @param latestMsgStatus
	 */
	private Document generateOperationResults(SOAPElement soapElem, String latestMsgStatus) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::processOperationResults 2");
		String respStatusStr = "";
		String respObjStr = "";
		Element eleInput = soapElem;
		Document docOut = null;

		try {
			docOut = XMLUtil.getDocumentForElement(eleInput);

		} catch (Exception e) {
			//logger.printStackTrace(e);

		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::processOperationResults 2");
		return docOut;
	}

	/**
	 * @param retCode
	 * @return
	 */
	private String returnMeaningfulROSSStatusToUser(String retCode) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::returnMeaningfulROSSStatusToUser");
		if (retCode.equals(NWCGAAConstants.ROSS_RET_SUCCESS_VALUE)) {
			logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::returnMeaningfulROSSStatusToUser (success)");
			return retCode.concat(NWCGAAConstants.ROSS_RET_SUCCESS_CODE);
		} else {
			logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::returnMeaningfulROSSStatusToUser (failure)");
			return retCode.concat(NWCGAAConstants.ROSS_RET_FAILURE_CODE);
		}
	}

	/**
	 * @param respObjStr
	 * @return
	 */
	private String getReturnCodeFromSOAPMessage(String respObjStr) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::getReturnCodeFromSOAPMessage");
		String resStr = "";
		try {
			Document d = XMLUtil.getDocument(respObjStr);
			Element valElem = d.getDocumentElement();

			logger.verbose("class name:" + valElem.getClass().getName());

			NodeList nl = valElem.getChildNodes();
			for (int a = 0; a < nl.getLength(); a++) {
				Object obj = nl.item(a);
				logger.verbose("node class name:" + obj.getClass().getName());
				if (obj instanceof org.apache.xerces.dom.DeferredTextImpl) {
					org.apache.xerces.dom.DeferredTextImpl defTextImpl = (org.apache.xerces.dom.DeferredTextImpl) obj;
					resStr = defTextImpl.getTextContent();
					logger.verbose("str value :" + resStr);

				}
			}
		} catch (Exception e) {
			logger.error("!!!!! Exception thrown:", e);
		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::getReturnCodeFromSOAPMessage");
		return resStr;
	}

	private Document formReturnResult(String respStatusStr, String respObjStr, String latestMsgStatus) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAPI::formReturnResult");
		try {
			Document getOperationResults = XMLUtil.createDocument("ROSSOperationResult");
			getOperationResults.getDocumentElement().setAttribute("ResponseStatus", respStatusStr);
			getOperationResults.getDocumentElement().setAttribute("ResponseMsg", respObjStr);
			getOperationResults.getDocumentElement().setAttribute("ICBSStatus", latestMsgStatus);
			logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAPI::formReturnResult ");
			return getOperationResults;
		} catch (Exception e) {
			logger.error("!!!!! Exiting NWCGGetOperationResultsAPI::formReturnResult (failure)");
			return null;
		}
	}
}