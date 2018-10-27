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

public class NWCGGetIncidentDtls implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetIncidentDtls.class);

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
	 * This class is created to get the incident details. This makes a service call to
	 * get the incident details.
	 * This is called from NWCGSaveIncidentOrderService. We can call a composite service
	 * of NWCGGetIncidentOrderService with in NWCGSaveIncidentOrderService. If we do that,
	 * we will not receive the output of composite service in NWCGSaveIncidentOrderService.
	 * So, making a call from this class and returning the XML.
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document getIncidentDtls(YFSEnvironment env, Document ipDoc) throws Exception {
		logger.verbose("NWCGGetIncidentDtls::getIncidentDtls, Entered");
		Element ipDocRootElm = ipDoc.getDocumentElement();
		String incNo = ipDocRootElm.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
		String incYr = ipDocRootElm.getAttribute(NWCGConstants.YEAR_ATTR);
		
		Document ipGetIncDtlsDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		Element ipGetIncDtlsElm = ipGetIncDtlsDoc.getDocumentElement();
		ipGetIncDtlsElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
		ipGetIncDtlsElm.setAttribute(NWCGConstants.YEAR_ATTR, incYr);
		
		Document opIncDtlsDoc = CommonUtilities.invokeService(env, 
									NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, ipGetIncDtlsDoc);
		logger.verbose("NWCGGetIncidentDtls::getIncidentDtls, Returning");
		return opIncDtlsDoc;
	}

}
