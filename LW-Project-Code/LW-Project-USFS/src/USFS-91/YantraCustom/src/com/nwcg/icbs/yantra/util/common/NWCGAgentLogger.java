package com.nwcg.icbs.yantra.util.common;

import com.yantra.yfc.log.YFCLogCategory;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import com.yantra.integration.adapter.IntegrationAdapter;

public class NWCGAgentLogger extends NWCGApplicationLogger {
	
	protected NWCGAgentLogger(String name) {
		super(name);
	}
	public static NWCGApplicationLogger instance(Class clazz)
	{
		return instance(clazz.getName());
    }
	public static NWCGApplicationLogger instance(String name)
	{
		if ( logger == null ) {
			logger =  YFCLogCategory.instance("CONSOLE");
			nlogger = new NWCGApplicationLogger("CONSOLE");
			String agentServer = System.getenv("AGENT_JAVA_SERVER");
			if ( agentServer != null ) {
				logger.verbose("Set logger to CONSOLE, agent = "+agentServer);
			}
			else {
				logger.verbose("Set logger to CONSOLE");
			}
		}
	    return nlogger;
	}
	
}
