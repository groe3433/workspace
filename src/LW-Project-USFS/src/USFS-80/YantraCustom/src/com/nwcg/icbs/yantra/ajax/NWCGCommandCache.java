package com.nwcg.icbs.yantra.ajax;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

/**
 * @author gacharya
 */


public class NWCGCommandCache {

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGCommandCache.class
			.getName());
	
	private HashMap commandMap = new HashMap();
	private static NWCGCommandCache cache = new NWCGCommandCache();
	
	private NWCGCommandCache() {
		super();
	}

	public static NWCGCommandCache getInstance(){
		return cache; 
	}
	
	public void loadCommands() throws Exception{
		// Load the XML and call populate the command
		//first clear the cache
		commandMap.clear();
		
		if(logger.isVerboseEnabled()) logger.verbose("Entering loadCommands("+ResourceUtil.get(NWCGCommandConstants.COMMAND_FILE_KEY)+")");
		Document commandDoc = XMLUtil.getDocument(CommonUtilities.getResourceStream(ResourceUtil.get(NWCGCommandConstants.COMMAND_FILE_KEY)));
		NodeList nList = commandDoc.getElementsByTagName(NWCGCommandConstants.COMMAND_ELEM);
		//System.out.println("Command List:"+nList.getLength());
		for(int i=0;i<nList.getLength();i++){
			Element n = (Element)nList.item(i);
			NWCGCommand c = new NWCGCommand(n);
			//System.out.println(c);
			commandMap.put(c.getCommandName(),c);
			
		}
		if(logger.isVerboseEnabled()) logger.verbose("Exiting loadCommands()");
		
	}
	
	public NWCGCommand getCommand(String commandName){
		return (NWCGCommand)commandMap.get(commandName);
	}

}
