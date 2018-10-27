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

import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.PlaceResourceRequestExternalResp;

import java.net.URL;
import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.ib.read.handler.NWCGMessageHandlerInterface;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGDeliverOperationResultsIB;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * test class....I don't think this class has a point....
 */
public class TestIBMessageHandler implements NWCGMessageHandlerInterface, YIFCustomApi {

	/**
	 * 
	 *
	 */
	public TestIBMessageHandler() {
		System.out.println("@@@@@ In TestIBMessageHandler::TestIBMessageHandler @@@@@");
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("@@@@@ Entering TestIBMessageHandler::main @@@@@");
		YFSEnvironment env = null;
		try {
			new TestIBMessageHandler().process(null, null);
		} catch (NWCGException e) {
			// do nothing
		}
		System.out.println("@@@@@ Exiting TestIBMessageHandler::main @@@@@");
	}

	/**
	 * 
	 */
	public Document process(YFSEnvironment env, Document msgXML) throws NWCGException {
		System.out.println("@@@@@ Entering TestIBMessageHandler::process @@@@@");
		PlaceResourceRequestExternalResp resp = new PlaceResourceRequestExternalResp();
		resp.setRegisterInterestInd(true);
		ResponseStatusType value = new ResponseStatusType();
		value.setReturnCode(100);
		resp.setResponseStatus(value);
		try {
			Document document = XMLUtil.getDocument();
			new NWCGJAXBContextWrapper().getDocumentFromObject(resp, document, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			document.getDocumentElement().setAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, "g055ef2c-9347-11de-9b2a-b5c775c9b078");
			new NWCGDeliverOperationResultsIB().process(env, document);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("!!!!! TestIBMessageHandler caught: " + e);
		}
		System.out.println("@@@@@ Exiting TestIBMessageHandler::process @@@@@");
		return msgXML;
	}

	/**
	 * 
	 * @return
	 */
	public String testMethod() {
		System.out.println("@@@@@ In TestIBMessageHandler::testMethod @@@@@");
		return "test";
	}

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		System.out.println("@@@@@ In TestIBMessageHandler::setProperties @@@@@");
	}
}