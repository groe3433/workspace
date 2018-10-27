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

package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.ue.YCPBeforeTaskCompletionUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

public class NWCGBeforeTaskCompletionUE implements YCPBeforeTaskCompletionUE {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGBeforeTaskCompletionUE.class);
	
	public void beforeRegisterTaskCompletion(YFSEnvironment env, Document inDoc)
			throws YFSUserExitException {
		Element taskElement = inDoc.getDocumentElement();
		String taskKey = taskElement.getAttribute("TaskKey");

		// sourceLocationId is present in Complete Tasks flow while absent in
		// View Tasks flow
		String sourceLocationId = taskElement.getAttribute("SourceLocationId");

		try {
			Document opTemplate = XMLUtil
					.getDocument("<Task SourceLocationId=\"\" TaskId=\"\" Node=\"\"><TaskReferences ShipmentKey=\"\" ShipmentNo=\"\" /></Task>");
			Document ipDoc = XMLUtil.getDocument("<Task TaskKey=\"" + taskKey
					+ "\" />");
			Document outDoc = CommonUtilities.invokeAPI(env, opTemplate,
					"getTaskDetails", ipDoc);

			Element opTaskElement = outDoc.getDocumentElement();
			String sourceLocation = opTaskElement
					.getAttribute("SourceLocationId");
			String node = opTaskElement.getAttribute("Node");
			String taskId = opTaskElement.getAttribute("TaskId");

			Element taskReferencesElement = (Element) opTaskElement
					.getElementsByTagName("TaskReferences").item(0);
			String shipmentKey = taskReferencesElement
					.getAttribute("ShipmentKey");
			String shipmentNo = taskReferencesElement
					.getAttribute("ShipmentNo");

			String isReturnReceipt = "N";
			if (shipmentNo != null && shipmentNo.trim().length() > 0) {
				// remove leading "S" from shipmentNo to obtain receiptNo
				String receiptNo = shipmentNo.substring(1);
				Document opReceiptTemplate = XMLUtil
						.getDocument("<Receipt ReceiptHeaderKey=\"\"><Extn ExtnIsReturnReceipt=\"\"/></Receipt>");
				Document ipReceiptDoc = XMLUtil
						.getDocument("<Receipt ReceivingNode=\"" + node
								+ "\" ReceiptNo=\"" + receiptNo
								+ "\" ShipmentKey=\"" + shipmentKey + "\" />");

				try {
					Document opReceiptDoc = CommonUtilities.invokeAPI(env,
							opReceiptTemplate, "getReceiptDetails",
							ipReceiptDoc);
					Element opReceiptElement = opReceiptDoc
							.getDocumentElement();

					if (opReceiptElement != null) {
						// the existence of Receipt record ensures that the
						// shipmentNo belongs to a Return.
						NodeList extnElementNL = opReceiptElement
								.getElementsByTagName("Extn");
						if (extnElementNL != null) {
							Element extnElement = (Element) extnElementNL
									.item(0);
							isReturnReceipt = extnElement
									.getAttribute("ExtnIsReturnReceipt");
						}
					}
				} catch (YFSException yfse) {
				}
			}

			if ((sourceLocationId.equals("") && sourceLocation.equals("RFI-1") && isReturnReceipt
					.equals("N"))
					|| (sourceLocationId.equals("RFI-1") && isReturnReceipt
							.equals("N"))) {
				throw new YFSUserExitException(
						"Cannot pick inventory from RFI-1. Task ID = " + taskId);
			}
		} catch (Exception e) {
			throw new YFSUserExitException(e.toString());
		}
	}
}