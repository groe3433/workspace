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

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetNextSubstRequestNoForIssueLine implements YIFCustomApi {
	
	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGGetNextSubstRequestNoForIssueLine.class.getName());
	
	String incidentNo=null,incidentYear=null;
	
	private Properties sdfApiArgs = null;
	public void setProperties(Properties arg0) throws Exception {
		sdfApiArgs = arg0;	
		logger.verbose("NWCGGetNextSubstRequestNoForIssueLine properties: "+sdfApiArgs.toString());
	}
	
	//Invoked by the SDF service configured for the ajaxCommand getNextSubstRequestNoForOrderLine
	public Document getNextSubstRequestNoForOrderLine (YFSEnvironment env, Document inDoc) throws Exception {
 		Element docElm = inDoc.getDocumentElement();

		incidentNo = docElm.getAttribute(NWCGConstants.INCIDENT_NO);
		incidentYear = docElm.getAttribute(NWCGConstants.INCIDENT_YEAR);
		String numRequests = docElm.getAttribute("NumberOfRequests");
		String extnRequestNo = docElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		String newRequestNo = NWCGConstants.EMPTY_STRING;
		
		Document retVal = XMLUtil.createDocument("RequestNumbers");
		Element retValDocElm = retVal.getDocumentElement();
		Element elm = null;
		
		int total = Integer.parseInt(numRequests);
		for (int i = 0; i < total; i++) {
			if (extnRequestNo != null) {
				//Find index of dots ".", if any
				int lastDotIndex = extnRequestNo.lastIndexOf('.');
				if (lastDotIndex != -1 && i != 0) {
					String numAfterLastDotStr = extnRequestNo.substring(lastDotIndex+1);
					int numAfterLastDot = Integer.parseInt(numAfterLastDotStr);
					numAfterLastDot += 1;						
					newRequestNo = extnRequestNo.substring(0, lastDotIndex);
					newRequestNo += "."+numAfterLastDot;
				}else {
					newRequestNo = extnRequestNo + ".1";
				}
			}
			else { return null; }
			
			elm = retVal.createElement("RequestLine");
			elm.setAttribute("RequestNo", newRequestNo);
			
			elm.setAttribute("Index", Integer.toString(i+1));
			retValDocElm.appendChild(elm);
			extnRequestNo = newRequestNo;
		}//for
		return retVal;
	}
}