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

package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGLockIncident implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGLockIncident.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * 
	 * @param env
	 * @param ipDoc
	 * @param modReason
	 * @return
	 * @throws Exception
	 */
	public Document lockIncident(YFSEnvironment env, Document ipDoc, String modReason) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLockIncident::lockIncident");
		logger.verbose("@@@@@ NWCGLockIncident::lockIncident::ipDoc :: " + XMLUtil.getXMLString(ipDoc));
		if (ipDoc == null){
			logger.verbose("@@@@@ Exiting NWCGLockIncident::lockIncident (null)");
			return ipDoc;
		}
		try {
			Element ipDocElm = ipDoc.getDocumentElement();
			String incidentNumber = ipDocElm.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
			String year = ipDocElm.getAttribute(NWCGConstants.YEAR_ATTR);
			
			// Adding the below logic for register/deregister. We need to set the RegisterInterestInROSS
			// flag appropriately. In Register/Deregister, we are sending the flag RegisterInterestInROSS = Y
			// and RegisterInterestInROSS = N respectively. I will be using this flags to update the DB.
			// Here, this flags are set in configurator and not from DB. So, we need to update it. In other
			// scenarios of active/inactive, it doesn't matter as we will be setting whatever value it is. Also,
			// I am making a null check + length check before passing this element.
			String regIntInROSS = ipDocElm.getAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR);
			
			Document xmlDoc = XMLUtil.createDocument("NWCGIncidentOrder");
			Element elm = xmlDoc.getDocumentElement();
			elm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incidentNumber);
			// BEGIN - PI 1674 (POST 9.1 Go-Live) - 05142015
			if(year == null || year.isEmpty()) {
				logger.verbose("@@@@@ NWCGLockIncident::lockIncident::year (Other Order) :: " + year);
				// if null or empty, then set it to a single space (" ")...
				elm.setAttribute(NWCGConstants.YEAR_ATTR, " ");
			} else {
				logger.verbose("@@@@@ NWCGLockIncident::lockIncident::year (Incident Order) :: " + year);
				elm.setAttribute(NWCGConstants.YEAR_ATTR, year);
			}
			// END - PI 1674 (POST 9.1 Go-Live) - 05142015
			elm.setAttribute(NWCGConstants.INCIDENT_LOCKED, NWCGConstants.YES);
			elm.setAttribute("LockReason", modReason);
			elm.setAttribute("ModificationDesc", modReason);
			elm.setAttribute("ModificationCode", "Sent to ROSS");
			if (regIntInROSS != null && (regIntInROSS.equalsIgnoreCase("Y") || regIntInROSS.equalsIgnoreCase("N"))) {
				elm.setAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR, regIntInROSS);
			}
			logger.verbose("@@@@@ NWCGLockIncident::lockIncident, Input xml to " + NWCGConstants.SVC_UPDT_INC_ORDER_SVC + " is " + XMLUtil.getXMLString(xmlDoc));
			CommonUtilities.invokeService(env, NWCGConstants.SVC_UPDT_INC_ORDER_SVC, xmlDoc);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! NWCGLockIncident::lockIncident, ParserConfigurationException while invoking :: " + NWCGConstants.SVC_UPDT_INC_ORDER_SVC);
			pce.printStackTrace();
		} catch (Exception e2) {
			logger.error("!!!!! NWCGLockIncident::lockIncident, Exception while invoking :: " + NWCGConstants.SVC_UPDT_INC_ORDER_SVC);
			e2.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGLockIncident::lockIncident");
		return ipDoc;
	}
	
	/**
	 * Setting the modification reason and locked reason for locking the incident
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document lockActiveInactiveIncident(YFSEnvironment env, Document ipDoc) throws Exception{
		logger.verbose("@@@@@ Entering NWCGLockIncident::lockActiveInactiveIncident");
		String isActive = ipDoc.getDocumentElement().getAttribute(NWCGConstants.IS_ACTIVE);
		String modReason = "";
		if (isActive.equalsIgnoreCase(NWCGConstants.YES)){
			modReason = "Made Active Incident Call";
		}
		else {
			modReason = "Made Inactive Incident Call";
		}
		logger.verbose("@@@@@ Exiting NWCGLockIncident::lockActiveInactiveIncident");
		return lockIncident(env, ipDoc, modReason);
	}

	/**
	 * Setting the modification reason and locked reason for locking the incident
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document lockRegisterDeregisterIncident(YFSEnvironment env, Document ipDoc) throws Exception{
		logger.verbose("@@@@@ Entering NWCGLockIncident::lockRegisterDeregisterIncident");
		String regIntInROSS = ipDoc.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR);		
		String modReason = "";
		if (regIntInROSS != null && regIntInROSS.equalsIgnoreCase(NWCGConstants.YES)){
			modReason = "Made Register Incident Call";
		}
		else if (regIntInROSS != null && regIntInROSS.equalsIgnoreCase(NWCGConstants.NO)){
			modReason = "Made Deregister Incident Call";
		}
		else {
			modReason = "RegisterInterestInROSS is not set. Unable to decipher the Reg/Dereg call for incident lock";
		}
		logger.verbose("@@@@@ Exiting NWCGLockIncident::lockRegisterDeregisterIncident");
		return lockIncident(env, ipDoc, modReason);
	}
}