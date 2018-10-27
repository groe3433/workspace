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

package com.nwcg.icbs.yantra.soap;

import java.io.StringWriter;

import javax.xml.soap.SOAPMessage;

import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGResponseStatusTypeReq extends NWCGSOAPMsg {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGResponseStatusTypeReq.class);

	NWCGResponseStatusTypeReq(){
		this.setServiceName("ResponseStatusType");
	}
	
	public SOAPMessage buildSOAPMsg() throws Exception{
		logger.verbose("@@@@@ Entering NWCGResponseStatusTypeReq::buildSOAPMsg");
		SOAPMessage msg = super.buildSOAPMsg();
		StringWriter writer = new StringWriter();
		String reqStr = writer.toString();
		reqStr = reqStr.substring(reqStr.indexOf('>')+1);
		this.theTemplate.setSlot("body", reqStr);
		logger.verbose("@@@@@ Exiting NWCGResponseStatusTypeReq::buildSOAPMsg");
		return msg;
	}
}