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

package com.nwcg.icbs.yantra.ob.handler;

import java.util.HashMap;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGOBResponseProcessor {		
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBResponseProcessor.class);
	
	/**
	 * Input document would be of MDTO format.
	 * @param inputDoc
	 * @return
	 */
	public Document processResponse(HashMap<Object, Object>msgMap) throws Exception{
		
		logger.verbose("Entering com.nwcg.icbs.yantra.ob.handler.NWCGOBResponseProcessor:processResponse");
		String msgName = (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME);
		Document returnDoc = null ;
		try {
			if (!StringUtil.isEmpty(msgName)) {
				String hndlrName = NWCGAAUtil.getHandler(msgName);
				
				if (!StringUtil.isEmpty(hndlrName)){
					Class handlerClassObj = Class.forName(hndlrName);
					Object handlerObj = handlerClassObj.newInstance();
					NWCGOBProcessorHandler hndlr = (NWCGOBProcessorHandler) handlerObj;
					returnDoc = hndlr.process(msgMap);
				}
				else {
					throw new Exception ("Unable to get handler for " + msgName);
				}
			}
			else {
				throw new Exception("Input Document is NULL");
			}
		}
		catch (ClassNotFoundException cnfe){
			//logger.printStackTrace(cnfe);
			throw cnfe;
		}
		catch (Exception e){
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}
		
		logger.verbose("Exiting com.nwcg.icbs.yantra.ob.handler.NWCGOBResponseProcessor:processResponse");
		return returnDoc;
	}
}