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

package com.nwcg.icbs.yantra.ue;

import java.util.Map;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.api.trackableinventory.NWCGProcessAdjustLocationInventory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetOrderNoUE;

/**
 * @author gacharya
 * 
 */
public class NWCGGenerateIncidentIssueNo implements YFSGetOrderNoUE {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGenerateIncidentIssueNo.class);
	
	public NWCGGenerateIncidentIssueNo() {
		super();
	}

	public String getOrderNo(YFSEnvironment env, Map map)
			throws YFSUserExitException {
		// Get the ServiceInstanceID from the YFS_SEQ_SERVICE_ID_KEY sequence
		long seqNo = ((YCPContext) env)
				.getNextDBSeqNo(NWCGConstants.SEQ_NWCG_INCIDENT_ISSUENO);
		String seqNoStr = Long.toString(seqNo);
		String orderNo = StringUtil.prepadStringWithZeros(seqNoStr,
				NWCGConstants.MAX_DIGITS_SEQ_NWCG_INCIDENT_ISSUENO);
		return orderNo;
	}

	public static void main(String args[]) {
		long seqNo = 1l;
		String seqNoStr = Long.toString(seqNo);
		String orderNo = StringUtil.prepadStringWithZeros(seqNoStr,
				NWCGConstants.MAX_DIGITS_SEQ_NWCG_INCIDENT_ISSUENO);
		if (logger.isVerboseEnabled())
			logger.verbose("OrderNo:" + orderNo);

	}
}