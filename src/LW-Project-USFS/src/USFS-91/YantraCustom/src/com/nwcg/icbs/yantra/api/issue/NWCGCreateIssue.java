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

package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCreateIssue implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGCreateIssue.class);
	
	public void setProperties(Properties props) throws Exception {
	}
	
	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document createOrder(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssue::createOrder @@@@@");
		Element order = inDoc.getDocumentElement();
		processOrderHeader(inDoc,order);
		Element orderLines = (Element)XMLUtil.getChildNodeByName(order,"OrderLines");
		NodeList orderLineList = orderLines.getElementsByTagName("OrderLine");
		for(int i=0;i<orderLineList.getLength();i++){
			Element orderLine = (Element)orderLineList.item(i);
			processOrderLine(inDoc,orderLine);
		}
		logger.verbose("@@@@@ inDoc:: @@@@@" +XMLUtil.getXMLString(inDoc));
		Document returnDoc = CommonUtilities.invokeAPI(env,"createOrder",inDoc);
		logger.verbose("@@@@@ returnDoc:: @@@@@" +XMLUtil.getXMLString(returnDoc));
		logger.verbose("@@@@@ Exiting NWCGCreateIssue::createOrder @@@@@");
		return returnDoc;
	}
	
	/**
	 * Process the order header and the extended fields associated with the order header
	 * 
	 * @param inDoc
	 * @param order
	 * @throws Exception
	 */
	protected void processOrderHeader(Document inDoc,Element order) throws Exception{
		// Empty Method???
	}

	protected void processOrderLine(Document inDoc,Element orderLine) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssue::processOrderLine @@@@@");
		Element orderLineExtn = (Element)XMLUtil.getChildNodeByName(orderLine,"Extn");
		if(null == orderLineExtn) {
			// there is no extended field in the input so add it
			orderLineExtn = inDoc.createElement("Extn");
			orderLine.appendChild(orderLineExtn);
		}
		// check for the backorder qty
		String backOrderQty = orderLineExtn.getAttribute("ExtnBackorderedQty");
		if(!StringUtil.isEmpty(backOrderQty)) {
			orderLineExtn.setAttribute("ExtnBackOrderFlag","Y");
		}
		logger.verbose("@@@@@ Exiting NWCGCreateIssue::processOrderLine @@@@@");
	}
}