/*
 * Created on Mar 6, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.nwcg.icbs.yantra.agents;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author sdas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGMessageStorePurge extends YCPBaseAgent{
	
	private static Logger logger = Logger.getLogger();

	private static YIFApi api;
    
    static {
        try {
            api = YIFClientFactory.getInstance().getLocalApi();
        } catch (YIFClientCreationException e) {
        	if (logger.isVerboseEnabled())logger.verbose("Error while instantiating YIFApi .. ");
        }
    }
    
	/**
	 * public default constructor
	 */
	public NWCGMessageStorePurge() {
		super();
		if (logger.isVerboseEnabled())logger.verbose("NWCGMessageStorePurge agent started!!");
	}
	
	/* (non-Javadoc)
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#getJobs(com.yantra.yfs.japi.YFSEnvironment, org.w3c.dom.Document)
	 */
	public List getJobs(YFSEnvironment env, Document arg1,Document lastMessage) throws Exception {
		
		if (logger.isVerboseEnabled())logger.verbose("inside getjobs");
		if (logger.isVerboseEnabled())logger.verbose("input to getJobs (arg1)::"+XMLUtil.getXMLString(arg1));
		if (logger.isVerboseEnabled())logger.verbose("lastMessage::"+XMLUtil.getXMLString(lastMessage));
		
		List aList = new ArrayList();
		
	        String msgStatus = NWCGAAConstants.MESSAGE_STATUS_PROCESSED;
	        
	        Document inDocGetNWCGOBMsgList = XMLUtil.createDocument("NWCGOutboundMessage");
			inDocGetNWCGOBMsgList.getDocumentElement().setAttribute("MessageStatus",msgStatus);
			
			Document inDocGetNWCGIBMsgList = XMLUtil.createDocument("NWCGInboundMessage");
			inDocGetNWCGIBMsgList.getDocumentElement().setAttribute("MessageStatus",msgStatus);
			
			Document opDocGetNWCGOBMsgList = api.executeFlow(env,NWCGAAConstants.GET_OB_MESSAGE_LIST_SERVICE,inDocGetNWCGOBMsgList);
			if (logger.isVerboseEnabled())logger.verbose("opDocGetNWCGOBMsgList ::"+XMLUtil.getXMLString(opDocGetNWCGOBMsgList));
			
			Document opDocGetNWCGIBMsgList = api.executeFlow(env,NWCGAAConstants.GET_IB_MESSAGE_LIST_SERVICE,inDocGetNWCGIBMsgList);
			if (logger.isVerboseEnabled())logger.verbose("opDocGetNWCGIBMsgList ::"+XMLUtil.getXMLString(opDocGetNWCGIBMsgList));
			
			aList.add(opDocGetNWCGOBMsgList);
			aList.add(opDocGetNWCGIBMsgList);
			
			return aList;
		
	}
	
	/* (non-Javadoc)
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#executeJob(com.yantra.yfs.japi.YFSEnvironment, org.w3c.dom.Document)
	 */
	public void executeJob(YFSEnvironment env, Document inDoc) throws Exception {
		
		if (logger.isVerboseEnabled())logger.verbose("inside execute jobs");
		if (logger.isVerboseEnabled())logger.verbose("inDoc ::"+XMLUtil.getXMLString(inDoc));
		
		if(inDoc != null){
			NodeList nlOutboundMessage = inDoc.getElementsByTagName("NWCGOutboundMessage");
			NodeList nlInboundMessage = inDoc.getElementsByTagName("NWCGInboundMessage");
						
			
			if(nlOutboundMessage.getLength()!=0){
				
					for(int x=0; x < nlOutboundMessage.getLength(); x++){
												
						Element elem = (Element)nlOutboundMessage.item(x);
						if (logger.isVerboseEnabled())logger.verbose("Elem " + x + " is: " + XMLUtil.getElementXMLString(elem));
						if (logger.isVerboseEnabled())logger.verbose("#####################################################");
						
						String msgKey = elem.getAttribute("MessageKey");
						String strModifyTS = elem.getAttribute("Modifyts");
						String strModifyTS1 = strModifyTS.substring(0 , 10);
						String strModifyTSHour = strModifyTS.substring(11, 19);
															
						GregorianCalendar now = new GregorianCalendar();
						long longThreeWeeks = 1814400000;
											    
					    Timestamp ts = Timestamp.valueOf(strModifyTS1 + " " + strModifyTSHour);
					    long tsTime = ts.getTime();
					    					    
					    if (logger.isVerboseEnabled())logger.verbose("Current time: " + (now.getTimeInMillis()));
											
						
						if ((tsTime+longThreeWeeks) <  now.getTimeInMillis()){
							if (logger.isVerboseEnabled())logger.verbose("More than 3 weeks have elapsed, deleting record...");
							
							Document docOutboundDelete = XMLUtil.createDocument("NWCGOutboundMessage");
							docOutboundDelete.getDocumentElement().setAttribute("MessageKey", msgKey);
							
							CommonUtilities.invokeService(env,NWCGAAConstants.DELETE_OB_MESSAGE_SERVICE,docOutboundDelete);
							
						}
											
						//2008-04-15T12:10:43-04:00
						
						if (logger.isVerboseEnabled())logger.verbose("msgKey:"+msgKey);
						if (logger.isVerboseEnabled())logger.verbose("strModifyTS:"+strModifyTS);
						
												
					}
				}
			if(nlInboundMessage.getLength()!=0){
				//	CommonUtilities.raiseAlert(env,queueName,NWCGAAConstants.ALERT_MESSAGE_7,inDoc,null,map);
					
					for(int x=0; x < nlInboundMessage.getLength(); x++){
												
						Element elem = (Element)nlInboundMessage.item(x);
						if (logger.isVerboseEnabled())logger.verbose("Elem " + x + " is: " + XMLUtil.getElementXMLString(elem));
						if (logger.isVerboseEnabled())logger.verbose("#####################################################");
						
						String msgKey = elem.getAttribute("MessageKey");
						String strModifyTS = elem.getAttribute("Modifyts");
						String strModifyTS1 = strModifyTS.substring(0 , 10);
						String strModifyTSHour = strModifyTS.substring(11, 19);
															
						GregorianCalendar now = new GregorianCalendar();
						long longThreeWeeks = 1814400000;
											    
					    Timestamp ts = Timestamp.valueOf(strModifyTS1 + " " + strModifyTSHour);
					    long tsTime = ts.getTime();
					    				
						
						if ((tsTime+longThreeWeeks) <  now.getTimeInMillis()){
							if (logger.isVerboseEnabled())logger.verbose("More than 3 weeks have elapsed, deleting record...");
							
							Document docInboundDelete = XMLUtil.createDocument("NWCGInboundMessage");
							docInboundDelete.getDocumentElement().setAttribute("MessageKey", msgKey);
							
							CommonUtilities.invokeService(env,NWCGAAConstants.DELETE_IB_MESSAGE_SERVICE,docInboundDelete);
							
						}
											
						
						
						if (logger.isVerboseEnabled())logger.verbose("msgKey:"+msgKey);
						if (logger.isVerboseEnabled())logger.verbose("strModifyTS:"+strModifyTS);
						
						
						
						
						
					}
				}
			/*
			if(nlInboundMessage.getLength()!=0){
					
					for(int x=0; x < nlInboundMessage.getLength(); x++){
						
						Element elem = (Element)nlInboundMessage.item(x);
						
						System.out.println("Elem " + x + " is: " + XMLUtil.getElementXMLString(elem));
						System.out.println("#####################################################");
						
						
						String msgKey = elem.getAttribute("MessageKey");
						String strModifyTS = elem.getAttribute("Modifyts");
						System.out.println("msgKey:"+msgKey);
						System.out.println("strModifyTS:"+strModifyTS);
						
						
						
						Document docInboundDelete = XMLUtil.createDocument("NWCGInboundMessage");
						docInboundDelete.getDocumentElement().setAttribute("MessageKey", msgKey);
						
						//CommonUtilities.invokeService(env,NWCGAAConstants.DELETE_IB_MESSAGE_SERVICE,docInboundDelete);
						
					}
				}
			*/
		}
	}
	
	
	
}
