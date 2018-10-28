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

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.api.trackableinventory.NWCGUpdateSysNoAPI;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetBatchDetails implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetBatchDetails.class);

	public Document getCustomBatchDetails(YFSEnvironment env, Document inDoc)
			throws Exception {
		Document outDoc = generateOutputDoc(env, inDoc);
		return outDoc;
	}

	public Document generateOutputDoc(YFSEnvironment env, Document inDoc)
			throws Exception {
		String workOrderNo = "";
		Document outDoc = CommonUtilities.invokeAPI(env, "getBatchDetails",
				inDoc);
		Document workOrderDoc = getWorkOrderNo(env, outDoc);

		if (workOrderDoc != null) {
			Element workOrderElem = (Element) workOrderDoc.getDocumentElement()
					.getElementsByTagName("WorkOrder").item(0);
			if (workOrderElem != null) {
				workOrderNo = workOrderElem.getAttribute("WorkOrderNo");
			}
		}

		Document finalOutDoc = XMLUtil.getDocument();
		Element batchElement = (Element) outDoc.getElementsByTagName("Batch")
				.item(0);
		Element newBatchElement = finalOutDoc.createElement("Batch");
		XMLUtil.copyElement(finalOutDoc, batchElement, newBatchElement);
		finalOutDoc.appendChild(newBatchElement);
		newBatchElement.setAttribute("WorkOrderNo", workOrderNo);

		return finalOutDoc;
	}

	private Document getWorkOrderNo(YFSEnvironment env, Document inDoc)
			throws Exception {
		Element batchElement = (Element) inDoc.getElementsByTagName("Batch")
				.item(0);
		String moveRequestNo = batchElement.getAttribute("MoveRequestNo");
		String node = batchElement.getAttribute("Node");

		Document inDocForAPI = XMLUtil.newDocument();
		Element element = inDocForAPI.createElement("MoveRequest");
		element.setAttribute("MoveRequestNo", moveRequestNo);
		element.setAttribute("Node", node);
		inDocForAPI.appendChild(element);

		Document invOutDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_GET_MOVE_REQUEST_DETAILS, inDocForAPI);

		return invOutDoc;
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}