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

package com.nwcg.icbs.yantra.ob.handler.generic;

import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;

/*
 * Any who doesnt want to set up the header information can use this handler to set up SSL properties.
 */
public class NWCGOBGenericHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		System.out.println("@@@@@ In NWCGOBGenericHandler::getHeaders @@@@@");
		return null;
	}

	public void close(javax.xml.ws.handler.MessageContext arg0) {
		System.out.println("@@@@@ In NWCGOBGenericHandler::close @@@@@");
	}

	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("@@@@@ Entering NWCGOBGenericHandler::handleFault @@@@@");
		
		try {
			smc.getMessage().writeTo(System.out);
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGOBGenericHandler::handleFault @@@@@");
		return false;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("@@@@@ Entering NWCGOBGenericHandler::handleMessage @@@@@");
		
		NWCGAAUtil.setSSL();
		NWCGLoggerUtil.Log.info("* handleMessage *");
		try {
			smc.getMessage().writeTo(System.out);
		} catch (SOAPException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGOBGenericHandler::handleMessage @@@@@");
		return false;
	}
}