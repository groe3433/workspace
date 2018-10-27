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

package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateReservationSNo implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGValidateReservationSNo.class);
	
	public void setProperties(Properties props) throws Exception {
	}
	
	/**
	 * Input: <ValidationRequest RequestNo="" IncidentNo=""/> 
	 * Response: <ValidationResponse Result="Y/N" Message="Faliure message"/>
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document validateSNo(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateReservationSNo::validateSNo @@@@@");
		Element rootElem = inDoc.getDocumentElement();
		NodeList nlOrderLines = rootElem.getElementsByTagName("OrderLines");
		// get the orderlines this is help us to remove the orderline elements
		if (nlOrderLines != null && nlOrderLines.getLength() >= 1) {
			Element elemOrderLines = (Element) nlOrderLines.item(0);
			// this will carry all the nodes to be remvoed ArrayList removeList = new ArrayList(20);
			NodeList nlOrderLine = elemOrderLines.getElementsByTagName("OrderLine");
			if (nlOrderLine != null) {
				int iTotal = nlOrderLine.getLength();
				for (int index = 0; index < iTotal; index++) {
					Element elemOrderLine = (Element) nlOrderLine.item(index);
					NodeList nlItem = elemOrderLine.getElementsByTagName("Item");
					String strOrderLineKey = elemOrderLine.getAttribute("OrderLineKey");
					Document getOrderLineDetailsInput = XMLUtil.createDocument("OrderLineDetail");
					getOrderLineDetailsInput.getDocumentElement().setAttribute("OrderLineKey", strOrderLineKey);
					env.setApiTemplate("getOrderLineDetails", "NWCGCheckReservationRequestNo_getOrderLineDetails");
					Document returnDoc = CommonUtilities.invokeAPI(env, "getOrderLineDetails", getOrderLineDetailsInput);
					env.clearApiTemplate("getOrderLineDetails");
					NodeList nlOrderLineExtn = returnDoc.getDocumentElement().getElementsByTagName("Extn");
					int nlLength = nlOrderLineExtn.getLength();
					for (int i = 0; i < nlLength; i++) {
						Element olElm = (Element) nlOrderLineExtn.item(i);
						String strExtnRequestNo = olElm.getAttribute("ExtnRequestNo");
						if (strExtnRequestNo.equals("")) {
							logger.verbose("@@@@@ strExtnRequestNo :: " + strExtnRequestNo);
							throw new NWCGException("NWCG_VALIDATE_REQUEST_NO_003");
						} else {
							// Do Nothing...
						}
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGValidateReservationSNo::validateSNo @@@@@");
		return inDoc;
	}
}