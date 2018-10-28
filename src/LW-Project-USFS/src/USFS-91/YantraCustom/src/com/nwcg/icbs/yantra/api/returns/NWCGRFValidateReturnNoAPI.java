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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

public class NWCGRFValidateReturnNoAPI implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRFValidateReturnNoAPI.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 * 
	 *             convert <NextReceiptNo CacheID="CORMK" IgnoreOrdering="Y"
	 *             ReceiptNo="CORMK000503" SequenceType="RETURN"/>
	 * 
	 *             to
	 * 
	 *             <NWCGSequence OrganizationID="CORMK" SequenceType="RETURN"
	 *             SequenceNo="CORMK000483"/>
	 * 
	 *             and invoke service "NWCGValidateReturnNoService"
	 */

	public Document rfValidateReturnNoAPI(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGRFValidateReturnNoAPI::rfValidateReturnNoAPI");
		Document validateRNOInXML = XMLUtil.getDocument();

		Element elemNWCGSequence = validateRNOInXML
				.createElement("NWCGSequence");

		validateRNOInXML.appendChild(elemNWCGSequence);

		elemNWCGSequence.setAttribute("OrganizationID", inXML
				.getDocumentElement().getAttribute("CacheID"));
		elemNWCGSequence.setAttribute("SequenceNo", inXML.getDocumentElement()
				.getAttribute("ReceiptNo"));
		elemNWCGSequence.setAttribute("SequenceType", inXML
				.getDocumentElement().getAttribute("SequenceType"));

		Document validateRNOOutXML = CommonUtilities.invokeService(env,
				"NWCGValidateReturnNoService", validateRNOInXML);
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGRFValidateReturnNoAPI::rfValidateReturnNoAPI");
		return validateRNOOutXML;
	}
}