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

package com.nwcg.icbs.yantra.reports.loftware;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGLocationLabel implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGLocationLabel.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	/*
	 * THIS IS FOR NWCG_LOCATION_LABEL <Print IgnoreOrdering="Y"> <Location
	 * LocationKey="" LoationID="" LabelSize="" /> <PrinterPreference
	 * OrganizationCode="" PrinterId="" UserId=""/> <LabelPreference
	 * BuyerOrganizationCode="" EnterpriseCode="" NoOfCopies="" Node="" SCAC=""
	 * Scac="" SellerOrganizationCode=""/> </Print>
	 */

	public Document triggerLocationLabel(YFSEnvironment env, Document inputDoc)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger.verbose("NWCGLocationLabel::triggerLocationLabel, Input XML : "
					+ XMLUtil.getXMLString(inputDoc));
		}

		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Location")
				.item(0);
		String LocationKey = rootElem1.getAttribute("LocationKey");
		String LocationId = rootElem1.getAttribute("LocationId");
		String LabelSize = rootElem1.getAttribute("LabelSize");

		// SET THE LABEL SIZE DOCUMENT
		String documentId = "";
		{
			if (LabelSize.equals("SMALL")) {
				documentId = "NWCG_SMALL_LOCATION_LABEL";
			} else {
				documentId = "NWCG_LARGE_LOCATION_LABEL";
			}

			Element rootElem2 = (Element) inputDoc.getElementsByTagName(
					"PrinterPreference").item(0);
			String OrganizationCode = rootElem2
					.getAttribute("OrganizationCode");
			String UserId = rootElem2.getAttribute("UserId");
			String printerId = rootElem2.getAttribute("PrinterId");

			Element rootElem3 = (Element) inputDoc.getElementsByTagName(
					"LabelPreference").item(0);
			String noOfCopies = rootElem3.getAttribute("NoOfCopies");
			String nodeKey = rootElem3.getAttribute("Node");
			String enterpriseCode = rootElem3.getAttribute("EnterpriseCode");

			int noCopies = 1;
			if (Integer.parseInt(noOfCopies) > 1)
				noCopies = Integer.parseInt(noOfCopies);

			NWCGReportsUtil reportsUtil = new NWCGReportsUtil();

			Document finalDoc = reportsUtil.generatePrintHeader(documentId,
					"xml:/Location", OrganizationCode, printerId,
					enterpriseCode);

			Element finalDocElm = finalDoc.getDocumentElement();
			Element printDocElm = (Element) finalDocElm.getFirstChild();
			Element inputData = finalDoc.createElement("InputData");
			printDocElm.appendChild(inputData);
			Element tmpElm = finalDoc.createElement("Location");

			tmpElm.setAttribute("LocationId", LocationId);
			inputData.appendChild(tmpElm);

			for (int i = 0; i < noCopies; i++) {
				String pageNo = new Integer(i + 1).toString();
				logger.verbose("NWCGLocationLabel::generateAndtriggerLocationLabel, Page No : "
						+ pageNo);
				// Call the printdocumentset API everytime here
				if (logger.isVerboseEnabled()) {
					logger.verbose("NWCGLocationLabel::generateAndtriggerLocationLabel, XML output to Loftware : "
							+ XMLUtil.getXMLString(finalDoc));
				}

				logger.verbose("NWCGLocationLabel::generateAndtriggerLocationLabel, Calling printDocumentSet API");
				CommonUtilities.invokeAPI(env,
						NWCGConstants.API_PRINT_DOCUMENT_SET, finalDoc);
			}

			return inputDoc;

		}
	}
}