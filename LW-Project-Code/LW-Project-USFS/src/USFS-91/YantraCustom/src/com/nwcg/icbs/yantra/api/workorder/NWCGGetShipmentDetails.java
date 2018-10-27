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

import java.util.ArrayList;
import java.util.Properties;

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetShipmentDetails implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetShipmentDetails.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	public Document getCustomShipmentDetails(YFSEnvironment env,
			Document inputDoc) throws Exception {
		logger.verbose("NWCGGetShipmentDetails::getCustomShipmentDetails, Entered");
		if (logger.isVerboseEnabled()) {
			logger.verbose("NWCGGetShipmentDetails::getCustomShipmentDetails, Input Doc : "
					+ XMLUtil.getXMLString(inputDoc));
		}

		Document outDoc = CommonUtilities.invokeAPI(env, "getShipmentDetails",
				inputDoc);

		logger.verbose("NWCGGetShipmentDetails::getCustomShipmentDetails, Returning");

		return generateOutputDoc(env, outDoc);
	}

	private Document generateOutputDoc(YFSEnvironment env, Document inputDoc)
			throws Exception {
		Element rootElement = inputDoc.getDocumentElement();
		String cacheId = rootElement.getAttribute("ShipNode");
		String orderNo = ((Element) inputDoc.getElementsByTagName(
				"ShipmentLine").item(0)).getAttribute("OrderNo");
		Element extnElement = (Element) inputDoc.getElementsByTagName("Extn")
				.item(0);
		String incidentNo = extnElement.getAttribute("ExtnIncidentNum");
		String incidentYear = extnElement.getAttribute("ExtnYear");

		Document ipDoc = XMLUtil.getDocument();
		Element ipDocRootElement = ipDoc
				.createElement("NWCGIssueTrackableList");
		ipDocRootElement.setAttribute("IssueNo", orderNo);
		ipDocRootElement.setAttribute("IncidentNo", incidentNo);
		ipDocRootElement.setAttribute("IncidentYear", incidentYear);
		ipDocRootElement.setAttribute("CacheId", cacheId);
		ipDoc.appendChild(ipDocRootElement);

		Document opDoc = CommonUtilities.invokeService(env,
				"NWCGGetIssueTrackableListService", ipDoc);

		NodeList nList = opDoc.getElementsByTagName("NWCGIssueTrackableList");

		Document outDoc = XMLUtil.newDocument();
		Element shipDetailsRootElement = outDoc
				.createElement("ShipmentDetails");
		outDoc.appendChild(shipDetailsRootElement);

		for (int j = 0; j < nList.getLength(); j++) {
			Element listElement = (Element) nList.item(j);

			Element shipLineElement = outDoc.createElement("ShipmentLine");
			shipLineElement.setAttribute("SerialNo",
					listElement.getAttribute("SerialNo"));
			shipLineElement.setAttribute("ItemID",
					listElement.getAttribute("ItemId"));
			shipLineElement.setAttribute("OrderNo",
					listElement.getAttribute("IssueNo"));

			shipDetailsRootElement.appendChild(shipLineElement);
		}
		return outDoc;
	}
}
