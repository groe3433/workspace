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

package com.nwcg.icbs.yantra.api.refurb;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRefurbWOCost implements YIFCustomApi{

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbWOCost.class);
	
	private Properties _properties;
	
	public void setProperties(Properties prop) throws Exception{
		_properties = prop;
	}

	/**
	 * This method gets the refurb cost of the refurb issue and updates on the work order
	 * @param env
	 * @param orderOPDoc
	 * @return
	 * @throws NWCGException
	 */
	public Document updateWORefurbCost(YFSEnvironment env, Document orderOPDoc)
	throws NWCGException {
		try {
			logger.verbose("NWCGRefurbWOCost::updateWORefurbCost, Entered");
			Element orderElm = orderOPDoc.getDocumentElement();
			//Element orderElm = (Element) XMLUtil.getChildNodeByName(rootNode, "Order");
			//Element rootNode = orderOPDoc.getDocumentElement();
			//Element orderElm = (Element) XMLUtil.getChildNodeByName(rootNode, "Order");
			String totalCost = orderElm.getAttribute("OriginalTotalAmount");
			String enterpriseCode = orderElm.getAttribute("EnterpriseCode");
			String nodeKey = orderElm.getAttribute("ShipNode");
			Element extnElm = (Element) XMLUtil.getChildNodeByName(orderElm, "Extn");
			String woNumber = extnElm.getAttribute("ExtnRefurbWO");
			
			Document woDoc = XMLUtil.newDocument();
			Element woElem = woDoc.createElement("WorkOrder");
			woDoc.appendChild(woElem);
			woElem.setAttribute("EnterpriseCode", enterpriseCode);
			woElem.setAttribute("NodeKey", nodeKey);
			woElem.setAttribute("WorkOrderNo", woNumber);
			
			Element woExtnElem = woDoc.createElement("Extn");
			woElem.appendChild(woExtnElem);
			woExtnElem.setAttribute("ExtnRefurbCost", totalCost);
			
			logger.verbose("NWCGRefurbWOCost::updateWORefurbCost, Calling Modify Work Order");
			CommonUtilities.invokeAPI(env, NWCGConstants.API_MODIFY_WO, woDoc);
			logger.verbose("NWCGRefurbWOCost::updateWORefurbCost, Exiting");
		}
  	catch (ParserConfigurationException pce){
  		logger.error("NWCGRefurbWOCost::updateWORefurbCost, Parser Conf Exc : " + pce.getMessage());
  		logger.error("NWCGRefurbWOCost::updateWORefurbCost, Parser StackTrace : " + pce.getStackTrace());
  		throw new NWCGException(pce);
  	}
  	catch (Exception e){
  		logger.error("NWCGRefurbWOCost::updateWORefurbCost, Exception Message : " + e.getMessage());
  		logger.error("NWCGRefurbWOCost::updateWORefurbCost, StackTrace : " + e.getStackTrace());
  		throw new NWCGException(e);
  	}
  	// Note: We need to pass createOrder output document as input to changeOrderStatus. So, we are
  	// returning the document that we received as input
		return orderOPDoc;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
