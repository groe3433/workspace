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

package com.nwcg.icbs.yantra.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGMessageStore;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGAgentLogger;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * @author sdas
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGReadNoResultMessageAgent extends YCPBaseAgent {
	private static YFCLogCategory logger = NWCGAgentLogger
			.instance(NWCGReadNoResultMessageAgent.class);
	private static YIFApi api;

	static {
		try {
			api = YIFClientFactory.getInstance().getLocalApi();
		} catch (YIFClientCreationException e) {
			logger.error("!!!!! Caught YIFClientCreationException, while instantiating YIFApi :: " + e);
		}
	}

	/**
	 * public default constructor
	 */
	public NWCGReadNoResultMessageAgent() {
		super();
		logger.verbose("NWCGReadNoResultMessageAgent agent started!!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#getJobs(com.yantra.yfs.japi.
	 * YFSEnvironment, org.w3c.dom.Document)
	 */
	public List getJobs(YFSEnvironment env, Document arg1, Document lastMessage) throws Exception {
		logger.verbose("inside getjobs");
		logger.verbose("input to getJobs (arg1)::" + XMLUtil.getXMLString(arg1));
		logger.verbose("lastMessage::" + XMLUtil.getXMLString(lastMessage));
		List aList = new ArrayList();
		String msgStatus = NWCGAAConstants.MESSAGE_STATUS_AUTHORIZED;
		Document inDocGetNWCGOBMsgList = XMLUtil.createDocument("NWCGOutboundMessage");
		inDocGetNWCGOBMsgList.getDocumentElement().setAttribute("MessageStatus", msgStatus);
		Document opDocGetNWCGOBMsgList = api.executeFlow(env, NWCGAAConstants.SERVICE_NAME, inDocGetNWCGOBMsgList);
		logger.verbose("opDocGetNWCGOBMsgList ::" + XMLUtil.getXMLString(opDocGetNWCGOBMsgList));
		aList.add(opDocGetNWCGOBMsgList);
		return aList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yantra.ycp.japi.util.YCPBaseAgent#executeJob(com.yantra.yfs.japi.
	 * YFSEnvironment, org.w3c.dom.Document)
	 */
	public void executeJob(YFSEnvironment env, Document opDocGetNWCGOBMsgList)
			throws Exception {

		if (logger.isVerboseEnabled())
			logger.verbose("inside execute jobs");
		if (logger.isVerboseEnabled())
			logger.verbose("opDocGetNWCGOBMsgList 1::"
					+ XMLUtil.getXMLString(opDocGetNWCGOBMsgList));

		if (opDocGetNWCGOBMsgList != null) {
			NodeList opDocGetNWCGOBMsgListNL = opDocGetNWCGOBMsgList
					.getElementsByTagName("NWCGOutboundMessage");

			Map map = new HashMap();
			for (int count = 0; count < opDocGetNWCGOBMsgListNL.getLength(); count++) {
				Element elem = (Element) opDocGetNWCGOBMsgListNL.item(count);
				map.put("DistributionID".concat("[")
						.concat(Integer.toString(count)).concat("]"),
						elem.getAttribute("DistributionID"));
			}
			String queueName = NWCGAAConstants.QUEUEID_NO_RESPONSE;
			if (opDocGetNWCGOBMsgListNL.getLength() != 0) {
				if (logger.isVerboseEnabled())
					logger.verbose("about to raise alert!!");
				CommonUtilities.raiseAlert(env, queueName,
						NWCGAAConstants.ALERT_MESSAGE_7, opDocGetNWCGOBMsgList,
						null, map);

				NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
				for (int c = 0; c < opDocGetNWCGOBMsgListNL.getLength(); c++) {
					Element elem = (Element) opDocGetNWCGOBMsgListNL.item(c);
					String distID = elem.getAttribute("DistributionID");
					String msgKey = elem.getAttribute("MessageKey");
					if (logger.isVerboseEnabled())
						logger.verbose("distID:" + distID);
					if (logger.isVerboseEnabled())
						logger.verbose("msgKey:" + msgKey);
					try {
						msgStore.updateMessage(
								env,
								distID,
								"OB",
								"",
								"",
								NWCGAAConstants.MESSAGE_STATUS_AWAITING_RESPONSE,
								"", msgKey, true, "");
					} catch (Exception e) {
						logger.error("!!!!! Caught General Exception, thrown during message update :: " + e);
					}
				}
			}
		}
	}

}
