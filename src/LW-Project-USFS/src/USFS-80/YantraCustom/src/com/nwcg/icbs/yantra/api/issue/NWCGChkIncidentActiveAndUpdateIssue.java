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

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGChkIncidentActiveAndUpdateIssue implements YIFCustomApi {

	private Properties myProperties = null;
	
	private YFSEnvironment myEnvironment = null;
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}

	/**
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document chkIncidentActiveAndUpdtIssue(YFSEnvironment env, Document ipDoc) throws Exception {
    	System.out.println("@@@@@ Entering NWCGChkIncidentActiveAndUpdateIssue::setPublishStatusAndRemoveUnpubItems @@@@@");
		System.out.println("@@@@@ ipDoc : " + XMLUtil.getXMLString(ipDoc));
		
		myEnvironment = env;
		Element elmIPDoc = ipDoc.getDocumentElement();
		String incKey = elmIPDoc.getAttribute("IncidentKey");
		String orderHdrKey = elmIPDoc.getAttribute("OrderHeaderKey");
		
		//Determine whether it's active or not in ICBSR
		boolean isIncidentActiveICBSR = isIncidentActive(incKey);
	
		//If it is active then remove the hold type
		if (isIncidentActiveICBSR) {
			removeIncidentInactiveHold(orderHdrKey);
		}		
		
    	System.out.println("@@@@@ Exiting NWCGChkIncidentActiveAndUpdateIssue::setPublishStatusAndRemoveUnpubItems @@@@@");
		return ipDoc;
	}
	
	/**
	 * 
	 * @param incidentKey
	 * @return
	 */
	private boolean isIncidentActive (String incidentKey) {
    	System.out.println("@@@@@ Entering NWCGChkIncidentActiveAndUpdateIssue::isIncidentActive @@@@@");
		
		String getIncidentOrderInput = "<NWCGIncidentOrder IncidentKey=\""+incidentKey+"\"/>";
		Document apiOutputDoc = null;
		boolean isIncidentActive = false;
		try {
			apiOutputDoc = CommonUtilities.invokeService(myEnvironment, "NWCGGetIncidentOrderOnlyService", getIncidentOrderInput);
		}
		catch (Exception e) {
			return isIncidentActive;
		}		
		
		//We get a null pointer back when the incident is not found.
		if (apiOutputDoc == null) {
			return isIncidentActive;		
		}	
		
		Element docElm = apiOutputDoc.getDocumentElement();
		String isActive = docElm.getAttribute("IsActive");
		if (!StringUtil.isEmpty(isActive) &&
			isActive.equalsIgnoreCase(NWCGConstants.YES)) {
			isIncidentActive = true;
		}		
		
    	System.out.println("@@@@@ Exiting NWCGChkIncidentActiveAndUpdateIssue::isIncidentActive @@@@@");
		return isIncidentActive;
	}
	
	/**
	 * This method will build the order input to resolve INCIDENT_INACTIVE
	 * hold.
	 * @param env
	 * @param orderHdrKey
	 * @throws Exception
	 */
	private void removeIncidentInactiveHold(String orderHdrKey) throws Exception{
    	System.out.println("@@@@@ Entering NWCGChkIncidentActiveAndUpdateIssue::removeIncidentInactiveHold @@@@@");
		try {
			Document docOrder = XMLUtil.createDocument("Order");
			Element elmDoc = docOrder.getDocumentElement();
			elmDoc.setAttribute("OrderHeaderKey", orderHdrKey);
			elmDoc.setAttribute("Override", NWCGConstants.YES);
			
			Element elmOrderHoldTypes = docOrder.createElement("OrderHoldTypes");
			elmDoc.appendChild(elmOrderHoldTypes);
			Element elmOrderHoldType = docOrder.createElement("OrderHoldType");
			elmOrderHoldTypes.appendChild(elmOrderHoldType);
			elmOrderHoldType.setAttribute("HoldType", "INCIDENT_INACTIVE");
			elmOrderHoldType.setAttribute("ReasonText", "Incident is active in ICBSR");
			elmOrderHoldType.setAttribute("ResolveUserId", myEnvironment.getUserId());
			elmOrderHoldType.setAttribute("Status", "1300");
			
			CommonUtilities.invokeAPI(myEnvironment, "NWCGGetOrderDetail_getOrderDetail", "changeOrder", docOrder);
		}
		catch(ParserConfigurationException pce){
			System.out.println("!!!!! ParserConfigurationException");
			pce.getStackTrace();
		}
		catch(Exception e){
			System.out.println("!!!!! Exception");
			e.getStackTrace();
			throw e;
		}
    	System.out.println("@@@@@ Exiting NWCGChkIncidentActiveAndUpdateIssue::removeIncidentInactiveHold @@@@@");
	}
}
