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
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

/**
 * @author sdas
 * 
 */
public class NWCGGetOperationResultsHandler extends NWCGJAXRPCWSHandler
		implements Handler {

	public boolean handleRequest(MessageContext mc) {
		System.out.println("@@@@@ Entering NWCGGetOperationResultsHandler::handleRequest @@@@@");

		SOAPMessageContext soap = (SOAPMessageContext) mc;

		try {
			Vector stringVector = getStringTokens(soap);
			super.formHeader(mc, soap, stringVector);
		} catch (Exception e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}

		System.out
				.println("@@@@@ Exiting NWCGGetOperationResultsHandler::handleRequest @@@@@");
		return true;
	}

	public boolean handleResponse(MessageContext arg0) {
		System.out
				.println("@@@@@ In NWCGGetOperationResultsHandler::handleResponse @@@@@");
		return false;
	}

	public boolean handleFault(MessageContext mc) {
		System.out
				.println("@@@@@ Entering NWCGGetOperationResultsHandler::handleFault @@@@@");

		NWCGLoggerUtil.Log
				.info("Entering handleFault... printing MessageContext:");
		NWCGLoggerUtil.Log.info(mc.toString());
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		SOAPMessage sm = smc.getMessage();

		// try {
		NWCGLoggerUtil.Log.info("About to print SOAP Fault message...");
		// sm.writeTo(System.out);
		NWCGLoggerUtil.Log.info(sm.toString());
		// }catch (IOException e) {
		// e.printStackTrace();
		// } catch (SOAPException e) {
		// e.printStackTrace();
		// }

		NWCGLoggerUtil.Log.info("Catching SOAP Fault...");
		Document outDoc = CommonUtilities.getCreateExceptionInput(sm); // TBD
																		// need
																		// to be
																		// generic
																		// alert
																		// method

		System.out
				.println("@@@@@ Exiting NWCGGetOperationResultsHandler::handleFault @@@@@");
		return true;
	}

	public void init(HandlerInfo arg0) {
		System.out
				.println("@@@@@ In NWCGGetOperationResultsHandler::init @@@@@");
	}

	public void destroy() {
		System.out
				.println("@@@@@ In NWCGGetOperationResultsHandler::destroy @@@@@");
	}

	public QName[] getHeaders() {
		System.out
				.println("@@@@@ In NWCGGetOperationResultsHandler::getHeaders @@@@@");
		return null;
	}

	public Vector getStringTokens(SOAPMessageContext soap) {
		System.out
				.println("@@@@@ Entering NWCGGetOperationResultsHandler::getStringTokens @@@@@");

		// String one_time_password = NWCGConstants.EMPTY_STRING;
		Vector vectorStr = new Vector();
		StringTokenizer strTok = null;
		try {
			SOAPMessage message = soap.getMessage();
			NWCGLoggerUtil.Log.info("message :");
			NWCGLoggerUtil.Log.info(message.toString());

			SOAPBody body = message.getSOAPBody();
			Iterator iter = body.getChildElements();

			while (iter.hasNext()) {

				Element GetOperationResultsReqelem = (Element) iter.next();
				NodeList nlGetOperationResultsReq = GetOperationResultsReqelem
						.getChildNodes();

				// Iterator iter1 =
				// GetOperationResultsReqelem.getChildElements();

				String strSystemID = NWCGConstants.EMPTY_STRING;

				NWCGLoggerUtil.Log
						.finest("*************************************");
				NWCGLoggerUtil.Log
						.finest("nlGetOperationResultsReq.getLength()"
								+ nlGetOperationResultsReq.getLength());

				for (int x = 0; x < nlGetOperationResultsReq.getLength(); x++) {
					// System.out.println("In for loop, iteration: " + x);
					Element Elem = (Element) nlGetOperationResultsReq.item(x);
					// Node nodeElem = (Node)nlGetOperationResultsReq.item(x);

					// SOAPElement soapElem =
					// (SOAPElement)nlGetOperationResultsReq.item(x);
					String tmp = Elem.getLocalName();
					NWCGLoggerUtil.Log.finest("String tmp is: " + tmp);
					NWCGLoggerUtil.Log.finest("NodeName is: "
							+ Elem.getNodeName());
					NWCGLoggerUtil.Log.finest("Elem is: "
							+ XMLUtil.getElementXMLString(Elem));
					NWCGLoggerUtil.Log.finest("tmp.value() is: "
							+ Elem.getNodeValue());

					if (tmp != null && tmp.equals("SystemOfOrigin")) {
						NodeList nlSOO = Elem.getChildNodes();
						NWCGLoggerUtil.Log.finest("nlSOO.getLenght is: "
								+ nlSOO.getLength());
						NWCGLoggerUtil.Log
								.finest("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

						for (int y = 0; y < nlSOO.getLength(); y++) {
							// System.out.println("y is: " + y);
							Element e = (Element) nlSOO.item(y);
							NWCGLoggerUtil.Log.finest("NodeName is: "
									+ e.getNodeName());

							if (e.getNodeName().equals("SystemID")) {

								NWCGLoggerUtil.Log.finest("Found SystemID...");
								strSystemID = XMLUtil.getNodeValue(e);
								strTok = new StringTokenizer(strSystemID, "#");
								int c = 0;
								while (strTok.hasMoreTokens()) {
									vectorStr.add(c, strTok.nextToken());
									c++;
								}
								NWCGLoggerUtil.Log
										.finest("Setting SystemID to blank...");
								XMLUtil.setNodeValue(e,
										NWCGConstants.EMPTY_STRING);
								NWCGLoggerUtil.Log.finest("e is now: "
										+ XMLUtil.getNodeValue(e));
							}
							NWCGLoggerUtil.Log.finest("SOO elem is: "
									+ XMLUtil.getNodeValue(e));
						}
					}
				}
			}
		} catch (Exception e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}

		System.out
				.println("@@@@@ Exiting NWCGGetOperationResultsHandler::getStringTokens @@@@@");
		return vectorStr;
	}
}