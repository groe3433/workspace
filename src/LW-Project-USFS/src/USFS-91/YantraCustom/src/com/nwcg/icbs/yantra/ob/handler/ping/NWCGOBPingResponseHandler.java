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

package com.nwcg.icbs.yantra.ob.handler.ping;

import java.util.HashMap;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.ob.handler.NWCGOBProcessorHandler;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

// handler for ping service, it does nothing just returns the xml document
public class NWCGOBPingResponseHandler  implements NWCGOBProcessorHandler {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBPingResponseHandler.class);

	public Document process(HashMap<Object, Object> msgMap) throws Exception {
		logger.verbose("Entering com.nwcg.icbs.yantra.ob.handler.NWCGOBPingResponseHandler:process");
		Object objResp = msgMap.get(NWCGAAConstants.MDTO_MSGBODY);
		Document document = XMLUtil.getDocument();
		document = new NWCGJAXBContextWrapper().getDocumentFromUnknownObject(objResp, document);
		logger.verbose("NWCGOBPingResponseHandler:: Returning " + XMLUtil.extractStringFromDocument(document));
		logger.verbose("Exiting com.nwcg.icbs.yantra.ob.handler.NWCGOBPingResponseHandler:process");
		return document;
	}
}
