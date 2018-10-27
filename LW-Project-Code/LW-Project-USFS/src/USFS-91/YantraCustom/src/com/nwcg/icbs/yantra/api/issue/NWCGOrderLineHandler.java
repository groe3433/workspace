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
import org.w3c.dom.Node;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

/**
 * Class for Sorting and Setting Issue details. 
 * 
 * @author Oxford
 * 
 */
public class NWCGOrderLineHandler implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGOrderLineHandler.class);

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * 
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document getSortedOrderLineList(YFSEnvironment env, Document ipDoc)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGOrderLineHandler::getSortedOrderLineList @@@@@");
		logger.verbose("@@@@@ inDoc : " + XMLUtil.getXMLString(ipDoc));
		// Begin CR830 01252013 - Get ALL OrderLines for the document using below API call
		Element ipDocElement = ipDoc.getDocumentElement();
		String strMaximumRecords = ipDocElement.getAttribute("MaximumRecords");
		logger.verbose("@@@@@ strMaximumRecords : " + strMaximumRecords);
		String strFromPrimeLineNo = ipDocElement.getAttribute("FromPrimeLineNo");
		logger.verbose("@@@@@ strFromPrimeLineNo : " + strFromPrimeLineNo);
		String strToPrimeLineNo = ipDocElement.getAttribute("ToPrimeLineNo");
		logger.verbose("@@@@@ strToPrimeLineNo : " + strToPrimeLineNo);
		ipDocElement.setAttribute("FromPrimeLineNo", "0");
		ipDocElement.setAttribute("ToPrimeLineNo", strMaximumRecords);
		// End CR830 01252013
		Document opDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.TEMPLATE_NWCG_GET_SORTED_ORDER_LINE_LIST,
				NWCGConstants.API_GET_ORDER_LINE_LIST, ipDoc);
		// Begin CR830 01252013
		Element opDocElement = opDoc.getDocumentElement();
		opDocElement.setAttribute("MaximumRecords", strMaximumRecords);
		opDocElement.setAttribute("FromPrimeLineNo", strFromPrimeLineNo);
		opDocElement.setAttribute("ToPrimeLineNo", strToPrimeLineNo);
		// End CR830 01252013
		logger.verbose("@@@@@ opDoc : " + XMLUtil.getXMLString(opDoc));
		logger.verbose("@@@@@ Exiting NWCGOrderLineHandler::getSortedOrderLineList @@@@@");
		return opDoc;
	}

	/**
	 * This method will sort the Order Lines and the ROSS Special Needs, however the current 
	 * implementation uses XSL files to do the sorting and hence this can be used for 
	 * debugging purposes if you set it up in the configurator to do so. 
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document reSortOrderLineList(YFSEnvironment env, Document inDoc)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGOrderLineHandler::reSortOrderLineList @@@@@");
		logger.verbose("@@@@@ opDoc : " + XMLUtil.getXMLString(inDoc));
		// Begin CR830 - Jan 25, 2013
		Element inDocElement = inDoc.getDocumentElement();
		String strFromPrimeLineNo = inDocElement.getAttribute("FromPrimeLineNo");
		logger.verbose("@@@@@ strFromPrimeLineNo : " + strFromPrimeLineNo);
		String strToPrimeLineNo = inDocElement.getAttribute("ToPrimeLineNo");
		logger.verbose("@@@@@ strToPrimeLineNo : " + strToPrimeLineNo);
		NodeList nlOrderLineList0 = inDoc.getDocumentElement().getElementsByTagName("OrderLine");
		int nlLength = (nlOrderLineList0.getLength() - 1);
		for (int i = 0; i < nlLength; i++) {
			for (int k = 0; k < (nlLength - i); k++) {
				Element elema = (Element) nlOrderLineList0.item(k);
				Element elemb = (Element) nlOrderLineList0.item(k + 1);
				NodeList nlelemaExtn = elema.getElementsByTagName("Extn");
				NodeList nlelembExtn = elemb.getElementsByTagName("Extn");
				Element elemaExtn = (Element) nlelemaExtn.item(0);
				Element elembExtn = (Element) nlelembExtn.item(0);
				String strA = elemaExtn.getAttribute("ExtnRequestNo");
				String strB = elembExtn.getAttribute("ExtnRequestNo");
				try {
					// try for a regular number
					Double d1 = Double.parseDouble(strA);
					Double d2 = Double.parseDouble(strB);
					if(d1 > d2) {
						(nlOrderLineList0.item(k)).getParentNode().insertBefore(nlOrderLineList0.item(k + 1), nlOrderLineList0.item(k));
					}
				} catch (Exception e) {
					if(strA.length() > 2 && strB.length() > 2) {
						if(strA.charAt(1) == '-' && strB.charAt(1) == '-') {
							if(strA.charAt(0) == strB.charAt(0)) {
								try {
									// try for a special "-" number
									Double d1 = Double.parseDouble(strA.substring(2));
									Double d2 = Double.parseDouble(strB.substring(2));
									if(d1 > d2) {
										(nlOrderLineList0.item(k)).getParentNode().insertBefore(nlOrderLineList0.item(k + 1), nlOrderLineList0.item(k));
									}
								} catch (Exception generalException2) {
									// special "-" number had other characters
									if((strA.compareTo(strB) >= 0)) {
										(nlOrderLineList0.item(k)).getParentNode().insertBefore(nlOrderLineList0.item(k + 1), nlOrderLineList0.item(k));
									}
								}
							} else {
								// the first characters were NOT equal. (ie - E-34 vs S-78)
								if((strA.compareTo(strB) >= 0)) {
									(nlOrderLineList0.item(k)).getParentNode().insertBefore(nlOrderLineList0.item(k + 1), nlOrderLineList0.item(k));
								}
							}
						} else {
							// there was no "-" in the second character
							if((strA.compareTo(strB) >= 0)) {
								(nlOrderLineList0.item(k)).getParentNode().insertBefore(nlOrderLineList0.item(k + 1), nlOrderLineList0.item(k));
							}
						}
					} else {
						// the string was smaller than 2
						if((strA.compareTo(strB) >= 0)) {
							(nlOrderLineList0.item(k)).getParentNode().insertBefore(nlOrderLineList0.item(k + 1), nlOrderLineList0.item(k));
						}
					}
				}
			}
		}
		logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: nlOrderLineList0.getLength()="
						+ nlOrderLineList0.getLength());
		logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: INTERMEDIATE inDoc="
						+ XMLUtil.extractStringFromDocument(inDoc));
		int intToPrimeLineNo = 0;
		int intFromPrimeLineNo = 0;
		if ((strToPrimeLineNo != null) && (strToPrimeLineNo.length() > 0)
				&& (!strToPrimeLineNo.equals(""))) {
			intToPrimeLineNo = Integer.parseInt(strToPrimeLineNo);
			logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: intToPrimeLineNo="
							+ intToPrimeLineNo);
			if (intToPrimeLineNo > 0) {
				if (intToPrimeLineNo > nlOrderLineList0.getLength()) {
					intToPrimeLineNo = nlOrderLineList0.getLength();
				}
				while ((nlOrderLineList0.getLength()) > intToPrimeLineNo) {
					logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step0="
									+ (nlOrderLineList0.getLength() - 1));
					Element thisNode = (Element) nlOrderLineList0
							.item(nlOrderLineList0.getLength() - 1);
					logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step1="
									+ (nlOrderLineList0.getLength() - 1));
					thisNode.getParentNode().removeChild(thisNode);
					logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step2="
									+ (nlOrderLineList0.getLength() - 1));
				}
			}
		}
		logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step1::nlOrderLineList0.getLength()="
						+ nlOrderLineList0.getLength());
		if ((strFromPrimeLineNo != null) && (strFromPrimeLineNo.length() > 0)
				&& (!strFromPrimeLineNo.equals(""))) {
			intFromPrimeLineNo = Integer.parseInt(strFromPrimeLineNo);
			logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: intFromPrimeLineNo="
							+ intFromPrimeLineNo);
			if (intFromPrimeLineNo > 1) {
				while (nlOrderLineList0.getLength() > (intToPrimeLineNo
						- intFromPrimeLineNo + 1)) {
					logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step0");
					Element thisNode = (Element) nlOrderLineList0.item(0);
					logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step1");
					thisNode.getParentNode().removeChild(thisNode);
					logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step2");
				}
			}
		}
		logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: Step3::nlOrderLineList0.getLength()="
						+ nlOrderLineList0.getLength());
		logger.verbose("@@@@@ NWCGOrderLineHandler::reSortOrderLineList:: OUTPUT opDoc="
						+ XMLUtil.extractStringFromDocument(inDoc));
		// END CR830 - Jan 25, 2013
		logger.verbose("@@@@@ Exiting NWCGOrderLineHandler::reSortOrderLineList @@@@@");
		return inDoc;
	}

	/**
	 * Sets the ROSS Special Needs GUI box. 
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document setROSSSpecialNeeds(YFSEnvironment env, Document inDoc)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGOrderLineHandler::setROSSSpecialNeeds @@@@@");
		// Begin CR830 - Jan 25, 2013
		logger.verbose("@@@@@ NWCGOrderLineHandler::setROSSSpecialNeeds:: INPUT inDoc="
						+ XMLUtil.extractStringFromDocument(inDoc));
		// Set attribute at the Order header level
		NodeList nlOrderLineList = inDoc.getDocumentElement()
				.getElementsByTagName("OrderLine");
		String strROSSSpecialNeeds = "";
		for (int j = 0; j < nlOrderLineList.getLength(); j++) {
			Element elemOrderLine = (Element) nlOrderLineList.item(j);
			// only concatenate the ExtnRequestNo of OrderLines with 1 or more Instruction
			NodeList nlOrderLineInstruction = elemOrderLine
					.getElementsByTagName("Instruction");
			logger.verbose("@@@@@ NWCGOrderLineHandler::setROSSSpecialNeeds:: nlOrderLineInstruction.getLength()="
							+ nlOrderLineInstruction.getLength());
			if (nlOrderLineInstruction.getLength() > 0) {
				NodeList nlOrderLineExtn = elemOrderLine
						.getElementsByTagName("Extn");
				logger.verbose("@@@@@ NWCGOrderLineHandler::setROSSSpecialNeeds:: nlOrderLineExtn.getLength()="
								+ nlOrderLineExtn.getLength());
				if (nlOrderLineExtn.getLength() > 0) {
					Element elemOrderLineExtn = (Element) nlOrderLineExtn
							.item(0);
					if (strROSSSpecialNeeds.length() == 0) {
						strROSSSpecialNeeds += elemOrderLineExtn
								.getAttribute("ExtnRequestNo");
					} else {
						strROSSSpecialNeeds += ":"
								+ elemOrderLineExtn
										.getAttribute("ExtnRequestNo");
					}
					logger.verbose("@@@@@ NWCGOrderLineHandler::setROSSSpecialNeeds:: strROSSSpecialNeeds="
									+ strROSSSpecialNeeds);
				}
			}
		}
		Element elemRoot = inDoc.getDocumentElement();
		elemRoot.setAttribute("ROSSSpecialNeeds", strROSSSpecialNeeds);
		logger.verbose("@@@@@ NWCGOrderLineHandler::setROSSSpecialNeeds:: OUTPUT inDoc="
						+ XMLUtil.extractStringFromDocument(inDoc));
		// END CR830 - Jan 25, 2013
		logger.verbose("@@@@@ Exiting NWCGOrderLineHandler::setROSSSpecialNeeds @@@@@");
		return inDoc;
	}
}