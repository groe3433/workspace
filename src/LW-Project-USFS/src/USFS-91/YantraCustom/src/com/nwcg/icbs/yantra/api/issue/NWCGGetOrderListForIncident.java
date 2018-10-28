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

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetOrderListForIncident implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGGetOrderListForIncident.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws NWCGException
	 */
	public Document getOrderListForIncident(YFSEnvironment env, Document inXML) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGGetOrderListForIncident::getOrderListForIncident @@@@@");
		try {
			/**
			 * Search for OrderList where nwcg_incident_Order.IncidentNo =
			 * yfs_order_header.extn_incident_no and-or any other search
			 * parameters input by the user on SearchView Order_ByItem
			 * (resourceID=ISUYOMS040)
			 */
			logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::inXML=" + XMLUtil.extractStringFromDocument(inXML));
			Document opXML = CommonUtilities.invokeAPI(env, NWCGConstants.TEMPLATE_GET_ORDER_LIST_FOR_INCIDENT, NWCGConstants.API_GET_ORDER_LIST, inXML);
			logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::opXML=" + XMLUtil.extractStringFromDocument(opXML));
			/**
			 * Call Service NWCGGetIncidentOrderList using input template
			 * (NWCGAAConstants.INCIDENT_ORDER_TAG and output template
			 * NWCGAAConstants.INCIDENT_ORDER_LIST_TAG Determines IncidentId
			 */
			String incidentNo = "";
			String incidentYear = "";
			String strReplacedIncidentNo = "";
			String strReplacedIncidentYear = "";
			String flagReplacedIncidentNo = "no";
			String strReplacedIncidentNo2 = "";
			String strReplacedIncidentYear2 = "";
			String flagReplacedIncidentNo2 = "no";
			String strReplacedIncidentNo3 = "";
			String strReplacedIncidentYear3 = "";
			String flagReplacedIncidentNo3 = "no";
			Element eleminXML = inXML.getDocumentElement();
			NodeList nlOrderChildNodes = eleminXML.getChildNodes();
			boolean elemExtnFlag = false;
			for (int i = 0; i < nlOrderChildNodes.getLength() && !elemExtnFlag; i++) {
				Node tmpNode = nlOrderChildNodes.item(i);
				if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elemOrderChild = (Element) tmpNode;
					if (elemOrderChild.getNodeName().equalsIgnoreCase("Extn")) {
						incidentNo = elemOrderChild.getAttribute(NWCGConstants.INCIDENT_NO);
						logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::incidentNo=" + incidentNo);
						incidentYear = elemOrderChild.getAttribute(NWCGConstants.INCIDENT_YEAR);
						logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::incidentYear=" + incidentYear);
						elemExtnFlag = true;
					}
				}
			}
			if ((incidentNo != "") && (incidentNo != "null") && (incidentNo.length() > 0) && (incidentNo != "undefined")) {
				String incidentId = "";
				Document inXML0 = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_TAG);
				Element eleminXML0 = inXML0.getDocumentElement();
				eleminXML0.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incidentNo);
				eleminXML0.setAttribute(NWCGConstants.YEAR_ATTR, incidentYear);
				eleminXML0.setAttribute("IsOtherOrder", "N");
				logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::inXML0=" + XMLUtil.extractStringFromDocument(inXML0));
				Document opXML0 = CommonUtilities.invokeService(env, NWCGConstants.SERVICE_NWCG_GET_INCIDENT_ORDER_LIST, inXML0);
				logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::opXML0=" + XMLUtil.extractStringFromDocument(opXML0));
				NodeList nlNWCGIncidentOrder0 = opXML0.getDocumentElement().getElementsByTagName(NWCGAAConstants.INCIDENT_ORDER_TAG);
				logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::nlNWCGIncidentOrder0.getLength()=" + nlNWCGIncidentOrder0.getLength());
				if (nlNWCGIncidentOrder0 != null) {
					for (int j = 0; j < nlNWCGIncidentOrder0.getLength(); j++) {
						if (j == 0) {
							Element elemNWCGIncidentOrder0 = (Element) nlNWCGIncidentOrder0.item(j);
							Document docNWCGIncidentOrder0 = XMLUtil.getDocumentForElement(elemNWCGIncidentOrder0);
							/**
							 * Retrieving the incidentId which binds together 1
							 * or more IncidentNo documents Retrieving the
							 * replacedIncidentNo and replacedIncidentYear
							 * Retrieving the replacedIncidentNo2 and
							 * replacedIncidentYear2 Retrieving the
							 * replacedIncidentNo3 and replacedIncidentYear3
							 */
							String incidentNoTemp = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
							String incidentYearTemp = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.YEAR_ATTR);
							if (incidentNoTemp.equals(incidentNo) && incidentYearTemp.equals(incidentYear)) {
								incidentId = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.INCIDENT_ID_ATTR);
								logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::incidentId=" + incidentId);
								strReplacedIncidentNo = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.REPLACED_INCIDENT_NO);
								strReplacedIncidentYear = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR);
								if ((strReplacedIncidentNo != "") && (!strReplacedIncidentNo.equals("null")) && (strReplacedIncidentNo.length() > 0) && (!strReplacedIncidentNo.equals("undefined"))) {
									flagReplacedIncidentNo = "yes";
								}
								strReplacedIncidentNo2 = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.REPLACED_INCIDENT_NO_2);
								strReplacedIncidentYear2 = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR_2);
								if ((strReplacedIncidentNo2 != "") && (!strReplacedIncidentNo2.equals("null")) && (strReplacedIncidentNo2.length() > 0) && (!strReplacedIncidentNo2.equals("undefined"))) {
									flagReplacedIncidentNo2 = "yes";
								}
								strReplacedIncidentNo3 = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.REPLACED_INCIDENT_NO_3);
								strReplacedIncidentYear3 = elemNWCGIncidentOrder0.getAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR_3);
								if ((strReplacedIncidentNo3 != "") && (!strReplacedIncidentNo3.equals("null")) && (strReplacedIncidentNo3.length() > 0) && (!strReplacedIncidentNo3.equals("undefined"))) {
									flagReplacedIncidentNo3 = "yes";
								}
							}
						}
					}
				}
				if ((incidentId != "") && (incidentId != "null") && (incidentId.length() > 0) && (incidentId != "undefined")) {
					/**
					 * Call Service NWCGGetIncidentOrderList using input
					 * template (NWCGAAConstants.INCIDENT_ORDER_TAG with output
					 * template NWCGAAConstants.INCIDENT_ORDER_LIST_TAG Uses
					 * IncidentId
					 */
					Document inXML1 = XMLUtil.createDocument(NWCGAAConstants.INCIDENT_ORDER_TAG);
					Element eleminXML1 = inXML1.getDocumentElement();
					eleminXML1.setAttribute(NWCGConstants.INCIDENT_ID_ATTR, incidentId);
					eleminXML1.setAttribute("IgnoreOrdering", "Y");
					eleminXML1.setAttribute("IsOtherOrder", "N");
					logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::inXML1=" + XMLUtil.extractStringFromDocument(inXML1));
					Document opXML1 = CommonUtilities.invokeService(env, NWCGConstants.SERVICE_NWCG_GET_INCIDENT_ORDER_LIST, inXML1);
					logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::opXML1=" + XMLUtil.extractStringFromDocument(opXML1));
					NodeList nlNWCGIncidentOrder1 = opXML1.getDocumentElement().getElementsByTagName(NWCGAAConstants.INCIDENT_ORDER_TAG);
					if (nlNWCGIncidentOrder1 != null) {
						logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::nlNWCGIncidentOrder1.getLength()=" + nlNWCGIncidentOrder1.getLength());
						String incidentNoNEXT = "";
						String incidentYearNEXT = "";
						for (int j = 0; j < nlNWCGIncidentOrder1.getLength(); j++) {
							Element elemNWCGIncidentOrder1 = (Element) nlNWCGIncidentOrder1.item(j);
							Document docNWCGIncidentOrder1 = XMLUtil.getDocumentForElement(elemNWCGIncidentOrder1);
							logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::docNWCGIncidentOrder1=" + XMLUtil.extractStringFromDocument(docNWCGIncidentOrder1));
							// Retrieving the incident key
							incidentNoNEXT = elemNWCGIncidentOrder1.getAttribute("IncidentNo");
							incidentYearNEXT = elemNWCGIncidentOrder1.getAttribute("Year");
							logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident:: incidentNoNEXT=" + incidentNoNEXT);
							logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::incidentYearNEXT=" + incidentYearNEXT);
							if (incidentNo.equals(incidentNoNEXT) && incidentYear.equals(incidentYearNEXT)) {
							} else if ((flagReplacedIncidentNo.equals("yes") && strReplacedIncidentNo.equals(incidentNoNEXT) && strReplacedIncidentYear.equals(incidentYearNEXT)) || (flagReplacedIncidentNo2.equals("yes") && strReplacedIncidentNo2.equals(incidentNoNEXT) && strReplacedIncidentYear2.equals(incidentYearNEXT)) || (flagReplacedIncidentNo3.equals("yes") && strReplacedIncidentNo3.equals(incidentNoNEXT) && strReplacedIncidentYear3.equals(incidentYearNEXT))) {
								// set incidentNo = incidentNoNEXT and incidentYear=incidentYearNEXT
								NodeList nlExtninXML2 = inXML.getElementsByTagName("Extn");
								boolean elemExtnFlag2 = false;
								for (int k = 0; k < nlExtninXML2.getLength() && !elemExtnFlag2; k++) {
									Element elemExtninXML2 = (Element) nlExtninXML2.item(k);
									elemExtninXML2.setAttribute("ExtnIncidentNo", incidentNoNEXT);
									elemExtninXML2.setAttribute("ExtnIncidentYear", incidentYearNEXT);
									elemExtnFlag2 = true;
									logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident:: elemExtnFlag2=true");
								}
								logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident:: modified inXML=" + XMLUtil.extractStringFromDocument(inXML));
								Document opXML2 = CommonUtilities.invokeAPI(env, NWCGConstants.TEMPLATE_GET_ORDER_LIST_FOR_INCIDENT, NWCGConstants.API_GET_ORDER_LIST, inXML);
								logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::opXML2=" + XMLUtil.extractStringFromDocument(opXML2));
								Element elemopXML2 = opXML2.getDocumentElement();
								NodeList nlopXML2 = opXML2.getElementsByTagName("Order");
								logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::nlopXML2.getLength()=" + nlopXML2.getLength());
								Element rootopXML = opXML.getDocumentElement();
								int intTotalOrderList = Integer.parseInt(rootopXML.getAttribute("TotalOrderList"));
								for (int l = 0; l < nlopXML2.getLength(); l++) {
									intTotalOrderList = intTotalOrderList + 1;
									rootopXML.setAttribute("TotalOrderList", Integer.toString(intTotalOrderList));
									logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident:: intTotalOrderList=" + intTotalOrderList);
									logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::BEFORE opXML=" + XMLUtil.extractStringFromDocument(opXML));
									logger.verbose("NWCGGetOrderListForIncident::getOrderListForIncident:: l=" + l);
									Element importedElement = (Element) nlopXML2.item(l);
									Node importedNode = opXML.importNode(importedElement, true);
									rootopXML.appendChild(importedNode);
									logger.verbose("NWCGGetOrderListForIncident::getOrderListForIncident::AFTER opXML=" + XMLUtil.extractStringFromDocument(opXML));
								}
							}
						}
					}
					//Restore Document inXML to original content
					logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::BEFORE inXML reset to original input values");
					Element eleminXML3 = inXML.getDocumentElement();
					NodeList nlOrderChildNodes3 = eleminXML3.getChildNodes();
					boolean elemExtnFlag3 = false;
					for (int k = 0; k < nlOrderChildNodes3.getLength() && !elemExtnFlag3; k++) {
						Node tmpNode3 = nlOrderChildNodes3.item(k);
						if (tmpNode3.getNodeType() == Node.ELEMENT_NODE) {
							Element elemOrderChild3 = (Element) tmpNode3;
							if (elemOrderChild3.getNodeName().equalsIgnoreCase("Extn")) {
								logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident:: incidentNo=" + incidentNo);
								logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident:: incidentYear= " + incidentYear);
								elemOrderChild3.setAttribute(NWCGConstants.INCIDENT_NO, incidentNo);
								if ((incidentYear != "") && (incidentYear != "null") && (incidentYear.length() > 0) && (incidentYear != "undefined")) {
									elemOrderChild3.setAttribute(NWCGConstants.INCIDENT_YEAR, incidentYear);
								}
								elemExtnFlag3 = true;
							}
						}
					}
					logger.verbose("@@@@@ NWCGGetOrderListForIncident::getOrderListForIncident::AFTER inXML reset to original input values" + XMLUtil.extractStringFromDocument(inXML));
				} else {
				}
			}
			logger.verbose("@@@@@ Exiting NWCGGetOrderListForIncident::getOrderListForIncident (1) @@@@@");
			return opXML;
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException in api " + NWCGConstants.API_GET_ORDER_LIST + " while running " + NWCGConstants.SERVICE_NWCG_GET_ORDER_LIST + " : " + pce.getMessage(), pce);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception in api " + NWCGConstants.API_GET_ORDER_LIST + " while running " + NWCGConstants.SERVICE_NWCG_GET_ORDER_LIST + " : " + "Exception: " + e.getMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGGetOrderListForIncident::getOrderListForIncident @@@@@ ");
		return null;
	}
}