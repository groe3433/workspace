package com.nwcg.icbs.yantra.ob.handler;

import java.util.HashMap;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;

public class NWCGOBResponseProcessor {		
	
	/**
	 * Input document would be of MDTO format.
	 * @param inputDoc
	 * @return
	 */
	public Document processResponse(HashMap<Object, Object>msgMap) throws Exception{
		System.out.println("@@@@@ Entering NWCGOBResponseProcessor::processResponse @@@@@");
		
		String msgName = (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME);
		Document returnDoc = null ;
		try {
			if (!StringUtil.isEmpty(msgName)) {
				System.out.println("@@@@@ Message Name : " + msgName);
				String hndlrName = NWCGAAUtil.getHandler(msgName);
				
				if (!StringUtil.isEmpty(hndlrName)){
					System.out.println("@@@@@ Class Name : " + hndlrName);
					Class handlerClassObj = Class.forName(hndlrName);
					Object handlerObj = handlerClassObj.newInstance();
					NWCGOBProcessorHandler hndlr = (NWCGOBProcessorHandler) handlerObj;
					System.out.println("@@@@@ Invoking handler...");
					returnDoc = hndlr.process(msgMap);
				}
				else {
					System.out.println("@@@@@ Unable to get handler for " + msgName);
					throw new Exception ("Unable to get handler for " + msgName);
				}
			}
			else {
				System.out.println("@@@@@ Input Document is NULL");
				throw new Exception("Input Document is NULL");
			}
		}
		catch (ClassNotFoundException cnfe){
			System.out.println("!!!!! ClassNotFoundException : " + cnfe.getMessage());
			cnfe.printStackTrace();
			throw cnfe;
		}
		catch (Exception e){
			System.out.println("!!!!!Exception : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("@@@@@ Entering NWCGOBResponseProcessor::processResponse @@@@@");
		return returnDoc;
	}
}