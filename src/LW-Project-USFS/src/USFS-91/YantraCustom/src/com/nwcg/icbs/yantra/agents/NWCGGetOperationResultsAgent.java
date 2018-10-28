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

package com.nwcg.icbs.yantra.agents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGAgentLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGGetOperationResultsOB;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetOperationResultsAgent extends YCPBaseAgent {

	private static YIFApi api;

	private static YFCLogCategory logger = NWCGAgentLogger
			.instance(NWCGGetOperationResultsAgent.class);

	static {
		try {
			api = YIFClientFactory.getInstance().getLocalApi();
		} catch (YIFClientCreationException e) {
			logger.error("!!!!! Caught YIFClientCreationException, Error while instantiating YIFApi :: " + e);
		}
	}

	public void executeJob(YFSEnvironment env, Document obMsgStoreRecord)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsAgent::executeJob ");
		if (obMsgStoreRecord != null) {
			Element docElm = obMsgStoreRecord.getDocumentElement();
			String distID = docElm.getAttribute("DistributionID");
			Document docGetOperRes = prepareGetOperationResultsXML();
			NWCGGetOperationResultsOB getOperationResultsOB = new NWCGGetOperationResultsOB();
			// Determine whether we're running in test mode with no actual API
			// calls made. SOAPMessage is printed instead
			// GetOperationResultsAgent.production_mode
			String getOperationResultsObTM = ResourceUtil
					.get("GetOperationResultsOBAgent.production_mode");
			logger.verbose("@@@@@ getOperationResultsObTM :: "
					+ getOperationResultsObTM);
			if (!StringUtil.isEmpty(getOperationResultsObTM)
					&& (getOperationResultsObTM
							.equalsIgnoreCase(NWCGConstants.YES) || getOperationResultsObTM
							.equalsIgnoreCase(NWCGConstants.STR_TRUE))) {
				getOperationResultsOB.process(env,
						updtDistID(docGetOperRes, distID));
			} else {
				logger.verbose("@@@@@ NWCGGetOperationResultsAgent::executeJob would have sent:");
				logger.verbose("@@@@@ (if GetOperationResultsOBAgent.production_mode=true in NWCGAnAEnvironment.properties)");
				logger.verbose("@@@@@ Distribution ID: " + distID);
				logger.verbose("@@@@@ Modifyts: "
						+ docElm.getAttribute("Modifyts"));
				logger.verbose("@@@@@ XML: "
						+ XMLUtil.extractStringFromDocument(updtDistID(
								docGetOperRes, distID)));
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsAgent::executeJob ");
	}

	/**
	 * This method prepares the GetOperationResultsReq xml
	 * 
	 * @return org.w3c.dom.Document
	 */
	private Document prepareGetOperationResultsXML() throws Exception {

		Document doc = null;
		try {
			doc = XMLUtil.getDocument();

			Element elemGetOperationResultsReq = doc.createElementNS(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE,
					"ro:GetOperationResultsReq");
			doc.appendChild(elemGetOperationResultsReq);
			elemGetOperationResultsReq.setAttribute("NWCGUSERID",
					NWCGAAConstants.ENV_USER_ID);
			elemGetOperationResultsReq.setAttribute(
					NWCGAAConstants.OPERATION_TYPE,
					NWCGAAConstants.OPERATION_SYNC);
			elemGetOperationResultsReq.setAttribute(
					NWCGAAConstants.MDTO_NAMESPACE,
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE);

			Element elmSysOfOrigin = doc.createElementNS(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE,
					"ro:SystemOfOrigin");
			elemGetOperationResultsReq.appendChild(elmSysOfOrigin);

			Element elmSysType = doc.createElement("SystemType");
			elmSysOfOrigin.appendChild(elmSysType);
			elmSysType.setTextContent(NWCGConstants.ICBS_SYSTEM);

			Element elmDist = doc.createElementNS(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE,
					"ro:DistributionID");
			elemGetOperationResultsReq.appendChild(elmDist);
			elmDist.setTextContent(NWCGConstants.EMPTY_STRING);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception, NWCGGetOperations::prepareGetOperationResultsXML :: " + e.getMessage());
			throw e;
		}
		try {
			logger.verbose(XMLUtil.extractStringFromDocument(doc));
		} catch (TransformerException e) {
			logger.error("!!!!! Caught TransformerException, NWCGGetOperations: error calling XMLUtil.extractStringFromDocument :: " + e);
		}
		return doc;
	}

	/**
	 * This method updates the distribution ID to the passed input XML
	 * 
	 * @param doc
	 * @param distID
	 * @return
	 */
	private Document updtDistID(Document doc, String distID) {
		NodeList nlDistID = doc.getElementsByTagName("ro:DistributionID");
		for (int i = 0; i < nlDistID.getLength(); i++) {
			Element elmDistID = (Element) nlDistID.item(0);
			elmDistID.setTextContent(distID);
		}
		return doc;
	}

	/**
	 * This method will get the NWCG_OUTBOUND_MESSAGE records in SENT status
	 * whose modifyts is greater than GetOperationResultsResponseLag ms ago
	 */
	public List getJobs(YFSEnvironment env, Document inputDoc, Document lastMsg)
			throws Exception {
		startTimer("NWCGGetOperationResultsAgent::getJobs()");
		logger.debug("--------------------------------------------------");
		logger.debug("     NWCGGetOperationResultsAgent::getJobs()      ");
		logger.debug("--------------------------------------------------");
		logger.verbose("Input XML Document from agent framework: \n"
				+ XMLUtil.extractStringFromDocument(inputDoc));

		if (lastMsg != null) {
			logger.verbose("Finished trigger at: "
					+ CommonUtilities.getXMLCurrentTime());
			endTimer("NWCGGetOperationResultsAgent::getJobs()");
			return null;
		}

		List<Document> sentList = getOBRecordsOverLagTime(env,
				NWCGAAConstants.MESSAGE_STATUS_OB_MESSAGE_SENT);
		if (sentList == null)
			return null;

		int numInSentStatus = sentList.size();

		if (numInSentStatus != 0) {
			logger.debug("SENT Outbound Msg store records found last modified more than 1.");
		}
		logger.debug("getJobs returning " + numInSentStatus
				+ " msgs back to the agent framework.");
		endTimer("NWCGGetOperationResultsAgent::getJobs()");
		return sentList;
	}

	/**
	 * Create the input for and calls the NWCGGetOperResOBMsgListService
	 * service: <NWCGOutboundMessage MessageStatus="SENT" ModifytsQryType="LT"
	 * Modifyts=
	 * "less than NWCGAnAEnvironment.properties/GetOperationResultsResponseLag"
	 * />
	 * 
	 * @param env
	 * @param String
	 *            msgStatus representing the status we're looking for in the OB
	 *            msg store table.
	 * @return List Documents representing each <NWCGOutboundMessage> record
	 *         returned
	 * @throws Exception
	 */
	private List<Document> getOBRecordsOverLagTime(YFSEnvironment env,
			String msgStatus) throws Exception {

		List<Document> obSentMessages = new ArrayList<Document>();
		String reprocessingTime = ResourceUtil.get(
				NWCGAAConstants.OB_GET_OPERATIONS_WAIT_TIME, "60000");
		Integer repTimeInt = Integer.parseInt(reprocessingTime);
		int milliSeconds = repTimeInt.intValue();
		int seconds = milliSeconds / 1000;

		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Calendar xTimeAgo = Calendar.getInstance();
		xTimeAgo.add(Calendar.SECOND, -seconds);
		String xmlFormattedXTimeAgo = formatter.format(xTimeAgo.getTime());

		Document inGetOBMsgDoc = XMLUtil.createDocument("NWCGOutboundMessage");
		Element obMsgSentDocElm = inGetOBMsgDoc.getDocumentElement();
		obMsgSentDocElm.setAttribute("MessageStatus", msgStatus);
		obMsgSentDocElm.setAttribute("ModifytsQryType", "LT");
		obMsgSentDocElm.setAttribute("Modifyts", xmlFormattedXTimeAgo);

		logger.debug("--------------------------------------------------------");
		logger.debug("Current time: " + CommonUtilities.getXMLCurrentTime());
		logger.debug("1 minute ago: " + xmlFormattedXTimeAgo);
		logger.debug("--------------------------------------------------------");

		logger.verbose("Generated NWCGGetOperResOBMsgListService input xml : "
				+ XMLUtil.extractStringFromDocument(inGetOBMsgDoc));

		Document opGetOBMsgDoc = api.executeFlow(env,
				NWCGAAConstants.GET_OPER_RESU_OB_MSG_LIST_SERVICE,
				inGetOBMsgDoc);

		if (opGetOBMsgDoc != null) {
			logger.verbose("Output from "
					+ NWCGAAConstants.GET_OPER_RESU_OB_MSG_LIST_SERVICE + ": "
					+ XMLUtil.extractStringFromDocument(opGetOBMsgDoc));
		} else {
			logger.debug("Output from "
					+ NWCGAAConstants.GET_OPER_RESU_OB_MSG_LIST_SERVICE
					+ " is null");
			return null;
		}

		List obMsgs = XMLUtil.getElementsByTagName(
				opGetOBMsgDoc.getDocumentElement(), "NWCGOutboundMessage");
		if (!obMsgs.isEmpty()) {
			ListIterator li = obMsgs.listIterator();
			while (li.hasNext()) {
				try {
					Object o = li.next();
					if (o instanceof Element) {
						Document toAddToList = XMLUtil.getDocument();
						Node n = toAddToList.importNode((Element) o, true);
						toAddToList.appendChild(n);
						obSentMessages.add(toAddToList);
						logger.verbose("\nAdding to obSentMessages: "
								+ XMLUtil
										.extractStringFromDocument(toAddToList));
					}
				} catch (Exception e) {
					logger.error("!!!!! Caught General Exception :: " + e);
					throw e;
				}
			}
		} else {
			logger.debug("No outbound message store records found for status "
					+ msgStatus);
		}
		return obSentMessages;
	}
}