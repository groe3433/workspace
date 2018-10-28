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
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;

/**
 * @author sdas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGOBGetIncidentRequestToROSSHandler extends NWCGJAXRPCWSHandler
		implements Handler {

	public Vector getStringTokens(SOAPMessageContext soap) {
		System.out.println("@@@@@ Entering NWCGOBGetIncidentRequestToROSSHandler::getStringTokens @@@@@");

		Vector vectorStr = new Vector();
		StringTokenizer strTok = null;
		try {
			SOAPMessage message = soap.getMessage();

			String str = NWCGAAUtil.serialize(message);
			NWCGLoggerUtil.Log.info("str ::" + str);

			SOAPBody body = message.getSOAPBody();
			Iterator iter = body.getChildElements();
			while (iter.hasNext()) {
				NWCGLoggerUtil.Log.info("inside while");
				SOAPElement soapElem = (SOAPElement) iter.next();
				NWCGLoggerUtil.Log.info("soapElem ::" + soapElem.toString());
				Iterator iter1 = soapElem.getChildElements();
				while (iter1.hasNext()) {
					SOAPElement Elem = (SOAPElement) iter1.next();
					NWCGLoggerUtil.Log.info("Elem ::" + Elem.toString());
					String MessageOriginatorStr = Elem.getElementName()
							.getLocalName();
					NWCGLoggerUtil.Log.info("MessageOriginatorStr ::"
							+ MessageOriginatorStr);
					if (MessageOriginatorStr != null
							&& MessageOriginatorStr.equals("MessageOriginator")) {
						Iterator childIter = Elem.getChildElements();
						while (childIter.hasNext()) {
							SOAPElement childElem = (SOAPElement) childIter
									.next();
							NWCGLoggerUtil.Log.info("childElem ::"
									+ childElem.toString());
							String childNameStr = childElem.getElementName()
									.getLocalName();
							NWCGLoggerUtil.Log.info("childNameStr :"
									+ childNameStr);
							if (childNameStr != null
									&& childNameStr.equals("SystemOfOrigin")) {
								Iterator childElemSysOfOriginIter = childElem
										.getChildElements();
								while (childElemSysOfOriginIter.hasNext()) {
									SOAPElement childElemSysOfOriginElem = (SOAPElement) childElemSysOfOriginIter
											.next();
									String childElemSysOfOriginStr = childElemSysOfOriginElem
											.getElementName().getLocalName();
									NWCGLoggerUtil.Log
											.info("childElemSysOfOriginStr :"
													+ childElemSysOfOriginStr);
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

		}
		
		System.out.println("@@@@@ Exiting NWCGOBGetIncidentRequestToROSSHandler::getStringTokens @@@@@");
		return vectorStr;
	}

	public boolean handleRequest(MessageContext mc) {
		System.out.println("@@@@@ Entering NWCGOBGetIncidentRequestToROSSHandler::handleRequest @@@@@");

		//ClientSignHandler handler = new ClientSignHandler();
		SOAPMessageContext soap = (SOAPMessageContext) mc;
		try {
			//mc.setProperty("ssl.configName",ResourceUtil.get("nwcg.ssl.configName","sundanceNode03/ROSS"));
			mc.setProperty(Call.USERNAME_PROPERTY, ResourceUtil.get(
					"nwcg.call.username", "icbs_dev"));
			mc.setProperty(Call.PASSWORD_PROPERTY, ResourceUtil.get(
					"nwcg.call.password", "password1!"));
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("it did not work !!!! " + e);

		}
		try {
			// Getting the vector object containing the string tokens.
			Vector stringVector = getStringTokens(soap);
			super.formHeader(mc, soap, stringVector);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("Caught exception ****** Handler ****");

		}
		
		System.out.println("@@@@@ Exiting NWCGOBGetIncidentRequestToROSSHandler::handleRequest @@@@@");
		return true;
	}

	public boolean handleResponse(MessageContext mc) {
		System.out.println("@@@@@ Entering NWCGOBGetIncidentRequestToROSSHandler::handleResponse @@@@@");
		
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		SOAPMessage sm = smc.getMessage();

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			sm.writeTo(baos);
			String soapMessage = new String(baos.toByteArray());
			NWCGLoggerUtil.Log.info("soap message in handle response:");
			NWCGLoggerUtil.Log.info(soapMessage);

			/*ServletEndpointContextImpl contextImpl = ServletEndpointContextImpl.get();
			 ServletContext servletContext = contextImpl.getServletContext();
			 servletContext.setAttribute("GetIncidentRespXML",soapMessage);*/

		} catch (IOException e) {
			NWCGLoggerUtil.Log.warning("io exception:" + e.toString());
		} catch (SOAPException e) {
			NWCGLoggerUtil.Log.warning("SOAPException :" + e.toString());
		}

		System.out.println("@@@@@ Exiting NWCGOBGetIncidentRequestToROSSHandler::handleResponse @@@@@");
		return true;
	}

	public boolean handleFault(MessageContext mc) {
		System.out.println("@@@@@ Entering NWCGOBGetIncidentRequestToROSSHandler::handleFault @@@@@");
		
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		SOAPMessage sm = smc.getMessage();

		try {
			NWCGLoggerUtil.Log.info("About to print SOAP Fault message...");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			sm.writeTo(baos);
			String soapMessage = new String(baos.toByteArray());
			NWCGLoggerUtil.Log.info(soapMessage);
		} catch (IOException e) {
			NWCGLoggerUtil.Log.warning("io exception:" + e.toString());
		} catch (SOAPException e) {
			NWCGLoggerUtil.Log.warning("SOAPException :" + e.toString());
		}

		System.out.println("@@@@@ Exiting NWCGOBGetIncidentRequestToROSSHandler::handleFault @@@@@");
		return false;
	}

	public void init(HandlerInfo arg0) {
		System.out.println("@@@@@ In NWCGOBGetIncidentRequestToROSSHandler::init @@@@@");
	}

	public void destroy() {
		System.out.println("@@@@@ In NWCGOBGetIncidentRequestToROSSHandler::destroy @@@@@");
	}

	public QName[] getHeaders() {
		System.out.println("@@@@@ In NWCGOBGetIncidentRequestToROSSHandler::getHeaders @@@@@");
		return null;
	}

}
