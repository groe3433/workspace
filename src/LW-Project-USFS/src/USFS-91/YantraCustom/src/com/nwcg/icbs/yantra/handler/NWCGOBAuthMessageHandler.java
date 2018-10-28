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

package com.nwcg.icbs.yantra.handler;

import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;
import com.nwcg.icbs.yantra.commCore.*;
import com.nwcg.icbs.yantra.soap.*;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

import org.w3c.dom.*;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGOBAuthMessageHandler extends com.nwcg.icbs.yantra.handler.NWCGMessageHandler {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBAuthMessageHandler.class);
	
	public NWCGSOAPMsg process(NWCGSOAPMsg msg) {
		
		logger.verbose("Entering com.nwcg.icbs.yantra.handler.NWCGOBAuthMessageHandler:process");

		NWCGSOAPMsg resp = null;
		String system_key = null;
		try {
			String responseStatus = "OK";

			// extract the key
			String xml = msg.getXml();
			Document doc = NWCGAAUtil.buildXMLDocument(xml);
			String username = NWCGAAUtil.lookupNodeValue(doc, "Username");
			String key_pw = NWCGAAUtil.lookupNodeValue(doc, "OnetimePassword");
			String distID = NWCGAAUtil.lookupNodeValue(doc, "distributionID");
			try {
				if (!NWCGTimeKeyManager.verifyKey(key_pw)) {
					responseStatus = "FAIL";
				}
				// verify the key and get system_key
				system_key = NWCGTimeKeyManager.extractSystemKey(key_pw);
			} catch (Exception e) {
				responseStatus = "FAIL";
			}

			if (responseStatus.equals("FAIL")) {
				NWCGSOAPMsg fail_msg = NWCGMessageFactory.newMessage("");
				// load the fault template
				String txt = NWCGAAUtil.readText(NWCGProperties.getProperty("FAULT_TEMPLATE"));
				NWCGCodeTemplate ct = new NWCGCodeTemplate(txt);
				ct.setSlot("faultcode", "SOAP-ENV:Server");
				ct.setSlot("faultstring", "Invalid Password");
				ct.setSlot("faultactor", "?");
				ct.setSlot("detail", "");
				fail_msg.setXml(ct.generateCode());
				resp = fail_msg;
			} else {
				// builds the auth body
				NWCGOBAuthRespMsg authRespMsg = (NWCGOBAuthRespMsg) NWCGMessageFactory.newMessage(NWCGAAConstants.AUTH_USER_RESP_OB_MSG_NAME);
				authRespMsg.setUserFullname("?");
				authRespMsg.setUsername(username);
				authRespMsg.setAuthMessage(responseStatus);
				// authRespMsg.setUsername("?");
				authRespMsg.setResponseStatus(responseStatus);
				authRespMsg.setDistId(distID);
				authRespMsg.buildSOAPMsg();
				resp = authRespMsg;
			}

			// System should update the message row based on system_key_id
			// having
			// type as “LATEST” in NWCG_OUTBOUND_MESSAGE table with status
			// “Authorized”
			// and Last Inbound flag as false.
			if (system_key != null) {
				NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
				msgStore.updateMessage("", "OB", xml, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_OB_MESSAGE_SENT, NWCGAAConstants.SYSTEM_NAME, system_key, false);
			}
		} catch (Exception e) {
		}
		logger.verbose("Exiting com.nwcg.icbs.yantra.handler.NWCGOBAuthMessageHandler:process");
		return resp;
	}

	private boolean authenticateUser(Object o) {
		return true;
	}

	public boolean authorize(NWCGIBAuthReqMsg authReqMsg) {
		// assume user is authenticated
		return true;
	}
}