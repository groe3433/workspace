/*
 * Created on Mar 6, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
public class NWCGReadNoResultMessageAgent extends YCPBaseAgent{
	private static Logger logger = Logger.getLogger(NWCGReadNoResultMessageAgent.class.getName());
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
	public NWCGReadNoResultMessageAgent() {
		super();
		if (logger.isVerboseEnabled())logger.verbose("NWCGReadNoResultMessageAgent agent started!!");
	}
	
	/* (non-Javadoc)
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#getJobs(com.yantra.yfs.japi.YFSEnvironment, org.w3c.dom.Document)
	 */
	public List getJobs(YFSEnvironment env, Document arg1,Document lastMessage) throws Exception {
		
		if (logger.isVerboseEnabled())logger.verbose("inside getjobs");
		if (logger.isVerboseEnabled())logger.verbose("input to getJobs (arg1)::"+XMLUtil.getXMLString(arg1));
		if (logger.isVerboseEnabled())logger.verbose("lastMessage::"+XMLUtil.getXMLString(lastMessage));
		
		List aList = new ArrayList();
		
	        String msgStatus = NWCGAAConstants.MESSAGE_STATUS_AUTHORIZED;
	        
			Document inDocGetNWCGOBMsgList = XMLUtil.createDocument("NWCGOutboundMessage");
			inDocGetNWCGOBMsgList.getDocumentElement().setAttribute("MessageStatus",msgStatus);
			
			Document opDocGetNWCGOBMsgList = api.executeFlow(env,NWCGAAConstants.SERVICE_NAME,inDocGetNWCGOBMsgList);
			if (logger.isVerboseEnabled())logger.verbose("opDocGetNWCGOBMsgList ::"+XMLUtil.getXMLString(opDocGetNWCGOBMsgList));
			
			aList.add(opDocGetNWCGOBMsgList);
			
			return aList;
		
	}
	
	/* (non-Javadoc)
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#executeJob(com.yantra.yfs.japi.YFSEnvironment, org.w3c.dom.Document)
	 */
	public void executeJob(YFSEnvironment env, Document opDocGetNWCGOBMsgList) throws Exception {
		
		if (logger.isVerboseEnabled())logger.verbose("inside execute jobs");
		if (logger.isVerboseEnabled())logger.verbose("opDocGetNWCGOBMsgList 1::"+XMLUtil.getXMLString(opDocGetNWCGOBMsgList));
		
		if(opDocGetNWCGOBMsgList != null){
			NodeList opDocGetNWCGOBMsgListNL = opDocGetNWCGOBMsgList.getElementsByTagName("NWCGOutboundMessage");
			
			Map map = new HashMap();
			for(int count=0;count<opDocGetNWCGOBMsgListNL.getLength();count++){
				Element elem = (Element)opDocGetNWCGOBMsgListNL.item(count);
				map.put("DistributionID".concat("[").concat(Integer.toString(count)).concat("]"),elem.getAttribute("DistributionID"));
			}
			String queueName = NWCGAAConstants.QUEUEID_NO_RESPONSE;
			if(opDocGetNWCGOBMsgListNL.getLength()!=0){
				if (logger.isVerboseEnabled())logger.verbose("about to raise alert!!");
				CommonUtilities.raiseAlert(env,queueName,NWCGAAConstants.ALERT_MESSAGE_7,opDocGetNWCGOBMsgList,null,map);
				
				NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
				for(int c=0;c<opDocGetNWCGOBMsgListNL.getLength();c++){
					Element elem = (Element)opDocGetNWCGOBMsgListNL.item(c);
					String distID = elem.getAttribute("DistributionID");
					String msgKey = elem.getAttribute("MessageKey");
					if (logger.isVerboseEnabled())logger.verbose("distID:"+distID);
					if (logger.isVerboseEnabled())logger.verbose("msgKey:"+msgKey);
					try{
						msgStore.updateMessage(env,distID,"OB","","",NWCGAAConstants.MESSAGE_STATUS_AWAITING_RESPONSE,"",msgKey,true,"");
					}catch(Exception e){
						if (logger.isVerboseEnabled())logger.verbose("Exception thrown during message update");
					}
				}
			}
		}
	}
	
	
	
}
