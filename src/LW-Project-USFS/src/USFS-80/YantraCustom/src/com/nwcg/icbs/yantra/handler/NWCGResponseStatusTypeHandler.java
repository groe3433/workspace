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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGMessageStore;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGMessageFactory;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;

//When ROSS async response to an ICBS request, this method is called
public class NWCGResponseStatusTypeHandler extends NWCGMessageHandler {

	public NWCGResponseStatusTypeHandler() {
		System.out.println("@@@@@ In NWCGResponseStatusTypeHandler::NWCGResponseStatusTypeHandler @@@@@");
	}

	public NWCGSOAPMsg process(NWCGSOAPMsg msg) {
		System.out.println("@@@@@ Entering NWCGResponseStatusTypeHandler::process @@@@@");

		//Process the response

		String xml = msg.getXml();

		Document doc = NWCGAAUtil.buildXMLDocument(xml);

		NodeList nL = doc.getElementsByTagName("DistributionID");

		Node n_distID = nL.item(0);
		String distID = n_distID.getFirstChild().getNodeValue();

		NWCGSOAPMsg msgResp = NWCGMessageFactory.newMessage("ACK");

		try {

			//Code to process the response

			NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();

			String message_key = NWCGAAUtil.lookupMessageKeyFromDistId("OB",
					distID);

			NWCGAAUtil.logInfo("NWCGResponseStatusTypeHandler", "message_key:"
					+ message_key);

			//update the message status to processed

			msgStore.updateMessage(distID, "OB", xml,
					NWCGAAConstants.MESSAGE_TYPE_LATEST,
					NWCGAAConstants.MESSAGE_STATUS_PROCESSED,
					NWCGAAConstants.SYSTEM_NAME, message_key, true);

			msgResp.buildSOAPMsg();

		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("@@@@@ Exiting NWCGResponseStatusTypeHandler::process @@@@@");
		return msgResp;
	}
}