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
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessIncidents implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGProcessIncidents.class);

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
  /*<NWCGIncidentOrder RegisterInterestInROSS="Y" IsActive="N" IncidentNo="AK-ACC-000001" Year="2011" IncidentKey="2011032108273938011584" NWCGUSERID="jbilliard" /> */

	

	public Document processIncidents(YFSEnvironment env, Document ipDoc) throws Exception {
		logger.verbose("NWCGGetIncidentDtls::getIncidentDtls, Entered");
		Element ipDocRootElm = ipDoc.getDocumentElement();
		NodeList IncList = ipDocRootElm.getElementsByTagName("NWCGIncidentOrder");
		
    	for(int i=0; i<IncList.getLength(); i++)
		{
			Element curIncListElm = (Element) IncList.item(i);
		    String IncNo = curIncListElm.getAttribute("IncidentNo");
		    String IncYr = curIncListElm.getAttribute("Year");
		    String RegisterinROSS = curIncListElm.getAttribute("RegisterInterestInROSS");
		    String ActiveFlag = curIncListElm.getAttribute("IsActive");
		    String IncKey = curIncListElm.getAttribute("IncidentKey");
		    String UserID = curIncListElm.getAttribute("NWCGUSERID");
		
		    Document ipGetIncDtlsDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		    Element ipGetIncDtlsElm = ipGetIncDtlsDoc.getDocumentElement();
		    ipGetIncDtlsElm.setAttribute("IncidentNo", IncNo);
		    ipGetIncDtlsElm.setAttribute("Year", IncYr);
		    ipGetIncDtlsElm.setAttribute("RegisterInterestInROSS", RegisterinROSS);
		    ipGetIncDtlsElm.setAttribute("IsActive", ActiveFlag);
		    ipGetIncDtlsElm.setAttribute("IncidentKey", IncKey);
		    ipGetIncDtlsElm.setAttribute("NWCGUSERID", UserID);
		    
		    Document opIncDtlsDoc = CommonUtilities.invokeService(env, 
									"NWCGPostInActiveIncidentService", ipGetIncDtlsDoc);
		    logger.verbose("NWCGGetIncidentDtls::getIncidentDtls, Returning");
		}
      return ipDoc;
	}

}
