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

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;

public class NWCGOBMsgServiceHandler extends NWCGJAXRPCWSHandler implements
		Handler {
	public boolean handleRequest(MessageContext mc) {
		System.out.println("@@@@@ Entering NWCGOBMsgServiceHandler::handleRequest @@@@@");
		
		//ClientSignHandler handler = new ClientSignHandler();
		SOAPMessageContext soap = (SOAPMessageContext) mc;
		/*try
		 {
		 mc.setProperty("ssl.configName",ResourceUtil.get("nwcg.ssl.configName","sundanceNode03/ROSS"));
		 mc.setProperty(Call.USERNAME_PROPERTY,ResourceUtil.get("nwcg.call.username","icbs_dev"));
		 mc.setProperty(Call.PASSWORD_PROPERTY,ResourceUtil.get("nwcg.call.password","password1!"));
		 }
		 catch(Exception e)
		 {
		 NWCGLoggerUtil.Log.warning("it did not worked !!!! " + e);
		 NWCGLoggerUtil.printStackTraceToLog(e);
		 }*/
		try {
			// Getting the vector object containing the string tokens.
			Vector stringVector = getStringTokens(soap);
			super.formHeader(mc, soap, stringVector);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("Caught exception ****** Handler ****");
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		
		System.out.println("@@@@@ Exiting NWCGOBMsgServiceHandler::handleRequest @@@@@");
		return true;
	}

	/**
	 * @param soap
	 * Parser method.. Should be moved to generic class handling all parsing.
	 */
	public Vector getStringTokens(SOAPMessageContext soap) {
		System.out.println("@@@@@ Entering NWCGOBMsgServiceHandler::getStringTokens @@@@@");
		
		Vector vectorStr = new Vector();
		StringTokenizer strTok = null;
		try {
			SOAPMessage message = soap.getMessage();

			String str = NWCGAAUtil.serialize(message);
			NWCGLoggerUtil.Log.info("str ::" + str);

			SOAPBody body = message.getSOAPBody();
			Iterator iter = body.getChildElements();
			while (iter.hasNext()) {
				SOAPElement CreateCatalogItemReqelem = (SOAPElement) iter
						.next();
				Iterator iter1 = CreateCatalogItemReqelem.getChildElements();
				while (iter1.hasNext()) {
					SOAPElement Elem = (SOAPElement) iter1.next();
					String MessageOriginatorStr = Elem.getElementName()
							.getLocalName();
					if (MessageOriginatorStr != null
							&& MessageOriginatorStr.equals("MessageOriginator")) {
						Iterator childIter = Elem.getChildElements();
						while (childIter.hasNext()) {
							SOAPElement childElem = (SOAPElement) childIter
									.next();
							String childNameStr = childElem.getElementName()
									.getLocalName();
							if (childNameStr != null
									&& childNameStr.equals("SystemOfOrigin")) {
								Iterator childElemSysOfOriginIter = childElem
										.getChildElements();
								while (childElemSysOfOriginIter.hasNext()) {
									SOAPElement childElemSysOfOriginElem = (SOAPElement) childElemSysOfOriginIter
											.next();
									String childElemSysOfOriginStr = childElemSysOfOriginElem
											.getElementName().getLocalName();
									if (childElemSysOfOriginStr != null
											&& childElemSysOfOriginStr
													.equals("SystemID")) {
										String fullString = childElemSysOfOriginElem
												.getValue();
										strTok = new StringTokenizer(
												fullString, "#");
										int c = 0;
										while (strTok.hasMoreTokens()) {
											vectorStr
													.add(c, strTok.nextToken());
											c++;
										}
										// Jay setting the system id to a blank string
										childElemSysOfOriginElem.setValue("");
										childElemSysOfOriginElem
												.setNodeValue("");
										//childElemSysOfOriginElem.setTextContent("");
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			NWCGLoggerUtil.printStackTraceToLog(ex);
		}
		
		System.out.println("@@@@@ Exiting NWCGOBMsgServiceHandler::getStringTokens @@@@@");
		return vectorStr;
	}

	public boolean handleResponse(MessageContext mc) {
		System.out.println("@@@@@ In NWCGOBMsgServiceHandler::handleResponse @@@@@");
		return true;
	}

	public boolean handleFault(MessageContext mc) {
		System.out.println("@@@@@ Entering NWCGOBMsgServiceHandler::handleFault @@@@@");
		
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		SOAPMessage sm = smc.getMessage();

		try {
			NWCGLoggerUtil.Log.info("About to print SOAP Fault message...");
			sm.writeTo(System.out);
		} catch (SOAPException e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		} catch (IOException e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}

		NWCGLoggerUtil.Log.info("Catching SOAP Fault...");
		Document outDoc = CommonUtilities.getCreateExceptionInput(sm);
		//setSOAPFaultDoc(outDoc);

		NWCGLoggerUtil.Log.info("Updating message store");

		System.out.println("@@@@@ Exiting NWCGOBMsgServiceHandler::handleFault @@@@@");
		return true;
	}

	public void init(HandlerInfo arg0) {
		System.out.println("@@@@@ In NWCGOBMsgServiceHandler::init @@@@@");
	}

	public void destroy() {
		System.out.println("@@@@@ In NWCGOBMsgServiceHandler::destroy @@@@@");
	}

	public QName[] getHeaders() {
		System.out.println("@@@@@ In NWCGOBMsgServiceHandler::getHeaders @@@@@");
		return null;
	}
}