package com.nwcg.icbs.yantra.commCore;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetOperationsResultsWS {

	public Document recieveMsg(YFSEnvironment env, Document xml) throws Exception{
		
		
        
        String msg_str = NWCGAAUtil.readText(NWCGProperties.getProperty("SIM_MSG_GET_OPERATION_RESULT_RESP_FILENAME"));
        
                
        return NWCGAAUtil.buildXMLDocument(msg_str);
        
		
		
	}
}
