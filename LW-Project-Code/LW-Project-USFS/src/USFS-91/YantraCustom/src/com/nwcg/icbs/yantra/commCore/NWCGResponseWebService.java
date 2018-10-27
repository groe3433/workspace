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

import org.w3c.dom.Document;
//import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGOBAuthMessageHandler;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

public class NWCGResponseWebService {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGResponseWebService.class);

	/**
	 * 1. store message using Sterling DB. output of this will entire message
	 * with key 2. create a JMS message msg, and embeed key in message 3. place
	 * message on key 4. respond with ack to ross that contains the message
	 * response key
	 */

	// Need to review exactly what the SOAP server will call on this method
	public Document recieveMsg(YFSEnvironment env, Document doc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGResponseWebService::recieveMsg");
		NWCGSOAPMsg msg = new NWCGSOAPMsg();
		msg.setXml(NWCGAAUtil.serialize(doc));
		// only reason to use two parameter in a factory is to invoke a particular MessageHandler, so just do it outside. 
		NWCGOBAuthMessageHandler handler = new NWCGOBAuthMessageHandler();
		NWCGSOAPMsg resp_msg = handler.process(msg);
		String resp_msg_str = "";
		try {
			resp_msg_str = NWCGAAUtil.buildSOAP(resp_msg);
		} catch (Exception e) {
			logger.error("!!!!!  " + e.toString());
		}
		// convert this string to a document
		logger.verbose("@@@@@ Exiting NWCGResponseWebService::recieveMsg");
		return NWCGAAUtil.buildXMLDocument(resp_msg_str);
	}
}