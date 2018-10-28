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

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.issue.util.NWCGCloseAlert;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.ib.read.handler.NWCGResourceRequestReassignNotificationHandler;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckPendingRR implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCheckPendingRR.class);

	private String incNo = "";
	private String incYr = "";
	private Vector<String> vecPendRRDistIds = new Vector<String>();
	private Hashtable<String, Vector<String>> htDistID2PendRRKeys = new Hashtable<String, Vector<String>>();

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public Document checkAndTriggerPendingRR(YFSEnvironment env, Document docIP)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGCheckPendingRR::checkAndTriggerPendingRR @@@@@");
		logger.verbose("@@@@@ Input XML : "
				+ XMLUtil.extractStringFromDocument(docIP));
		String inc2incTxIssue = docIP.getDocumentElement().getAttribute(
				NWCGConstants.ORDER_NO);
		Vector<String> vecReqNos = setIncKeyAndGetReqs(docIP);
		if (vecReqNos != null && vecReqNos.size() > 0) {
			logger.verbose("@@@@@ No of inc 2 inc transfer lines : " + vecReqNos.size());
			for (int i = 0; i < vecReqNos.size(); i++) {
				String reqNo = vecReqNos.get(i);
				checkAndUpdatePendingRR(env, reqNo);
			}

			// If the vector containing distribution ids size is greater than 1,
			// then
			// check whether all the lines from that distribution id are ready
			// to be
			// triggered. If so, trigger resource reassignment and close this
			// particular
			// alert.
			// For our code, if pendRRXml is null, it means that atleast one of
			// the pending RR
			// is not READY_TO_TRIGGER status
			if (vecPendRRDistIds != null && vecPendRRDistIds.size() > 0) {
				for (int distIdCount = 0; distIdCount < vecPendRRDistIds.size(); distIdCount++) {
					String distId = vecPendRRDistIds.get(distIdCount);
					String pendRRXml = isDistReadyToTrigger(env, distId);
					if (pendRRXml != null) {
						String closeReason = "Automatically resolved by ICBSR. Pending RR is triggered as "
								+ "incident to incident transfer issue "
								+ inc2incTxIssue + " is confirmed";
						// For Pending RR, RR alert is being created without
						// shipnode as the incident does not contain
						// CacheOrganization, so the code in RRhandler is
						// creating with shipnode of "CacheOrganization"
						// Since "CacheOrganization" is not a valid ship node, a
						// normal user will not be able to
						// see those alert messages. For example, slegg user
						// will not be able to close the alert,
						// so changing the user to ROSSInterface only for
						// closing the alert.
						logger.verbose("@@@@@ Closing alert with user " + NWCGAAConstants.ENV_USER_ID);
						YFSEnvironment tmpEnv = CommonUtilities
								.createEnvironment(NWCGAAConstants.ENV_USER_ID,
										NWCGAAConstants.ENV_PROG_ID);
						NWCGCloseAlert.closeAlert(tmpEnv, distId,
								"ResourceRequestReassignNotification",
								closeReason);

						// Set the distribution ID in input XML
						Document docTmp = XMLUtil.getDocument(pendRRXml);
						docTmp.getDocumentElement().setAttribute(
								NWCGConstants.INC_NOTIF_DIST_ID_ATTR, distId);
						triggerPendingRR(env, docTmp);

						// Update all the lines for this distribution id to
						// PROCESSED
						updatePendRRStatus(env, distId, "PROCESSED");
					}
				}
			} else {
				logger.verbose("@@@@@ There are no order lines in Pending RR table, so not doing anything");
			}
		} else {
			logger.verbose("@@@@@ There are no order lines for this incident transfer issue " + inc2incTxIssue);
		}
		logger.verbose("@@@@@ Exiting NWCGCheckPendingRR::checkAndTriggerPendingRR @@@@@");
		return null;
	}

	/**
	 * This method sets the incident number and year and retrieves request
	 * numbers from the input xml to this class
	 * 
	 * @param docIP
	 * @return
	 */
	private Vector<String> setIncKeyAndGetReqs(Document docIP) {
		logger.verbose("@@@@@ Entering NWCGCheckPendingRR::setIncKeyAndGetReqs @@@@@");
		Vector<String> vecReqNos = new Vector<String>();
		Element elmInc2IncXfer = docIP.getDocumentElement();
		NodeList nlXfer = elmInc2IncXfer.getChildNodes();
		for (int i = 0; i < nlXfer.getLength(); i++) {
			Node tmpNode = nlXfer.item(i);
			if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = tmpNode.getNodeName();
				if (nodeName.equalsIgnoreCase(NWCGConstants.EXTN)) {
					Element elmOrderExtn = (Element) tmpNode;
					incNo = elmOrderExtn
							.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_NO);
					incYr = elmOrderExtn
							.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_YR);
				} else if (nodeName.equalsIgnoreCase(NWCGConstants.ORDER_LINES)) {
					Element elmOLs = (Element) tmpNode;
					NodeList nlOLExtn = elmOLs
							.getElementsByTagName(NWCGConstants.EXTN);
					for (int extnElms = 0; extnElms < nlOLExtn.getLength(); extnElms++) {
						Element elmOLExtn = (Element) nlOLExtn.item(extnElms);
						String reqNo = elmOLExtn
								.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
						vecReqNos.add(reqNo);
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCheckPendingRR::setIncKeyAndGetReqs @@@@@");
		return vecReqNos;
	}

	/**
	 * This method will check if this request line is present in Pending RR
	 * table. If it is present, then it will update the status to
	 * READY_FOR_TRIGGER. It will keep a list of distribution ids in class
	 * variable vector. Below is the use of vector Scenario 1: ROSS sends 9 req
	 * lines in a single inc2inc transfer from Inc A to Inc B. Now, ROSS can
	 * send inc2inc transfer for all the 9 lines from B to C in a single inc2inc
	 * transfer request or it can send 9 different requests. If either case, we
	 * need to trigger resource reassignment for all the distribution ids (if
	 * the other pending RR lines in this table are ready to be triggered), so
	 * we are storing the distribution ids in this vector
	 * 
	 * @param env
	 * @param reqNo
	 */
	private void checkAndUpdatePendingRR(YFSEnvironment env, String reqNo) {
		logger.verbose("@@@@@ Entering NWCGCheckPendingRR::checkAndUpdatePendingRR @@@@@");
		try {
			Document docPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmPendRR = docPendRR.getDocumentElement();
			elmPendRR.setAttribute("FromIncidentNo", incNo);
			elmPendRR.setAttribute("FromIncidentYear", incYr);
			elmPendRR.setAttribute("FromRequestNo", reqNo);
			elmPendRR.setAttribute(NWCGConstants.STATUS_ATTR,
					NWCGConstants.STATUS_NOT_READY_TO_TRIGGER);

			logger.verbose("@@@@@ Input XML : " + XMLUtil.extractStringFromDocument(docPendRR));
			Document docOPPendRRList = CommonUtilities.invokeService(env,
					NWCGConstants.SVC_GET_PENDING_RR_LIST, docPendRR);
			if (docOPPendRRList != null) {
				Element elmPendingRRList = docOPPendRRList.getDocumentElement();
				NodeList nlPendingRR = elmPendingRRList
						.getElementsByTagName("NWCGPendingRr");

				if (nlPendingRR != null && nlPendingRR.getLength() > 0) {
					Element elmPendingRR = (Element) nlPendingRR.item(0);
					String distId = elmPendingRR
							.getAttribute(NWCGConstants.DIST_ID_ATTR);
					String pendRRKey = elmPendingRR.getAttribute("PendRRKey");
					if (vecPendRRDistIds != null && vecPendRRDistIds.size() > 0) {
						if (!vecPendRRDistIds.contains(distId)) {
							vecPendRRDistIds.add(distId);
						}
					} else {
						vecPendRRDistIds.add(distId);
					}

					Document docUpdtPendRR = XMLUtil
							.createDocument("NWCGPendingRr");
					Element elmUpdtPendRR = docUpdtPendRR.getDocumentElement();
					elmUpdtPendRR.setAttribute("PendRRKey", pendRRKey);
					elmUpdtPendRR.setAttribute(NWCGConstants.STATUS_ATTR,
							NWCGConstants.STATUS_READY_TO_TRIGGER);
					logger.verbose("@@@@@ Input XML for change order : " + XMLUtil.extractStringFromDocument(docUpdtPendRR));

					Document docOPUpdtPendRR = CommonUtilities.invokeService(
							env, NWCGConstants.SVC_CHANGE_PENDING_RR,
							docUpdtPendRR);
					if (docOPUpdtPendRR == null) {
						logger.verbose("@@@@@ Unable to update Request " + incNo + "/" + incYr + "/" + reqNo + " to " + NWCGConstants.STATUS_READY_TO_TRIGGER);
					}
				} else {
					logger.verbose("@@@@@ nlPendingRR is NULL");
					logger.verbose("@@@@@ Request " + incNo + "/" + incYr + "/" + reqNo + " is not present in pending RR");
				}
			} else {
				logger.verbose("@@@@@ Request " + incNo + "/" + incYr + "/" + reqNo + " is not present in pending RR list");
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (SAXException sae) {
			logger.error("!!!!! Caught SAXException : " + sae);
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException : " + ioe);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		} catch (Exception e) {
			logger.error("!!!!! Request " + incNo + "/" + incYr + "/" + reqNo + " is not present in pending RR list");
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGCheckPendingRR::checkAndUpdatePendingRR @@@@@");
	}

	/**
	 * This method will get all the entries for that distribution id from
	 * pending RR table If all of them are in ready to be triggered status, then
	 * it will return the resource reassignment input xml or else it will return
	 * null value
	 * 
	 * @param env
	 * @param distId
	 * @return
	 */
	private String isDistReadyToTrigger(YFSEnvironment env, String distId) {
		logger.verbose("@@@@@ Entering NWCGCheckPendingRR::isDistReadyToTrigger @@@@@");
		String rrXml = null;
		try {
			Document docIPPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmIPPendRR = docIPPendRR.getDocumentElement();
			elmIPPendRR.setAttribute(NWCGConstants.DIST_ID_ATTR, distId);
			Document docOPPendRRList = CommonUtilities.invokeService(env,
					NWCGConstants.SVC_GET_PENDING_RR_LIST, docIPPendRR);
			if (docOPPendRRList != null) {
				Element elmPendingRRList = docOPPendRRList.getDocumentElement();
				NodeList nlPendingRR = elmPendingRRList
						.getElementsByTagName("NWCGPendingRr");
				if (nlPendingRR != null && nlPendingRR.getLength() > 0) {
					Vector<String> pendRRKeys = new Vector<String>();
					for (int i = 0; i < nlPendingRR.getLength(); i++) {
						Element elmOPPendRR = (Element) nlPendingRR.item(i);
						String pendRRStatus = elmOPPendRR
								.getAttribute(NWCGConstants.STATUS_ATTR);
						pendRRKeys.add(elmOPPendRR.getAttribute("PendRRKey"));
						if (pendRRStatus
								.equalsIgnoreCase(NWCGConstants.STATUS_READY_TO_TRIGGER)) {
							rrXml = elmOPPendRR
									.getAttribute(NWCGConstants.MESSAGE_ATTR);
						} else {
							rrXml = null;
							logger.verbose("@@@@@ Request " + elmOPPendRR.getAttribute(NWCGConstants.FROM_REQUEST_NO) + " is in status " + pendRRStatus + ", so not triggering pending RR");
							break;
						}
					} 
					htDistID2PendRRKeys.put(distId, pendRRKeys);
				} else {
					logger.verbose("@@@@@ THIS SHOULD NOT HAPPEN @@@@@");
					logger.verbose("@@@@@ Unable to find any entry for distribution ID : " + distId);
				}
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (SAXException sae) {
			logger.error("!!!!! Caught SAXException : " + sae);
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException : " + ioe);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}
		if (rrXml == null) {
			logger.verbose("@@@@@ Distribution ID : " + distId + " is not ready to trigger");
		} else {
			logger.verbose("@@@@@ Distribution ID : " + distId + " is ready to trigger for pending RR");
		}
		logger.verbose("@@@@@ Exiting NWCGCheckPendingRR::isDistReadyToTrigger @@@@@");
		return rrXml;
	}

	/**
	 * This method will make pending RR call as if we received it from ROSS for
	 * the first time
	 * 
	 * @param env
	 * @param pendRRIPXml
	 * @return
	 */
	private Document triggerPendingRR(YFSEnvironment env, Document docPendRR) {
		logger.verbose("@@@@@ Entering NWCGCheckPendingRR::triggerPendingRR @@@@@");
		NWCGResourceRequestReassignNotificationHandler rrNotifHdlr = new NWCGResourceRequestReassignNotificationHandler();
		try {
			rrNotifHdlr.process(env, docPendRR);
		} catch (NWCGException e) {
			logger.error("!!!!! Caught NWCGException : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGCheckPendingRR::triggerPendingRR @@@@@");
		return null;
	}

	/**
	 * Update Distribution ID to the passed status. As of now, we are passing
	 * only PROCESSED as the status
	 * 
	 * @param env
	 * @param distId
	 * @param status
	 */
	private void updatePendRRStatus(YFSEnvironment env, String distId, String status) {
		logger.verbose("@@@@@ Entering NWCGCheckPendingRR::updatePendRRStatus @@@@@");
		try {
			Document docPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmPendRR = docPendRR.getDocumentElement();
			elmPendRR.setAttribute(NWCGConstants.STATUS_ATTR, status);
			Vector<String> vecPendRRKeys = htDistID2PendRRKeys.get(distId);
			for (int i = 0; i < vecPendRRKeys.size(); i++) {
				String pendRRKey = vecPendRRKeys.get(i);
				elmPendRR.setAttribute("PendRRKey", pendRRKey);
				logger.verbose("@@@@@ Input XML to update status : " + XMLUtil.extractStringFromDocument(docPendRR));
				Document docOPPendRR = CommonUtilities.invokeService(env, NWCGConstants.SVC_CHANGE_PENDING_RR, docPendRR);
				if (docOPPendRR != null) {
					logger.verbose("@@@@@ Updated Pending RR Key " + pendRRKey + " to " + status);
				} else {
					logger.verbose("@@@@@ Unable to update Pending RR Key " + pendRRKey + " to " + status);
				}

			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (SAXException sae) {
			logger.error("!!!!! Caught SAXException : " + sae);
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException : " + ioe);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGCheckPendingRR::updatePendRRStatus @@@@@");
	}
}