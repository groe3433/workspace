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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * @author sdas
 *
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGOBGetIncidentRequestToROSSHandler extends NWCGJAXRPCWSHandler
		implements Handler {

	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBGetIncidentRequestToROSSHandler.class);

	public Vector getStringTokens(SOAPMessageContext soap) {
		logger.verbose("@@@@@ Entering NWCGOBGetIncidentRequestToROSSHandler::getStringTokens");
		Vector vectorStr = new Vector();
		StringTokenizer strTok = null;
		try {
			SOAPMessage message = soap.getMessage();
			String str = NWCGAAUtil.serialize(message);
			SOAPBody body = message.getSOAPBody();
			Iterator iter = body.getChildElements();
			while (iter.hasNext()) {
				SOAPElement soapElem = (SOAPElement) iter.next();
				Iterator iter1 = soapElem.getChildElements();
				while (iter1.hasNext()) {
					SOAPElement Elem = (SOAPElement) iter1.next();
					String MessageOriginatorStr = Elem.getElementName().getLocalName();
					logger.verbose("@@@@@ Elem.getElementName().getLocalName() ::" + MessageOriginatorStr);
					if (MessageOriginatorStr != null && MessageOriginatorStr.equals("MessageOriginator")) {
						Iterator childIter = Elem.getChildElements();
						while (childIter.hasNext()) {
							SOAPElement childElem = (SOAPElement) childIter.next();
							String childNameStr = childElem.getElementName().getLocalName();
							logger.verbose("@@@@@ childElem.getElementName().getLocalName() ::" + childNameStr);
							if (childNameStr != null && childNameStr.equals("SystemOfOrigin")) {
								Iterator childElemSysOfOriginIter = childElem.getChildElements();
								while (childElemSysOfOriginIter.hasNext()) {
									SOAPElement childElemSysOfOriginElem = (SOAPElement) childElemSysOfOriginIter.next();
									String childElemSysOfOriginStr = childElemSysOfOriginElem.getElementName().getLocalName();
									logger.verbose("@@@@@ childElemSysOfOriginElem.getElementName().getLocalName() ::" + childElemSysOfOriginStr);
									if (childElemSysOfOriginStr != null && childElemSysOfOriginStr.equals("SystemID")) {
										String fullString = childElemSysOfOriginElem.getValue();
										strTok = new StringTokenizer(fullString, "#");
										int c = 0;
										while (strTok.hasMoreTokens()) {
											vectorStr.add(c, strTok.nextToken());
											c++;
										}
										childElemSysOfOriginElem.setValue("");
										childElemSysOfOriginElem.setNodeValue("");
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception :: " + ex);
		}
		logger.verbose("@@@@@ Exiting NWCGOBGetIncidentRequestToROSSHandler::getStringTokens");
		return vectorStr;
	}

	public boolean handleRequest(MessageContext mc) {
		SOAPMessageContext soap = (SOAPMessageContext) mc;
		try {
			mc.setProperty(Call.USERNAME_PROPERTY, ResourceUtil.get("nwcg.call.username", "icbs_dev"));
			mc.setProperty(Call.PASSWORD_PROPERTY, ResourceUtil.get("nwcg.call.password", "password1!"));
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		try {
			// Getting the vector object containing the string tokens.
			Vector stringVector = getStringTokens(soap);
			super.formHeader(mc, soap, stringVector);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		return true;
	}

	public boolean handleResponse(MessageContext mc) {
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		SOAPMessage sm = smc.getMessage();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			sm.writeTo(baos);
			String soapMessage = new String(baos.toByteArray());
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException:: " + ioe);
		} catch (SOAPException se) {
			logger.error("!!!!! Caught SOAPException :: " + se);
		}
		return true;
	}

	public boolean handleFault(MessageContext mc) {
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		SOAPMessage sm = smc.getMessage();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			sm.writeTo(baos);
			String soapMessage = new String(baos.toByteArray());
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException:: " + ioe);
		} catch (SOAPException se) {
			logger.error("!!!!! Caught SOAPException :: " + se);
		}
		return false;
	}

	public void init(HandlerInfo arg0) {
	}

	public void destroy() {
	}

	public QName[] getHeaders() {
		return null;
	}
}