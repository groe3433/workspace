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

package com.nwcg.icbs.yantra.webservice.ob.reader;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This API is called on receiving JMS message from SDF. It does the following
 * logic - Converts the message to ROSS format - Based on the message name, call
 * the appropriate web service
 * 
 * @author sgunda
 * 
 */
public class NWCGOBReader implements YIFCustomApi {

	// Properties passed in from the SDF, these are from the arguments tab in
	// the SDF
	Properties myProperties = null;

	public void setProperties(Properties myProperties) throws Exception {
		this.myProperties = myProperties;
	}

	/**
	 * This class is triggered after reading a message from queue (by SDF). This
	 * will come into picture only for out bound. All the inbound from A&A will
	 * be an api call to Sterling. So, we are not handling inbound here. Same
	 * goes with GET logic
	 * 
	 * @param msgName
	 * @return integer indicating the operation type for the message name
	 */
	private int getOperationType(String msgName) {
		int operationType = -1;
		String isSync = ResourceUtil.get(msgName
				.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_IS_SYNC));
		String boundType = ResourceUtil.get(msgName
				.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_TYPE));
		String isNotification = ResourceUtil
				.get(msgName
						.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_IS_NOTIFICATION));
		if (isNotification != null
				&& isNotification.equalsIgnoreCase(NWCGAAConstants.STR_TRUE)) {
			operationType = NWCGWebServicesConstant.OBTYPE_NOTIFICATION;
		} else {
			if (boundType != null && isSync != null) {
				if (boundType
						.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)
						&& isSync.equalsIgnoreCase(NWCGAAConstants.STR_TRUE)) {
					// OB Synchronous
					operationType = NWCGWebServicesConstant.OBTYPE_SYNC;
				} else if (boundType
						.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)
						&& isSync.equalsIgnoreCase(NWCGAAConstants.STR_FALSE)) {
					// OB Asynchronous
					operationType = NWCGWebServicesConstant.OBTYPE_ASYNC;
				}
			}
		}
		NWCGLoggerUtil.Log
				.finer("NWCGOBReader::getOperationType, Operation Type : "
						+ operationType);
		return operationType;
	}

	/**
	 * This method will get a message from the queue. Based on the message name,
	 * it will call the appropriate ROSS webservice call. On failure, it will
	 * raise an alert in NWCG_OB_EXCEPTIONS queue
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 */
	public Document processMsgFromQueue(YFSEnvironment env, Document inputDoc) {
		Document opDoc = inputDoc;
		try {
			NWCGLoggerUtil.Log.finest("NWCGOBReader::processMsgFromQueue, "
					+ "Input document : " + XMLUtil.getXMLString(inputDoc));
			if (inputDoc != null) {

				inputDoc = prepareInputDocument(env, inputDoc);
				int operationType = getOperationTypeFromInput(inputDoc);

				if (operationType == -1) {
					NWCGLoggerUtil.Log
							.warning("NWCGOBReader::processMsgFromQueue, Invalid message type and NWCGAnAImpl.properties combination");
				} else {
					NWCGLoggerUtil.Log
							.finer("NWCGOBReader::processMsgFromQueue, Calling NWCGBusinessMsgProcessor");
					Object retnObj = (new NWCGBusinessMsgProcessor())
							.postOutboundMessageToROSS(env, operationType,
									inputDoc);
					NWCGLoggerUtil.Log
							.finer("NWCGOBReader::processMsgFromQueue, Called NWCGBusinessMsgProcessor");
					if (retnObj != null
							&& operationType == NWCGWebServicesConstant.OBTYPE_SYNC) {
						opDoc = (Document) retnObj;
						NWCGLoggerUtil.Log
								.finest("NWCGOBReader::processMsgFromQueue>> response Document ="
										+ XMLUtil
												.extractStringFromDocument(opDoc));
					} else if (retnObj == null) {
						opDoc = null;
					}
				}
			} else {
				NWCGLoggerUtil.Log
						.warning("NWCGOBReader::processMsgFromQueue, input document is null");
			}
		} catch (Exception e) {
			NWCGLoggerUtil.Log
					.warning("NWCGOBReader::processMsgFromQueue, Exception : "
							+ e.getMessage());
			e.printStackTrace();
		}
		return opDoc;
	}

	private int getOperationTypeFromInput(Document inputDoc) {
		int operationType = -1;
		Element inputDocElement = inputDoc.getDocumentElement();
		String operationTypeFromInput = inputDocElement
				.getAttribute(NWCGAAConstants.OPERATION_TYPE);
		/*
		 * Changing the calculation of operation type. If we are creating an
		 * issue for a new incident, then we need to make a register interest
		 * call to ROSS (sync). We will be calling register incident interest
		 * from incident details screens too (async). In order to distinguish
		 * between these calls (sync and async), we are setting the
		 * OperationType variable in the xsl for both create and update. If it
		 * is create, then we will treat this as a sync call.
		 */

		if (!StringUtil.isEmpty(operationTypeFromInput)) {
			if (operationTypeFromInput
					.equalsIgnoreCase(NWCGAAConstants.OPERATION_SYNC)) {
				operationType = NWCGWebServicesConstant.OBTYPE_SYNC;
			} else if (operationTypeFromInput
					.equalsIgnoreCase(NWCGAAConstants.OPERATION_ASYNC)) {
				operationType = NWCGWebServicesConstant.OBTYPE_ASYNC;
			}
		}

		// If we didn't get the operation type from the input DOM doc, then
		// call getOperationType with the message name to pull the correct
		// operation
		// type from NWCGAnAImpl.properties
		if (operationType == -1) {
			operationType = getOperationType(inputDocElement.getLocalName());
		}

		if (operationType == -1) {
			// as a last attempt we will use whatever operationType we are
			// getting in input XML
			// this will be used for get operation results OB call
			try {
				operationType = Integer.parseInt(operationTypeFromInput);
			}
			// catch any exception thrown by application and return -1 to caller
			catch (Exception e) {
				NWCGLoggerUtil.Log
						.warning("OBReader can not determine the operation type, will not post any message to ROSS"
								+ e.getMessage());
				operationType = -1;
			}
		}
		return operationType;
	}

	public Document prepareInputDocument(YFSEnvironment env, Document inputDoc)
			throws YFSException {
		Element inputDocElement = inputDoc.getDocumentElement();
		String msgName = inputDocElement.getLocalName();
		try {
			// Pull the namespace from the properties file
			// <messageName>.namespace
			String namespace = NWCGProperties.getProperty(msgName
					.concat(".namespace"));

			if (StringUtil.isEmpty(namespace)) {
				NWCGLoggerUtil.Log.warning(msgName.concat(".namespace")
						+ " is not configured in NWCGAnAImpl.properties");
				if (msgName.contains("atalog")) {
					namespace = "http://nwcg.gov/services/ross/catalog/1.1";
				} else {
					namespace = "http://nwcg.gov/services/ross/resource_order/1.1";
				}
			}

			// Pull the user id from root/@NWCGUSERID
			String nwcgUserID = inputDocElement.getAttribute("NWCGUSERID");
			if (StringUtil.isEmpty(nwcgUserID)) {
				// If not found, we have to throw an exception

				// TODO: will need to update the message store so that the
				// reprocess agent can pick it up
				YFSException yfs = new YFSException(
						"NWCGUSERID attribute not set in root element!");
				throw yfs;
			}

			inputDocElement.setAttribute(NWCGAAConstants.MDTO_NAMESPACE,
					namespace);
			inputDocElement.setAttribute(NWCGAAConstants.MDTO_USERNAME,
					nwcgUserID);
			inputDocElement.setAttribute(NWCGAAConstants.MDTO_MSGNAME, msgName);
		} catch (YFSException yfe) {
			throw yfe;
		}
		return inputDoc;
	}
}
