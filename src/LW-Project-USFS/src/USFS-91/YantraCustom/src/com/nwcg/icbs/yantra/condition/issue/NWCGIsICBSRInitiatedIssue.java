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

package com.nwcg.icbs.yantra.condition.issue;

import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Class invoked by SDF service to determine whether
 * a given order is ROSS or ICBS initiated
 */
public class NWCGIsICBSRInitiatedIssue implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGIsICBSRInitiatedIssue.class);
	
	private Map myProperties = null;
	/**
	 * This will return true if the order is ICBSR initiated issue, else
	 * it will return true. In order to determine this, it will check
	 * Order/Extn/@ExtnSystemOfOrigin
	 */
	public boolean evaluateCondition(YFSEnvironment arg0, String arg1,
			Map arg2, Document arg3) {
		logger.verbose("NWCGIsICBSRInitiatedIssue::evaluateCondition, Entered");
		logger.verbose("NWCGIsICBSRInitiatedIssue.evaluateCondition");
		try {
			logger.verbose("NWCGIsICBSRInitiatedIssue::evaluateCondition, Input XML : " + XMLUtil.extractStringFromDocument(arg3));
		} catch (TransformerException e) {
			//logger.printStackTrace(e);
		}
		
		NodeList nlOrderExtn = arg3.getDocumentElement().getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
		// There will be only one root element as the input is a template controlled XML
		Element elmExtn = (Element) nlOrderExtn.item(0);
		String sysOfOrigin = elmExtn.getAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN);
		logger.verbose("NWCGIsICBSRInitiatedIssue.evaluateCondition");
		logger.verbose("NWCGIsICBSRInitiatedIssue::evaluateCondition, Exited");
		if ((sysOfOrigin != null) && sysOfOrigin.equalsIgnoreCase("ICBSR")){
			return true;
		}
		else {
			return false;
		}		
	}

	public void setProperties(Map arg0) { 
		this.myProperties = arg0;
	}
}