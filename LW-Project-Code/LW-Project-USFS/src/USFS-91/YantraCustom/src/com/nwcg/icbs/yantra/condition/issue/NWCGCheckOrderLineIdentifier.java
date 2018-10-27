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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckOrderLineIdentifier implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCheckOrderLineIdentifier.class);

	private Map props = new HashMap();

	public NWCGCheckOrderLineIdentifier() {
		super();
	}

	/*
	 * <OrderLine OrderNo=""> <Extn EntnRequestNo=""/> </OrderLine>
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yantra.ycp.japi.YCPDynamicConditionEx#evaluateCondition(com.yantra
	 * .yfs.japi.YFSEnvironment, java.lang.String, java.util.Map,
	 * org.w3c.dom.Document)
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map dataMap, Document inDoc) {
		boolean rFlag = false;
		try {
			// do a get orderline
			Element inDocElm = inDoc.getDocumentElement();
			String identiFierPath = (String) props
					.get(NWCGConstants.KEY_ORDER_LINE_IDENTIFIER_XPATH);
			String identifier = XPathUtil.getString(inDocElm, identiFierPath);

			String incidentNoPath = (String) props
					.get(NWCGConstants.KEY_INCIDENT_NO_XPATH);
			String incidentNo = XPathUtil.getString(inDocElm, incidentNoPath);

			String incidentYrPath = (String) props
					.get(NWCGConstants.KEY_INCIDENT_YEAR_XPATH);
			String incidentYr = XPathUtil.getString(inDocElm, incidentYrPath);

			if (StringUtil.isEmpty(identifier)
					|| StringUtil.isEmpty(incidentNo)) {
				return rFlag;
			}
			// check if the orderline exists
			rFlag = CommonUtilities.checkOrderLineExistsForRequestNo(env,
					identifier, incidentNo, incidentYr);

		} catch (Exception e) {
			// logger.printStackTrace(e);
		}

		return rFlag;
	}

	public void setProperties(Map props) {
		this.props = props;
	}

}
