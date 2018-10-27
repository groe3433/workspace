package com.nwcg.icbs.yantra.api.tiggeragent;

import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.tools.util.ArgumentParser;
import com.yantra.ycp.agent.server.YCPAgentTrigger;
import com.yantra.yfs.japi.YFSEnvironment;

/* (non-Javadoc)
*@see java.io.Writer#write(int)
Jay : This is a wrapper class for invoking the triggeragent.sh script
May be a bad design but we want to execute the triggeragent.sh as soon as some even occurs
unable to invoke a triggeragent.sh from the action to though about this work around */
public class NWCGTriggerAgent implements YIFCustomApi {
	
	private static Logger logger = Logger.getLogger(NWCGTriggerAgent.class.getName());
	
	public void setProperties(Properties arg0) throws Exception 
	{
		

	}
	
	public Document triggerCycleCountAgent(YFSEnvironment env,Document inDoc) throws Exception
	{
		String strCache = System.getProperty("yfs.enableLocalCache");
		System.setProperty("yfs.enableLocalCache", "N");
		
		String [] arg = new String[2] ;
		
		arg[0] = "-criteria";
		
		if(NWCGConstants.TRIGGER_AGENT_CRITERIA == null 
				|| NWCGConstants.TRIGGER_AGENT_CRITERIA.equals("") 
				|| NWCGConstants.TRIGGER_AGENT_CRITERIA.equals("null"))
		{
			arg[1] = "CREATE_COUNT_TASKS";
		}
		else
		{
			arg[1] = NWCGConstants.TRIGGER_AGENT_CRITERIA;
		}
		
        ArgumentParser parser = new ArgumentParser(arg);
        Map mOptions = parser.getOptions();
        YCPAgentTrigger alarm = new YCPAgentTrigger(mOptions);
        alarm.sendMessage();
        try
        {
        	if(strCache == null)
        		strCache = "";
        	System.setProperty("yfs.enableLocalCache", strCache);
        }
        catch(Exception e)
        {
        	// doesnt matter if we cant set this
        }
        
        return inDoc;
	}
}
