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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor;
import com.nwcg.icbs.yantra.webservice.ob.NWCGOBSyncAsyncProcessor;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSException;

/**
 * This API is called on receiving JMS message from SDF. It does the following
 * logic - Converts the message to ROSS format - Based on the message name, call
 * the appropriate web service
 * 
 * Modifications for 9.1:
 * 	- Line 208-211 - not using getLocalName() anymore because it gives a NullPointerException. Using getNodeName and trimming off the prefix prior to setting the message name.  
 *  
 * @revisions lightwell
 * @version 1.2
 */
public class NWCGOBReader implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGOBReader.class);

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
		logger.verbose("@@@@@ Entering NWCGOBReader::getOperationType");

		int operationType = -1;
		String isSync = ResourceUtil.get(msgName.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_IS_SYNC));
		String boundType = ResourceUtil.get(msgName.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_TYPE));
		String isNotification = ResourceUtil.get(msgName.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_IS_NOTIFICATION));
		if (isNotification != null && isNotification.equalsIgnoreCase(NWCGAAConstants.STR_TRUE)) {
			operationType = NWCGWebServicesConstant.OBTYPE_NOTIFICATION;
		} else {
			if (boundType != null && isSync != null) {
				if (boundType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_OB) && isSync.equalsIgnoreCase(NWCGAAConstants.STR_TRUE)) {
					// OB Synchronous
					operationType = NWCGWebServicesConstant.OBTYPE_SYNC;
				} else if (boundType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_OB) && isSync.equalsIgnoreCase(NWCGAAConstants.STR_FALSE)) {
					// OB Asynchronous
					operationType = NWCGWebServicesConstant.OBTYPE_ASYNC;
				}
			}
		}

		logger.verbose("@@@@@ Exiting NWCGOBReader::getOperationType");
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
		logger.verbose("@@@@@ Entering NWCGOBReader::processMsgFromQueue");
		
		Document opDoc = inputDoc;
		try {
			if (inputDoc != null) {
				inputDoc = prepareInputDocument(env, inputDoc);
				int operationType = getOperationTypeFromInput(inputDoc);
				logger.verbose("@@@@@ operationType :: " + operationType);
				if (operationType == -1) {
					logger.verbose("@@@@@ In Empty IF...operationType is -1 ");
				} else {
					Object retnObj = (new NWCGBusinessMsgProcessor()).postOutboundMessageToROSS(env, operationType, inputDoc);
					if (retnObj != null && operationType == NWCGWebServicesConstant.OBTYPE_SYNC) {
						opDoc = (Document) retnObj;
						logger.verbose("@@@@@ Output Doc is not NULL... ");
					} else if (retnObj == null) {
						opDoc = null;
						logger.verbose("@@@@@ Setting Output Doc to NULL... ");
					}
				}
			} else {
				logger.verbose("@@@@@ In Empty ELSE...inputDoc is NULL... ");
			}
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception :: " + ex);
			ex.printStackTrace();
		}
		
		logger.verbose("@@@@@ Exiting NWCGOBReader::processMsgFromQueue");
		return opDoc;
	}
	
	/**
	 * Changing the calculation of operation type. If we are creating an
	 * issue for a new incident, then we need to make a register interest
	 * call to ROSS (sync). We will be calling register incident interest
	 * from incident details screens too (async). In order to distinguish
	 * between these calls (sync and async), we are setting the
	 * OperationType variable in the xsl for both create and update. If it
	 * is create, then we will treat this as a sync call.
	 * 
	 * @param inputDoc
	 * @return
	 */
	private int getOperationTypeFromInput(Document inputDoc) {
		logger.verbose("@@@@@ Entering NWCGOBReader::getOperationTypeFromInput");

		int operationType = -1;
		
		NodeList nodes = inputDoc.getChildNodes();
		if(nodes.getLength() > 0) {
			logger.verbose("@@@@@ nodes.getLength() :: " + nodes.getLength());
			Element rootElem = (Element) nodes.item(0);
		
			//Element inputDocElement = inputDoc.getDocumentElement();
			String operationTypeFromInput = rootElem.getAttribute(NWCGAAConstants.OPERATION_TYPE);
			logger.verbose("@@@@@ operationTypeFromInput :: " + operationTypeFromInput);
			if (!StringUtil.isEmpty(operationTypeFromInput)) {
				if (operationTypeFromInput.equalsIgnoreCase(NWCGAAConstants.OPERATION_SYNC)) {
					operationType = NWCGWebServicesConstant.OBTYPE_SYNC;
				} else if (operationTypeFromInput.equalsIgnoreCase(NWCGAAConstants.OPERATION_ASYNC)) {
					operationType = NWCGWebServicesConstant.OBTYPE_ASYNC;
				}
			}
			
			// If we didn't get the operation type from the input DOM doc, then call getOperationType with the message name to pull the correct operation type from NWCGAnAImpl.properties
			if (operationType == -1) {
				//operationType = getOperationType(rootElem.getLocalName());
				operationType = getOperationType(rootElem.getAttribute("messageName"));
			}
			if (operationType == -1) {
				// as a last attempt we will use whatever operationType we are getting in input XML this will be used for get operation results OB call
				try {
					operationType = Integer.parseInt(operationTypeFromInput);
				}
				// catch any exception thrown by application and return -1 to caller
				catch (Exception e) {
					logger.error("!!!!! OBReader can not determine the operation type, will not post any message to ROSS" + e.getMessage());
					operationType = -1;
				}
			}
		
		}

		logger.verbose("@@@@@ Exiting NWCGOBReader::getOperationTypeFromInput");
		return operationType;
	}

	/**
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws YFSException
	 */
	public Document prepareInputDocument(YFSEnvironment env, Document inputDoc) throws YFSException {
		logger.verbose("@@@@@ Entering NWCGOBReader::prepareInputDocument @@@@@");
		Element inputDocElement = inputDoc.getDocumentElement();
		//String msgName = inputDocElement.getLocalName();
		int index = inputDocElement.getNodeName().indexOf(":") + 1;
		String msgName = inputDocElement.getNodeName().substring(index);
		logger.verbose("@@@@@ msgName :: " + msgName + " :: index :: " + index);
		try {
			// Pull the namespace from the properties file <messageName>.namespace
			String namespace = NWCGProperties.getProperty(msgName.concat(".namespace"));
			if (StringUtil.isEmpty(namespace)) {
				logger.verbose(msgName.concat(".namespace") + " is not configured in NWCGAnAImpl.properties");
				if (msgName.contains("atalog")) {
					namespace = "http://nwcg.gov/services/ross/catalog/1.1";
				} else {
					namespace = "http://nwcg.gov/services/ross/resource_order/1.1";
				}
			}
			// Pull the user id from root/@NWCGUSERID
			String nwcgUserID = inputDocElement.getAttribute("NWCGUSERID");
			if (StringUtil.isEmpty(nwcgUserID)) {
				// If not found, we have to throw an exception; TODO: will need to update the message store so that the reprocess agent can pick it up
				YFSException yfs = new YFSException("NWCGUSERID attribute not set in root element!");
				throw yfs;
			}
			inputDocElement.setAttribute(NWCGAAConstants.MDTO_NAMESPACE, namespace);
			logger.verbose("@@@@@ namespace :: " + namespace);
			inputDocElement.setAttribute(NWCGAAConstants.MDTO_USERNAME, nwcgUserID);
			logger.verbose("@@@@@ nwcgUserID :: " + nwcgUserID);
			inputDocElement.setAttribute(NWCGAAConstants.MDTO_MSGNAME, msgName);
			logger.verbose("@@@@@ msgName :: " + msgName);
		} catch (YFSException yfe) {
			logger.error("!!!!! Caught YFSException : " + yfe);
			throw yfe;
		}
		logger.verbose("@@@@@ Exiting NWCGOBReader::prepareInputDocument @@@@@");
		return inputDoc;
	}
}