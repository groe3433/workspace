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

package com.nwcg.icbs.yantra.handler;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGGetOperationResultsHandler extends NWCGJAXRPCWSHandler implements Handler {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGGetOperationResultsHandler.class);

	public boolean handleRequest(MessageContext mc) {
		SOAPMessageContext soap = (SOAPMessageContext) mc;
		try {
			Vector stringVector = getStringTokens(soap);
			super.formHeader(mc, soap, stringVector);
		} catch (Exception e) {
			logger.error("!!!!! General Exception: " + e);
		}
		return true;
	}

	public boolean handleResponse(MessageContext arg0) {
		return false;
	}

	public boolean handleFault(MessageContext mc) {
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		SOAPMessage sm = smc.getMessage();
		Document outDoc = CommonUtilities.getCreateExceptionInput(sm);
		return true;
	}

	public void init(HandlerInfo arg0) {
	}

	public void destroy() {
	}

	public QName[] getHeaders() {
		return null;
	}

	public Vector getStringTokens(SOAPMessageContext soap) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsHandler::getStringTokens");
		Vector vectorStr = new Vector();
		StringTokenizer strTok = null;
		try {
			SOAPMessage message = soap.getMessage();
			SOAPBody body = message.getSOAPBody();
			Iterator iter = body.getChildElements();
			while (iter.hasNext()) {
				Element GetOperationResultsReqelem = (Element) iter.next();
				NodeList nlGetOperationResultsReq = GetOperationResultsReqelem.getChildNodes();
				String strSystemID = NWCGConstants.EMPTY_STRING;
				for (int x = 0; x < nlGetOperationResultsReq.getLength(); x++) {
					Element Elem = (Element) nlGetOperationResultsReq.item(x);
					String tmp = Elem.getLocalName();
					logger.verbose("@@@@@ Elem.getLocalName(); :: " + tmp);
					if (tmp != null && tmp.equals("SystemOfOrigin")) {
						NodeList nlSOO = Elem.getChildNodes();
						for (int y = 0; y < nlSOO.getLength(); y++) {
							Element e = (Element) nlSOO.item(y);
							if (e.getNodeName().equals("SystemID")) {
								strSystemID = XMLUtil.getNodeValue(e);
								strTok = new StringTokenizer(strSystemID, "#");
								int c = 0;
								while (strTok.hasMoreTokens()) {
									vectorStr.add(c, strTok.nextToken());
									c++;
								}
								XMLUtil.setNodeValue(e, NWCGConstants.EMPTY_STRING);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsHandler::getStringTokens");
		return vectorStr;
	}
}