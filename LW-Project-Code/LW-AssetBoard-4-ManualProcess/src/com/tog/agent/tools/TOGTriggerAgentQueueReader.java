package com.tog.agent.tools;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.tog.constants.TOGConstants;
import com.tog.util.TOGXMLUtil;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.interop.japi.YIFCustomApi; 
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.log.YFCLogLevel;

public class TOGTriggerAgentQueueReader implements YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory.instance("TOGApplicationLogger");

	private Properties properties;	
	
	public final void setProperties(Properties apiArgs) throws Exception {
		this.properties = apiArgs;
	}	
	
	/**
	 * This method will get a message from the trigger agent queue.  
	 * The message will determine what type of process the trigger agent will execute. 
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
	 *  - TriggerAgentType="Item_Substitution"
	 *  - TriggerAgentType="Order_Reconciliation"
	 *  - TriggerAgentType="Back_Order_Detail"
	 *  
	 * @param env
	 * @param ipDoc
	 * @throws Exception
	 */
	public Document triggerAgentForManualProcess(YFSEnvironment env, Document inDoc) throws Exception {
		if (YFCLogUtil.isVerboseEnabled()) {
			logger.setLogAll(true);
			logger.verbose("@@@@@ Entering com.tog.agent.tools.TOGTriggerAgentQueueReader::triggerAgentForManualProcess @@@@@");
			logger.verbose("@@@@@ inDoc :: " + TOGXMLUtil.getXmlString(inDoc));			
		}
		
		Element elemAgentTriggerType = (Element) inDoc.getDocumentElement().getElementsByTagName(TOGConstants.ELE_TRIGGERAGENTTYPE).item(0);
		String strTriggerAgentType = elemAgentTriggerType.getAttribute(TOGConstants.ATTRIB_TRIGGERAGENTTYPE);
		Element elemAgentTriggerScreen = (Element) inDoc.getDocumentElement().getElementsByTagName(TOGConstants.ELE_TRIGGERAGENTSCREEN).item(0);
		String strTriggerAgentScreen = elemAgentTriggerScreen.getAttribute(TOGConstants.ATTRIB_TRIGGERAGENTSCREEN);
		if (YFCLogUtil.isVerboseEnabled()) {
			logger.verbose("@@@@@ strTriggerAgentType :: " + strTriggerAgentType);
			logger.verbose("@@@@@ strTriggerAgentScreen :: " + strTriggerAgentScreen);
		}
		
		String strDirectoryPath = "";
		PrintWriter writer = null;
		if(strTriggerAgentScreen.equals("Manual_Processing")) {
			if(strTriggerAgentType.equals("Schedule_Agent")) {
				strDirectoryPath = YFSSystem.getProperty("tog.trigger.agent");
				String strTempFile = (strDirectoryPath + TOGConstants.OMS_SCHEDULE_NEW_AGENT_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("OMS_SCHEDULE_NEW_AGENT Trigger File");
				writer.close();
			} else if(strTriggerAgentType.equals("Schedule_Backorder_Agent")) {
				strDirectoryPath = YFSSystem.getProperty("tog.trigger.agent");
				String strTempFile = (strDirectoryPath + TOGConstants.OMS_SCHEDULE_BO_AGENT_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("OMS_SCHEDULE_BO_AGENT Trigger File");
				writer.close();				
			} else if(strTriggerAgentType.equals("Release_Agent")) {
				strDirectoryPath = YFSSystem.getProperty("tog.trigger.agent");
				String strTempFile = (strDirectoryPath + TOGConstants.OMS_RELEASE_AGENT_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("OMS_RELEASE_AGENT Trigger File");
				writer.close();				
			}
		} else if(strTriggerAgentScreen.equals("Reports")) {
			if(strTriggerAgentType.equals("Open_Orders")) {
				strDirectoryPath = YFSSystem.getProperty("tog.openorder");
				String strTempFile = (strDirectoryPath + TOGConstants.OPEN_ORDER_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("Open_Orders Trigger File");
				writer.close();
			} else if(strTriggerAgentType.equals("Open_Order_Detail")) {
				strDirectoryPath = YFSSystem.getProperty("tog.openorder.detail");
				String strTempFile = (strDirectoryPath + TOGConstants.OPEN_ORDER_DETAIL_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("Open_Order_Detail Trigger File");
				writer.close();
			} else if(strTriggerAgentType.equals("Open_Order_Detail_ATP")) {
				strDirectoryPath = YFSSystem.getProperty("tog.openorder.detail");
				String strTempFile = (strDirectoryPath + TOGConstants.OPEN_ORDER_DETAIL_ATP_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("Open_Order_Detail Trigger File");
				writer.close();			
			} else if(strTriggerAgentType.equals("Back_Order_Summary")) {
				strDirectoryPath = YFSSystem.getProperty("tog.backorder.summary");
				String strTempFile = (strDirectoryPath  + TOGConstants.BACK_ORDER_SUMMARY_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("Back_Order_Summary Trigger File");
				writer.close();
			} else if(strTriggerAgentType.equals("Item_Substitution")) {
				strDirectoryPath = YFSSystem.getProperty("tog.item.substitution");
				String strTempFile = (strDirectoryPath  + TOGConstants.ITEM_SUBSTITUTION_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("Item_Substitution Trigger File");
				writer.close();
			} else if(strTriggerAgentType.equals("Order_Reconciliation")) {
				strDirectoryPath = YFSSystem.getProperty("tog.order.reconciliation");
				String strTempFile = (strDirectoryPath  + TOGConstants.ORDER_RECONCILIATION_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("Order_Reconciliation Trigger File");
				writer.close();
			} else if(strTriggerAgentType.equals("Back_Order_Detail")) {
				strDirectoryPath = YFSSystem.getProperty("tog.backorder.detail");
				String strTempFile = (strDirectoryPath  + TOGConstants.BACK_ORDER_DETAIL_TEMP_FILE + ".txt");
				if (YFCLogUtil.isVerboseEnabled()) {
					logger.verbose("@@@@@ strTempFile :: " + strTempFile);
				}
				writer = new PrintWriter(strTempFile, "UTF-8");
				writer.println("Back_Order_Detail Trigger File");
				writer.close();
			} 
		}
		
		if (YFCLogUtil.isVerboseEnabled()) {
			logger.verbose("@@@@@ Exiting com.tog.agent.tools.TOGTriggerAgentQueueReader::triggerAgentForManualProcess @@@@@");
		}
		return inDoc;
	}	
}