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

package com.nwcg.icbs.yantra.webservice.ob.notification;

import javax.xml.rpc.ServiceException;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.webservice.ob.NWCGOBSyncAsyncProcessor;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGOBNotification extends NWCGOBSyncAsyncProcessor {

	public NWCGOBNotification() {
		super();
	}

	/*
	 *1.	Notification  is received by A&A framework from Sterling App via JMS queue. 
	 2.	A&A creates new records in the Message Store with initial status VOID and distribution id set to random UUID
	 3.	A&A builds Soap Envelop including Soap Headers  and send Notification  in the Soap Body and set status to PROCESSED
	 4.	ROSS system is  performing synchronous call to ICSBR to authenticate the Notification  (not part of A&A)
	 5.	If A&A receives  Exception back indicating Communication failure or any other exceptions occurred, A&A sets message status to SENT_FAILED 
	 (these messages will be re-sent to ROSS by retry described in use-scenario 4)
	 */
	public void process(YFSEnvironment env, Document msg) throws Exception {

		NWCGLoggerUtil.Log.info("NWCGOBNotification.process");

		setupMessageMap(env, msg);
		setupMessageStore(env);

		// now need to invoke web service and post this message.
		try {

			//Create Request XML, setup headers
			Document msgDoc = createMessageRequest();

			//Send msg to ROSS
			Object returnedMessage = sendMessageRequest(msgDoc);

			setMsgStatusProcessed(null);
		} catch (ServiceException e) {
			setMsgStatusSentFailed(null);
			NWCGLoggerUtil.Log
					.warning("Got Exception in NWCGOBNotification.process()");
		}
	}
}