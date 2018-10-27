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

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGSendICBSRInitiatedRequestToROSS implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGSendICBSRInitiatedRequestToROSS.class);
	
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
	public Document sendICBSRInitiatedReqToROSS(YFSEnvironment env, Document ipDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGSendICBSRInitiatedRequestToROSS::sendICBSRInitiatedReqToROSS @@@@@");
		NodeList nlOrderExtn = ipDoc.getDocumentElement().getElementsByTagName("Extn");
		// There will be only one root element as the input is a template controlled XML
		Element elmExtn = (Element) nlOrderExtn.item(0);
		String sysOfOrigin = elmExtn.getAttribute("ExtnSystemOfOrigin");
		if (sysOfOrigin != null && sysOfOrigin.equalsIgnoreCase("ICBSR")) {
			// This service will call the XSL to transform to ROSS format and will call NWCGPostOBMsgService Make a synchronous webservice call by calling ROSS
			logger.verbose("@@@@@ ipDoc :: " + XMLUtil.getXMLString(ipDoc));
			CommonUtilities.invokeService(env, "NWCGSendICBSRInitiatedRequestToROSSService", ipDoc);
		} else {
			// Do Nothing...
		}	
		logger.verbose("@@@@@ Exiting NWCGSendICBSRInitiatedRequestToROSS::sendICBSRInitiatedReqToROSS @@@@@");
		return ipDoc;
	}
}