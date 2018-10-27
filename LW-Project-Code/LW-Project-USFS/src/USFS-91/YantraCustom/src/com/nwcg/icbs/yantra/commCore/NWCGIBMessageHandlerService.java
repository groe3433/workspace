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

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandlerInterface;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

public class NWCGIBMessageHandlerService implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGIBMessageHandlerService.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	// is call once a message is retrieved from JMS queue
	public Document process(YFSEnvironment env, Document xml) throws Exception {
		logger.verbose("@@@@@ Entering NWCGIBMessageHandlerService::process");
		logger.verbose("@@@@@ Input XML: " + XMLUtil.getXMLString(xml));
		String serviceName = xml.getFirstChild().getNodeName();
		// String serviceGroup = NWCGAAUtil.determineServiceGroup(serviceName);
		String serviceGroup = "";
		logger.verbose("@@@@@ ServiceName:" + serviceName);
		NWCGMessageHandlerInterface handler = NWCGMessageHandlerFactory.getHandler(serviceName);
		Document resp_msg = handler.process(env, xml); 
		// get the distID. ??
		// String distID = NWCGAAUtil.lookupNodeValue(xml,"distributionID");
		String distID = "";
		// Get the latest message key(message key) from NWCG_INBOUND_MESSAGE
		// for the DistributionID
		Document inXML = XMLUtil.getDocument("<NWCGInboundMessage DistributionID=" + distID + " />");
		Document opXML = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME, inXML);
		logger.verbose("@@@@@ opXML:" + XMLUtil.getXMLString(opXML));
		NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
		// Update message store
		// updateMessageStatusToProcessing(env, xml, serviceName, resp_msg, distID, opXML, msgStore);
		// Post the resp_msg to ESB synchronously.
		String resp_msg_str = "";
		// NWCGAAUtil.buildXMLDocument(resp_msg_str);
		logger.verbose("@@@@@ Exiting NWCGIBMessageHandlerService::process");
		return null;
	}

	/**
	 * @param env
	 * @param xml
	 * @param serviceName
	 * @param resp_msg
	 * @param distID
	 * @param opXML
	 * @param msgStore
	 */
	private void updateMessageStatusToProcessing(YFSEnvironment env, Document xml, String serviceName, Document resp_msg, String distID, Document opXML, NWCGMessageStore msgStore) {
		logger.verbose("@@@@@ Entering NWCGIBMessageHandlerService::updateMessageStatusToProcessing");
		if (opXML.getDocumentElement().getChildNodes().getLength() != 0) {
			NodeList nwcgIBMsgNL = opXML.getElementsByTagName("NWCGInboundMessage");
			for (int i = 0; i < nwcgIBMsgNL.getLength(); i++) {
				Element nwcgIBMsgElem = (Element) nwcgIBMsgNL.item(i);
				String latest_msg_key = nwcgIBMsgElem.getAttribute("MessageKey");
				String messageStatus = nwcgIBMsgElem.getAttribute("MessageStatus");
				if (!messageStatus.equals("VOID")) {
					if (resp_msg != null) {
						String xmlStr = XMLUtil.getXMLString(resp_msg);
						try {
							logger.verbose("@@@@@ updating message");
							msgStore.updateMessage(distID, NWCGAAConstants.MESSAGE_DIR_TYPE_IB, xmlStr, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_PROCESSING, NWCGAAConstants.SYSTEM_NAME, latest_msg_key, true, serviceName);
						} catch (Exception e) {
							logger.error("!!!!! Exception occured while updating message with msg status - PROCESSING");
							CommonUtilities.raiseAlert(env, NWCGAAConstants.QUEUEID_DEFAULT, "", xml, e, null);
						}
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGIBMessageHandlerService::updateMessageStatusToProcessing");
	}
}