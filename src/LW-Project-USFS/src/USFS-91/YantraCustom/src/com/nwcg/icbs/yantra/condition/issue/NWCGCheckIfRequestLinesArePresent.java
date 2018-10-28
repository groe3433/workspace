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
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class will be called as part of a condition before sending CreateRequest
 * message to ROSS
 * 
 * @author sgunda
 * 
 */
public class NWCGCheckIfRequestLinesArePresent implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCheckIfRequestLinesArePresent.class);

	private Map myProperties = null;

	public boolean evaluateCondition(YFSEnvironment arg0, String arg1,
			Map arg2, Document doc) {
		logger.verbose("NWCGCheckIfRequestLinesArePresent::evaluateCondition, Entered");
		try {
			logger.verbose("NWCGCheckIfRequestLinesArePresent::evaluateCondition, Input XML : "
					+ XMLUtil.extractStringFromDocument(doc));
		} catch (TransformerException e) {
			// logger.printStackTrace(e);
		}

		NodeList nlReq = doc.getDocumentElement().getElementsByTagName(
				"ro:Request");
		if (nlReq != null && nlReq.getLength() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		myProperties = arg0;
	}

}
