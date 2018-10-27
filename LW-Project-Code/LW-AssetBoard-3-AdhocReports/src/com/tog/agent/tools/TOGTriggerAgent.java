package com.tog.agent.tools;

import java.io.PrintWriter;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.tog.constants.TOGConstants;
import com.tog.util.TOGXMLUtil;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.log.YFCLogLevel;

public class TOGTriggerAgent implements YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory.instance("TOGApplicationLogger");

	private Properties properties;

	public final void setProperties(Properties apiArgs) throws Exception {
		properties = apiArgs;
	}

	/**
	 * This method will put a message into the trigger agent queue.  
	 * The message will be read by an agent to determine what type of agent to trigger. 
	 * 
	 * Input xml sample: 
	 * <TriggerAgent>
	 * <TriggerAgentType TriggerAgentType="xml:/TriggerAgent/TriggerAgentType" />
	 * <TriggerAgentScreen TriggerAgentScreen="xml:/TriggerAgent/TriggerAgentScreen" />
	 * </TriggerAgent>
	 * 
	 * Possible Values (set 1):
	 *  - TriggerAgentScreen=Manual_Processing
	 *  - TriggerAgentType="Schedule_Agent"
	 *  - TriggerAgentType="Schedule_Backorder_Agent"
	 *  - TriggerAgentType="Release_Agent"
	 *  
	 * Possible Values (set 2):
	 *  - TriggerAgentScreen=Reports
	 *  - TriggerAgentType="Open_Orders"
	 *  - TriggerAgentType="Open_Order_Detail"
	 *  - TriggerAgentType="Open_Order_Detail_ATP"
	 *  - TriggerAgentType="Back_Order_Summary"
	 *  - TriggerAgentType="Back_Order_Detail"
	 *  - TriggerAgentType="Order_Reconciliation"
	 * 
	 * @param env
	 * @param ipDoc
	 * @throws Exception
	 */
	public Document triggerForManualProcess(YFSEnvironment env, Document ipDoc) throws Exception {
		if (YFCLogUtil.isVerboseEnabled()) {
			logger.setLogAll(true);
			logger.verbose("@@@@@ Entering com.tog.agent.tools.TOGTriggerAgent::triggerForManualProcess @@@@@");
			logger.verbose("@@@@@ ipDoc :: " + TOGXMLUtil.getXmlString(ipDoc));
		}
		
		Element elemAgentTriggerType = (Element) ipDoc.getDocumentElement().getElementsByTagName(TOGConstants.ELE_TRIGGERAGENTTYPE).item(0);
		String strTriggerAgentType = elemAgentTriggerType.getAttribute(TOGConstants.ATTRIB_TRIGGERAGENTTYPE);
		Element elemAgentTriggerScreen = (Element) ipDoc.getDocumentElement().getElementsByTagName(TOGConstants.ELE_TRIGGERAGENTSCREEN).item(0);
		String strTriggerAgentScreen = elemAgentTriggerScreen.getAttribute(TOGConstants.ATTRIB_TRIGGERAGENTSCREEN);
		if (YFCLogUtil.isVerboseEnabled()) {
			logger.verbose("@@@@@ strTriggerAgentType :: " + strTriggerAgentType);
			logger.verbose("@@@@@ strTriggerAgentScreen :: " + strTriggerAgentScreen);
		}
		
		if (YFCLogUtil.isVerboseEnabled()) {
			logger.verbose("@@@@@ Exiting com.tog.agent.tools.TOGTriggerAgent::triggerForManualProcess @@@@@");
		}
		return ipDoc;
	}
}