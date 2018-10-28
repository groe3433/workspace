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

package com.nwcg.icbs.yantra.api.tiggeragent;

import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.tools.util.ArgumentParser;
import com.yantra.ycp.agent.server.YCPAgentTrigger;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

public class NWCGTriggerAgent implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGTriggerAgent.class);

	public void setProperties(Properties arg0) throws Exception {

	}

	public Document triggerCycleCountAgent(YFSEnvironment env, Document inDoc)
			throws Exception {
		String strCache = System.getProperty("yfs.enableLocalCache");
		System.setProperty("yfs.enableLocalCache", "N");

		String[] arg = new String[2];

		arg[0] = "-criteria";

		if (NWCGConstants.TRIGGER_AGENT_CRITERIA == null
				|| NWCGConstants.TRIGGER_AGENT_CRITERIA.equals("")
				|| NWCGConstants.TRIGGER_AGENT_CRITERIA.equals("null")) {
			arg[1] = "CREATE_COUNT_TASKS";
		} else {
			arg[1] = NWCGConstants.TRIGGER_AGENT_CRITERIA;
		}

		ArgumentParser parser = new ArgumentParser(arg);
		Map mOptions = parser.getOptions();
		YCPAgentTrigger alarm = new YCPAgentTrigger(mOptions);
		alarm.sendMessage();
		try {
			if (strCache == null)
				strCache = "";
			System.setProperty("yfs.enableLocalCache", strCache);
		} catch (Exception e) {
			// doesnt matter if we cant set this
		}

		return inDoc;
	}
}
