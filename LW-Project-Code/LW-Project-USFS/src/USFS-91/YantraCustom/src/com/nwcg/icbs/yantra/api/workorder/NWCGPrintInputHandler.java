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

import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGPrintInputHandler implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPrintInputHandler.class);
	
	public Document customizeInputForPrinting(YFSEnvironment env, Document inDoc)
			throws Exception {
		return customizedInput(env, inDoc);
	}

	private Document customizedInput(YFSEnvironment env, Document inDoc)
			throws Exception {
		Document modifiedDoc = XMLUtil.getDocument();
		Element batchElement = (Element) inDoc.getElementsByTagName("Batch")
				.item(0);
		Element newBatchElement = modifiedDoc.createElement("Batch");
		XMLUtil.copyElement(modifiedDoc, batchElement, newBatchElement);
		modifiedDoc.appendChild(newBatchElement);
		newBatchElement.setAttribute("DocType", getDocumentType(env, inDoc));

		return modifiedDoc;
	}

	private String getDocumentType(YFSEnvironment env, Document inDoc)
			throws Exception {
		String documentType = null;
		String shipmentKey = null;
		String workOrderKey = null;

		Element batchElement = (Element) inDoc.getElementsByTagName("Batch")
				.item(0);
		String node = batchElement.getAttribute("Node");
		String moveRequestNo = getMoveRequestNo(env, inDoc);

		Document moveRequestDoc = getMoveRequestDetails(env, moveRequestNo,
				node);
		Element moveReqElement = (Element) moveRequestDoc.getDocumentElement();
		shipmentKey = moveReqElement.getAttribute("ShipmentKey");
		workOrderKey = moveReqElement.getAttribute("WorkOrderKey");

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

	private String getMoveRequestNo(YFSEnvironment env, Document inDoc)
			throws Exception {
		String moveRequestNo = null;
		Element batchElement = (Element) inDoc.getElementsByTagName("Batch")
				.item(0);
		String batchNo = batchElement.getAttribute("BatchNo");

		Document opTemplate = XMLUtil
				.getDocument("<Batch MoveRequestNo=\"\" />");
		Document ipDoc = XMLUtil.getDocument("<Batch BatchNo=\"" + batchNo
				+ "\" />");

		if (batchNo != null && !batchNo.trim().equals("")) {
			Document outDoc = CommonUtilities.invokeAPI(env, opTemplate,
					"getBatchDetails", ipDoc);
			Element rootElement = (Element) outDoc.getDocumentElement();
			moveRequestNo = rootElement.getAttribute("MoveRequestNo");
		}

		return moveRequestNo;
	}

	private Document getMoveRequestDetails(YFSEnvironment env,
			String moveRequestNo, String node) throws Exception {
		Document opTemplate = XMLUtil
				.getDocument("<MoveRequest ShipmentKey=\"\" WorkOrderKey=\"\" />");
		Document ipDoc = XMLUtil.getDocument("<MoveRequest MoveRequestNo=\""
				+ moveRequestNo + "\" Node=\"" + node + "\"/>");
		return CommonUtilities.invokeAPI(env, opTemplate,
				"getMoveRequestDetails", ipDoc);
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}