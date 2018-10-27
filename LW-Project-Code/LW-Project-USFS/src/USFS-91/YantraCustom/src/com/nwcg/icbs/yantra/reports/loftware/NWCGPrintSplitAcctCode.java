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

public class NWCGPrintSplitAcctCode implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPrintSplitAcctCode.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}

	public Document printSplitAcctCode(YFSEnvironment env, Document inputDoc)
			throws Exception {
		int scnt = 0;
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGPrintSplitAcctCode::printSplitAcctCode, InputDoc: "
							+ XMLUtil.getXMLString(inputDoc));
		}

		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String strDate = reportsUtil.dateToString(new java.util.Date(),
				"MM-dd-yyyy");

		String documentId = "NWCG_SPLIT_ACCT_CODE";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Shipment")
				.item(0);
		String shipmentKey = rootElem1.getAttribute("ShipmentKey");
		String orderId = rootElem1.getAttribute("OrderNo");

		Element rootElem2 = (Element) inputDoc.getElementsByTagName(
				"PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		String shipNode = rootElem2.getAttribute("OrganizationCode");

		Element rootElem3 = (Element) inputDoc.getElementsByTagName(
				"LabelPreference").item(0);
		String enterpriseCode = rootElem3
				.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);

		Element rootElem4 = (Element) inputDoc.getElementsByTagName("Extn")
				.item(0);
		String extnAcctCode1 = rootElem4.getAttribute("ExtnAcctCode1"); // first
																		// Acct
																		// Code
		String ExtnOverrideCode1 = rootElem4.getAttribute("ExtnOverrideCode1");
		String ExtnSplitAmount1 = rootElem4.getAttribute("ExtnSplitAmount1");

		String extnAcctCode2 = rootElem4.getAttribute("ExtnAcctCode2"); // second
																		// Acct
																		// Code
		String ExtnOverrideCode2 = rootElem4.getAttribute("ExtnOverrideCode2");
		String ExtnSplitAmount2 = rootElem4.getAttribute("ExtnSplitAmount2");

		String extnAcctCode3 = rootElem4.getAttribute("ExtnAcctCode3"); // third
																		// Acct
																		// Code
		String ExtnOverrideCode3 = rootElem4.getAttribute("ExtnOverrideCode3");
		String ExtnSplitAmount3 = rootElem4.getAttribute("ExtnSplitAmount3");

		String extnAcctCode4 = rootElem4.getAttribute("ExtnAcctCode4"); // fourth
																		// Acct
																		// Code
		String ExtnOverrideCode4 = rootElem4.getAttribute("ExtnOverrideCode4");
		String ExtnSplitAmount4 = rootElem4.getAttribute("ExtnSplitAmount4");

		String extnAcctCode5 = rootElem4.getAttribute("ExtnAcctCode5"); // fifth
																		// Acct
																		// Code
		String ExtnOverrideCode5 = rootElem4.getAttribute("ExtnOverrideCode5");
		String ExtnSplitAmount5 = rootElem4.getAttribute("ExtnSplitAmount5");

		// generate XML doc
		Document SplitAcctCodesDoc = XMLUtil.getDocument();
		Element ShipmentTagElm = SplitAcctCodesDoc.createElement("Shipment");
		ShipmentTagElm.setAttribute("ShipmentKey", shipmentKey);
		ShipmentTagElm.setAttribute("OrderNo", orderId);
		ShipmentTagElm.setAttribute("CurrentDate", strDate);
		SplitAcctCodesDoc.appendChild(ShipmentTagElm);

		Element SplitAcctCodesElm = SplitAcctCodesDoc
				.createElement("SplitAcctCodes");
		ShipmentTagElm.appendChild(SplitAcctCodesElm);

		// if split account code is not blank/null, keep adding
		if (extnAcctCode1 != null && !extnAcctCode1.equals("")) {
			Element SplitAcctCodeElm1 = SplitAcctCodesDoc
					.createElement("SplitAcctCode");
			SplitAcctCodeElm1.setAttribute("AcctCode", extnAcctCode1);
			SplitAcctCodeElm1.setAttribute("OverrideCode", ExtnOverrideCode1);
			SplitAcctCodeElm1.setAttribute("Amount", ExtnSplitAmount1);
			SplitAcctCodesElm.appendChild(SplitAcctCodeElm1);
			scnt++;
		}

		if (extnAcctCode2 != null && !extnAcctCode2.equals("")) {
			Element SplitAcctCodeElm2 = SplitAcctCodesDoc
					.createElement("SplitAcctCode");
			SplitAcctCodeElm2.setAttribute("AcctCode", extnAcctCode2);
			SplitAcctCodeElm2.setAttribute("OverrideCode", ExtnOverrideCode2);
			SplitAcctCodeElm2.setAttribute("Amount", ExtnSplitAmount2);
			SplitAcctCodesElm.appendChild(SplitAcctCodeElm2);
			scnt++;
		}

		if (extnAcctCode3 != null && !extnAcctCode3.equals("")) {
			Element SplitAcctCodeElm3 = SplitAcctCodesDoc
					.createElement("SplitAcctCode");
			SplitAcctCodeElm3.setAttribute("AcctCode", extnAcctCode3);
			SplitAcctCodeElm3.setAttribute("OverrideCode", ExtnOverrideCode3);
			SplitAcctCodeElm3.setAttribute("Amount", ExtnSplitAmount3);
			SplitAcctCodesElm.appendChild(SplitAcctCodeElm3);
			scnt++;
		}

		if (extnAcctCode4 != null && !extnAcctCode4.equals("")) {
			Element SplitAcctCodeElm4 = SplitAcctCodesDoc
					.createElement("SplitAcctCode");
			SplitAcctCodeElm4.setAttribute("AcctCode", extnAcctCode4);
			SplitAcctCodeElm4.setAttribute("OverrideCode", ExtnOverrideCode4);
			SplitAcctCodeElm4.setAttribute("Amount", ExtnSplitAmount4);
			SplitAcctCodesElm.appendChild(SplitAcctCodeElm4);
			scnt++;
		}

		if (extnAcctCode5 != null && !extnAcctCode5.equals("")) {
			Element SplitAcctCodeElm5 = SplitAcctCodesDoc
					.createElement("SplitAcctCode");
			SplitAcctCodeElm5.setAttribute("AcctCode", extnAcctCode5);
			SplitAcctCodeElm5.setAttribute("OverrideCode", ExtnOverrideCode5);
			SplitAcctCodeElm5.setAttribute("Amount", ExtnSplitAmount5);
			SplitAcctCodesElm.appendChild(SplitAcctCodeElm5);
			scnt++;
		}

		Document printInputDoc = reportsUtil.generatePrintHeader(documentId,
				"xml:/Shipment", shipNode, printerId, enterpriseCode);
		Element printInputDocRootNode = printInputDoc.getDocumentElement();
		Element printDocElm = (Element) printInputDocRootNode.getFirstChild();

		Element inputDataElm = printInputDoc.createElement("InputData");
		// inputDataElm.appendChild(ShipmentTagElm);
		inputDataElm
				.appendChild(printInputDoc.importNode(ShipmentTagElm, true));

		printDocElm.appendChild(inputDataElm);

		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGPrintSplitAcctCode::printSplitAcctCode, XML to printDocumentSet : "
							+ XMLUtil.getXMLString(printInputDoc));
		}

		if (scnt > 0) {
			CommonUtilities.invokeAPI(env,
					NWCGConstants.API_PRINT_DOCUMENT_SET, printInputDoc);
		}

		if (logger.isVerboseEnabled()) {
			logger.verbose("Exiting NWCGPrintSplitAcctCode::printSplitAcctCode: "
					+ XMLUtil.getXMLString(printInputDoc));
		}
		return printInputDoc;
	}
}