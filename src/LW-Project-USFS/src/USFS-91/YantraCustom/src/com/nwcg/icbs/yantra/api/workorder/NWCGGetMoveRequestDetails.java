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

package com.nwcg.icbs.yantra.api.workorder;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetMoveRequestDetails implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetMoveRequestDetails.class);

	public Document getCustomMoveRequestDetails(YFSEnvironment env,
			Document inDoc) throws Exception {
		return modifiedMoveRequest(env, inDoc);
	}

	private Document modifiedMoveRequest(YFSEnvironment env, Document inDoc)
			throws Exception {
		Document modifiedMoveRequestDoc = XMLUtil.getDocument();
		Element moveRequestElement = (Element) inDoc.getElementsByTagName(
				"MoveRequest").item(0);
		Element newMoveRequestElement = modifiedMoveRequestDoc
				.createElement("MoveRequest");
		XMLUtil.copyElement(modifiedMoveRequestDoc, moveRequestElement,
				newMoveRequestElement);
		modifiedMoveRequestDoc.appendChild(newMoveRequestElement);
		newMoveRequestElement.setAttribute("DocType",
				getDocumentType(env, inDoc));

		return modifiedMoveRequestDoc;
	}

	private String getDocumentType(YFSEnvironment env, Document inDoc)
			throws Exception {
		String documentType = null;
		Element moveRequestElement = (Element) inDoc.getElementsByTagName(
				"MoveRequest").item(0);
		// String moveRequestKey =
		// moveRequestElement.getAttribute("MoveRequestKey");
		String workOrderKey = moveRequestElement.getAttribute("WorkOrderKey");
		String shipmentKey = moveRequestElement.getAttribute("ShipmentKey");

		if (workOrderKey != null && !workOrderKey.trim().equals("")) {
			Document outDoc = CommonUtilities.invokeAPI(
					env,
					NWCGConstants.API_GET_WORK_ORDER_DTLS,
					XMLUtil.getDocument("<WorkOrder WorkOrderKey=\""
							+ workOrderKey + "\" />"));
			Element workOrderElem = (Element) outDoc.getElementsByTagName(
					"WorkOrder").item(0);
			if (workOrderElem != null) {
				documentType = workOrderElem.getAttribute("DocumentType");
			}
		} else if (shipmentKey != null && !shipmentKey.trim().equals("")) {
			Document outDoc = CommonUtilities.invokeAPI(
					env,
					NWCGConstants.API_GET_SHIPMENT_DETAILS,
					XMLUtil.getDocument("<Shipment ShipmentKey=\""
							+ shipmentKey + "\" />"));
			Element shipmentElem = (Element) outDoc.getElementsByTagName(
					"Shipment").item(0);
			if (shipmentElem != null) {
				documentType = shipmentElem.getAttribute("DocumentType");
			}
		}

		return documentType;
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}