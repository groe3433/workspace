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

package com.nwcg.icbs.yantra.webservice.ib.reader;

import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.ib.read.handler.NWCGMessageHandlerInterface;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This API is called on receiving JMS message from SDF. It does the following logic
 * - Converts the message to ROSS format
 * - Based on the message name, call the appropriate web service
 * @author sgunda
 */
public class NWCGIBReader implements YIFCustomApi {

	private Properties myProperties = null;

	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}

	/**
	 * This method is used to retrieve the correct message name. In a NOTIFICATION
	 * scenario, we will recieve the message name as DeliverNotificationReq, but the
	 * actual message name is an element that ends with Notification with in that
	 * message body
	 * If the message name is not DeliverNotificationRequest, then return the
	 * current message name. Sample input xml 
	 * <MetadataTransferObject distributionID="0123450" messageName="DeliverNotificationReq" namespace="gov.nwcg.services.ross.resource_order._1" username="User">
	 <ron:DeliverNotificationReq encoding="UTF-8" standalone="yes" version="1.0" xmlns:cdf="http://www.cdf.ca.gov/CAD" xmlns:ron="http://nwcg.gov/services/ross/resource_order_notification/1.1" xmlns:rscn="http://nwcg.gov/services/ross/resource_notification/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	 <ron:NotifiedSystem>
	 <SystemType>ICBS</SystemType> 
	 <SystemID>null</SystemID> 
	 </ron:NotifiedSystem>
	 <ron:UpdateIncidentKeyNotification>
	 <ron:NotificationBase>
	 <MessageOriginator>
	 <SystemOfOrigin>
	 <SystemType>ROSS</SystemType> 
	 </SystemOfOrigin>
	 <DispatchUnitID>
	 <UnitIDPrefix>LM</UnitIDPrefix> 
	 <UnitIDSuffix>9A1</UnitIDSuffix> 
	 </DispatchUnitID>
	 </MessageOriginator>
	 <TimeOfNotification>2007-09-14T22:56:13+00:00</TimeOfNotification> 
	 <Action>Update</Action> 
	 </ron:NotificationBase>
	 <ron:OldIncidentKey>
	 <IncidentID>
	 <EntityType>Incident</EntityType> 
	 <EntityID>151</EntityID> 
	 <ApplicationSystem>
	 <SystemType>ROSS</SystemType> 
	 </ApplicationSystem>
	 </IncidentID>
	 <NaturalIncidentKey>
	 <HostID>
	 <UnitIDPrefix>LM</UnitIDPrefix> 
	 <UnitIDSuffix>9A1</UnitIDSuffix> 
	 </HostID>
	 <SequenceNumber>500041</SequenceNumber> 
	 <YearCreated>2007</YearCreated> 
	 </NaturalIncidentKey>
	 </ron:OldIncidentKey>
	 <ron:NewIncidentKey>
	 <IncidentID>
	 <EntityType>Incident</EntityType> 
	 <EntityID>151</EntityID> 
	 <ApplicationSystem>
	 <SystemType>ROSS</SystemType> 
	 </ApplicationSystem>
	 </IncidentID>
	 <NaturalIncidentKey>
	 <HostID>
	 <UnitIDPrefix>LM</UnitIDPrefix> 
	 <UnitIDSuffix>9A1</UnitIDSuffix> 
	 </HostID>
	 <SequenceNumber>500042</SequenceNumber> 
	 <YearCreated>2007</YearCreated> 
	 </NaturalIncidentKey>
	 </ron:NewIncidentKey>
	 </ron:UpdateIncidentKeyNotification>
	 </ron:DeliverNotificationReq>
	 </MetadataTransferObject>	 
	 * @param msgName
	 * @return
	 */

	/**
	 * This method will get a message from the queue. Based on the message name, it will
	 * call the appropriate interface/class. On failure, it will raise an alert
	 * in NWCG_IB_EXCEPTIONS queue
	 * @param env
	 * @param inputDoc
	 * @return
	 */
	public Document processMsgFromQueue(YFSEnvironment env, Document inputDoc) {
		System.out.println("@@@@@ Entering NWCGIBNotification::processMsgFromQueue @@@@@");
		
		try {
			NWCGLoggerUtil.Log.finest("NWCGIBReader::processMsgFromQueue, "
					+ "Input document : "
					+ XMLUtil.extractStringFromDocument(inputDoc));
			if (inputDoc != null) {
				String msgName = inputDoc.getDocumentElement().getAttribute(
						NWCGAAConstants.MESSAGE_NAME_ELEM_NAME);
				NWCGLoggerUtil.Log
						.finest("NWCGIBReader::processMsgFromQueue, Message Name : "
								+ msgName);
				String handlerClassName = NWCGAAUtil.getHandler(msgName);
				NWCGLoggerUtil.Log
						.finest("NWCGIBReader::processMsgFromQueue, Handler Class Name : "
								+ handlerClassName);
				Class handlerClassObj = Class.forName(handlerClassName);
				Object handlerObj = handlerClassObj.newInstance();
				NWCGMessageHandlerInterface msgHndlrInterface = (NWCGMessageHandlerInterface) handlerObj;
				NWCGLoggerUtil.Log
						.finest("NWCGIBReader::processMsgFromQueue, Invoking class...");
				msgHndlrInterface.process(env, inputDoc);
			} else {
				NWCGLoggerUtil.Log
						.warning("NWCGIBReader::processMsgFromQueue, input document is null");
			}
		} catch (ClassNotFoundException cnfe) {
			NWCGLoggerUtil.Log
					.warning("NWCGIBReader::processMsgFromQueue, Class Not Found Exception : "
							+ cnfe.getMessage());
			cnfe.printStackTrace();
		} catch (Exception e) {
			NWCGLoggerUtil.Log
					.warning("NWCGIBReader::processMsgFromQueue, Exception : "
							+ e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGIBNotification::processMsgFromQueue @@@@@");
		return inputDoc;
	}
}