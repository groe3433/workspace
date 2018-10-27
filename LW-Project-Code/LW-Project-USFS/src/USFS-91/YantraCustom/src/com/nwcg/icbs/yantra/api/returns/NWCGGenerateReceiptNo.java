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

package com.nwcg.icbs.yantra.api.returns;

import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGGenerateReceiptNo implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGenerateReceiptNo.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	public Document getReceiptNo(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGGenerateReceiptNo::getReceiptNo");
		String OrgID = inXML.getDocumentElement().getAttribute("CacheID");
		String SeqType = inXML.getDocumentElement()
				.getAttribute("SequenceType");

		// Begin CR844 11302012
		// String ReceiptNo = GenerateSeqNo(env,OrgID,SeqType);
		int OrgIDlength = OrgID.length();
		String ReceiptNo = "";
		if (OrgIDlength > 0) {
			ReceiptNo = GenerateSeqNo(env, OrgID, SeqType);
			logger.verbose("@@@@@ NWCGGenerateReceiptNo::getReceiptNo - ReceiptNo : " + ReceiptNo);

			String strFirstCharInReceiptNo = ReceiptNo.substring(0, 1);
			if (strFirstCharInReceiptNo.equals("-")) {
				return null;
			}

			String strPrefixInReceiptNo = ReceiptNo.substring(0, OrgIDlength);
			if (!strPrefixInReceiptNo.equals(OrgID)) {
				return null;
			}
		}
		// End CR844 11302012

		Document UpdDoc = UpdateSeqNo(env, OrgID, ReceiptNo, SeqType);
		Document Receipt_Output = XMLUtil.newDocument();
		Element el_Receipt = Receipt_Output.createElement("NextReceiptNo");
		Receipt_Output.appendChild(el_Receipt);
		el_Receipt.setAttribute("ReceiptNo", ReceiptNo);

		logger.verbose("@@@@@ Receipt_Output doc :: " + XMLUtil.extractStringFromDocument(Receipt_Output));
		logger.verbose("@@@@@ Exiting NWCGGenerateReceiptNo::getReceiptNo");
		return Receipt_Output;
	}

	public String GenerateSeqNo(YFSEnvironment env, String CacheID,
			String SeqType) {
		logger.verbose("@@@@@ Entering NWCGGenerateReceiptNo::GenerateSeqNo");
		String SeqNo = "";
		String SeqNoStr = "";

		Document SeqDtls = null;
		Document getSeqInput = null;
		try {
			getSeqInput = XMLUtil.createDocument("NWCGSequence");
			getSeqInput.getDocumentElement().setAttribute("SequenceType",
					SeqType);
			getSeqInput.getDocumentElement().setAttribute("OrganizationID",
					CacheID);
			logger.verbose("@@@@@ NWCGGetSequenceListService service :: " + XMLUtil.extractStringFromDocument(getSeqInput));
			SeqDtls = CommonUtilities.invokeService(env,
					"NWCGGetSequenceListService", getSeqInput);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, GenerateSeqNo Msg : " + pce.getMessage());
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, GenerateSeqNo : " + pce.getStackTrace());
		} catch (Exception e) {
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, Exception Msg : " + e.getMessage());
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, StackTrace : " + e.getStackTrace());
		}

		NodeList SeqElmList = SeqDtls.getDocumentElement()
				.getElementsByTagName("NWCGSequence");

		if (SeqElmList.getLength() > 0) {
			Element rootElem = (Element) SeqElmList.item(0);
			SeqNo = rootElem.getAttribute("SequenceNo");
			int Seqlen = SeqNo.length();
			int pos = Seqlen - 6;
			SeqNoStr = SeqNo.substring(pos);
			int CurrentSeq = Integer.parseInt(SeqNoStr);
			CurrentSeq++;
			SeqNoStr = Integer.toString(CurrentSeq);
			if (SeqType.equalsIgnoreCase("LPN")) {
				SeqNoStr = CacheID + SeqType
						+ StringUtil.prepadStringWithZeros(SeqNoStr, 6);
			} else {
				SeqNoStr = CacheID
						+ StringUtil.prepadStringWithZeros(SeqNoStr, 6);
			}

		} else {
			int seqNo = 1;
			SeqNoStr = Integer.toString(seqNo);
			if (SeqType.equalsIgnoreCase("LPN")) {
				SeqNoStr = CacheID + SeqType
						+ StringUtil.prepadStringWithZeros(SeqNoStr, 6);
			} else {
				SeqNoStr = CacheID
						+ StringUtil.prepadStringWithZeros(SeqNoStr, 6);
			}
			getSeqInput.getDocumentElement().setAttribute("SequenceNo",
					SeqNoStr);
			try {
				logger.verbose("@@@@@ NWCGCreateSequenceListService service :: " + XMLUtil.extractStringFromDocument(getSeqInput));
				SeqDtls = CommonUtilities.invokeService(env,
						"NWCGCreateSequenceListService", getSeqInput);
			} catch (Exception e) {
				logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, Exception Msg : " + e.getMessage());
				logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, StackTrace : " + e.getStackTrace());
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGenerateReceiptNo::GenerateSeqNo");
		return SeqNoStr;
	}

	public Document UpdateSeqNo(YFSEnvironment env, String CacheID,
			String ReturnNo, String SeqType) {
		logger.verbose("@@@@@ Entering NWCGGenerateReceiptNo::UpdateSeqNo");
		String SeqNo = "";
		String SeqNoStr = "";

		Document SeqDtls = null;
		try {
			Document getSeqInput = XMLUtil.createDocument("NWCGSequence");
			getSeqInput.getDocumentElement().setAttribute("SequenceType",
					SeqType);
			getSeqInput.getDocumentElement().setAttribute("OrganizationID",
					CacheID);
			getSeqInput.getDocumentElement().setAttribute("SequenceNo",
					ReturnNo);
			logger.verbose("@@@@@ NWCGUpdateSequenceListService service :: " + XMLUtil.extractStringFromDocument(getSeqInput));
			SeqDtls = CommonUtilities.invokeService(env,
					"NWCGUpdateSequenceListService", getSeqInput);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, GenerateSeqNo Msg : " + pce.getMessage());
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, GenerateSeqNo : " + pce.getStackTrace());
		} catch (Exception e) {
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, Exception Msg : " + e.getMessage());
			logger.error("!!!!! NWCGReportsIssue::getIncidentDetails, StackTrace : " + e.getStackTrace());
		}
		logger.verbose("@@@@@ Exiting NWCGGenerateReceiptNo::UpdateSeqNo");
		return SeqDtls;
	}
}
