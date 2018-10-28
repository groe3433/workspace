package com.nwcg.icbs.yantra.ue;

import java.util.Map;

import com.nwcg.icbs.yantra.api.trackableinventory.NWCGProcessAdjustLocationInventory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;
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

	public NWCGGenerateIncidentIssueNo() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.yantra.yfs.japi.ue.YFSGetOrderNoUE#getOrderNo(com.yantra.yfs.japi.YFSEnvironment, java.util.Map)
	 */
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
		if(log.isVerboseEnabled()) log.verbose("OrderNo:" + orderNo);

	}
	private static Logger log = Logger.getLogger(NWCGProcessAdjustLocationInventory.class.getName());
}
