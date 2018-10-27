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
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGValidateReturnNo implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGValidateReturnNo.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}

	// Entry point method
	public Document ValidateReturnNo(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::ValidateReturnNo");
		if (logger.isVerboseEnabled()) {
			logger.verbose("Entering the NWCGValidateReturnNo API , input document is:"
					+ XMLUtil.getXMLString(inXML));
		}// End log

		Document ResultDoc = null;
		Element Result = null;
		try {
			ResultDoc = XMLUtil.newDocument();
			Result = ResultDoc.createElement("Result");
			ResultDoc.appendChild(Result);
			String SeqNoStr = "";
			Result.setAttribute("ValidReturnNo", "False");
			// String OrgID =
			// inXML.getDocumentElement().getAttribute("OrganizationID");
			String SeqNo = inXML.getDocumentElement()
					.getAttribute("SequenceNo");
			String SeqType = inXML.getDocumentElement().getAttribute(
					"SequenceType");
			SeqNo = SeqNo.trim();

			int orglen = 5;
			int seqlen = 11;
			if (SeqNo.length() != seqlen)
				return ResultDoc;

			String OrgID = SeqNo.substring(0, 5);

			int Seqlen = SeqNo.length();
			int pos = Seqlen - 6;
			SeqNoStr = SeqNo.substring(pos);

			if (!validateNumber(SeqNoStr))
				return ResultDoc;

			int UserSeq = Integer.parseInt(SeqNoStr);

			String CurrentSeqNo = GetSeqNo(env, OrgID);

			if (CurrentSeqNo.length() == 0)
				return ResultDoc;

			// Need to check existing Return No
			int rcnt = GetReceiptList(env, SeqNo);
			if (rcnt > 0) {
				Result.setAttribute("ValidReturnNo", "Received");
				return ResultDoc;
			}

			pos = CurrentSeqNo.length() - 6;
			SeqNoStr = CurrentSeqNo.substring(pos);
			int CurrentSeq = Integer.parseInt(SeqNoStr);

			if (UserSeq > CurrentSeq)
				return ResultDoc;
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGValidateReturnNo::, ParserConfigurationException Msg : "
					+ pce.getMessage());
			logger.error("NWCGValidateReturnNo::, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			logger.error("NWCGValidateReturnNo::Exception Msg : " + e.getMessage());
			logger.error("NWCGValidateReturnNo::StackTrace : " + e.getStackTrace());
		}
		Result.setAttribute("ValidReturnNo", "True");
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::ValidateReturnNo");
		return ResultDoc;
	}

	public String GetSeqNo(YFSEnvironment env, String CacheID) {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::GetSeqNo");
		String SeqNo = "";
		String Cache = "";

		Document SeqDtls = null;
		Document getSeqInput = null;
		try {
			getSeqInput = XMLUtil.createDocument("NWCGSequence");
			getSeqInput.getDocumentElement().setAttribute("SequenceType",
					"RETURN");
			getSeqInput.getDocumentElement().setAttribute("OrganizationID",
					CacheID);
			SeqDtls = CommonUtilities.invokeService(env,
					"NWCGGetSequenceListService", getSeqInput);
		} catch (Exception e) {
			logger.error("NWCGValidateReturnNo::GetSeqNo, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGValidateReturnNo::GetSeqNo, StackTrace : "
					+ e.getStackTrace());
		}

		NodeList SeqElmList = SeqDtls.getDocumentElement()
				.getElementsByTagName("NWCGSequence");

		if (SeqElmList.getLength() > 0) {
			Element rootElem = (Element) SeqElmList.item(0);
			SeqNo = rootElem.getAttribute("SequenceNo");
			Cache = rootElem.getAttribute("OrganizationID");
			if (!Cache.equals(CacheID)) {
				SeqNo = "";
				logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::GetSeqNo :: SeqNo Blank");
				return SeqNo;
			}
		}
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::GetSeqNo");
		return SeqNo;
	}

	public int GetReceiptList(YFSEnvironment env, String ReceiptNo) {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::GetReceiptList");
		int retcnt = 0;

		Document ReceiptDtls = null;
		Document ReceiptInput = null;
		try {
			ReceiptInput = XMLUtil.createDocument("Receipt");
			ReceiptInput.getDocumentElement().setAttribute("ReceiptNo",
					ReceiptNo);
			ReceiptDtls = CommonUtilities.invokeAPI(env, "getReceiptList",
					ReceiptInput);
		} catch (Exception e) {
			logger.error("NWCGValidateReturnNo::GetReceiptList, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGValidateReturnNo::GetReceiptList, StackTrace : "
					+ e.getStackTrace());
		}

		NodeList ReceiptElmList = ReceiptDtls.getDocumentElement()
				.getElementsByTagName("Receipt");
		retcnt = ReceiptElmList.getLength();
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::GetReceiptList");
		return retcnt;
	}

	public static boolean validateNumber(String num) {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::validateNumber");
		try {
			Integer.parseInt(num);
			logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::validateNumber :: true");
			return true;
		} catch (Exception e) {
			logger.error("!!!!! Exiting com.nwcg.icbs.yantra.api.returns.NWCGValidateReturnNo::validateNumber :: false");
			return false;
		}
	}
}// End Class

