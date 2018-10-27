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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * NWCGGetOriginalIssuePrice returns the Latest Issued Price for an item. 
 * It gets called on reorderPrice API User Exit.  
 * 
 * @author Lightwellinc
 * @version 1.0
 * @date November 5, 2014
 */
public class NWCGGetOriginalIssuePrice implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGGetOriginalIssuePrice.class);
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
	}
	
	/**
	 * The inXML comes from the JSP using the "resolveValue" method. 
	 * 
	 * Sample inXML: 
	 * 		<NWCGGetOriginalIssuePrice 
	 * 			IncidentNo="AK-MID-001000" Year="2013" 
     *			ItemID="000159" OrderedQty="1" 
     *			ShipNode="IDGBK" TrackableId="AKK-0159-1270"/>
     *
	 * Sample outXML:
	 * 		<NWCGGetOriginalIssuePrice 
	 * 			LastIssuedPrice="726.32"/>
	 * 
	 * @param env
	 * @param inDoc
	 * @return Document
	 * @throws Exception
	 */
	public Document getOriginalIssuePrice(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOriginalIssuePrice::getOriginalIssuePrice");
		String strIncidentNo = "";
		String strYear = "";
		String strItemID = "";
		String strProductClass = "";
		String strTransactionalUOM = "";
		String strShipNode = "";
		String strTrackableId = "";
		NodeList nlinDocNWCGGetOriginalIssuePrice = inDoc.getElementsByTagName("NWCGGetOriginalIssuePrice");
		if(nlinDocNWCGGetOriginalIssuePrice.getLength() == 1) {
			Node nodeinDocNWCGGetOriginalIssuePrice = nlinDocNWCGGetOriginalIssuePrice.item(0);
			Element eleminDocNWCGGetOriginalIssuePrice = (Element)nodeinDocNWCGGetOriginalIssuePrice;
			strIncidentNo = eleminDocNWCGGetOriginalIssuePrice.getAttribute("IncidentNo");
			strYear = eleminDocNWCGGetOriginalIssuePrice.getAttribute("Year");
			strItemID = eleminDocNWCGGetOriginalIssuePrice.getAttribute("ItemID");
			strProductClass = eleminDocNWCGGetOriginalIssuePrice.getAttribute("ProductClass");
			strTransactionalUOM = eleminDocNWCGGetOriginalIssuePrice.getAttribute("TransactionalUOM");
			strShipNode = eleminDocNWCGGetOriginalIssuePrice.getAttribute("ShipNode");
			strTrackableId = eleminDocNWCGGetOriginalIssuePrice.getAttribute("ExtnTrackableId");
		}
		
		String strLastIssuedPrice = findLastIssuedPrice(env, strIncidentNo, strYear, strItemID, strShipNode, strTrackableId);
		if(strLastIssuedPrice.trim().equals("")) {
			logger.verbose("@@@@@ The item's originally issued price was not found, getting the current price from getItemDetails...");
			strLastIssuedPrice = getCurrentSystemPrice(env, strItemID, strTransactionalUOM);
		} else {
			logger.verbose("@@@@@ Item was Found on an Issue, therefore return this price...");
		}			

		Document outDoc = null;
		outDoc = XMLUtil.newDocument();
		Element elemoutDocNWCGGetOriginalIssuePrice = outDoc.createElement("NWCGGetOriginalIssuePrice");
		outDoc.appendChild(elemoutDocNWCGGetOriginalIssuePrice);
		elemoutDocNWCGGetOriginalIssuePrice.setAttribute("LastIssuedPrice", strLastIssuedPrice);
		logger.verbose("@@@@@ Exiting NWCGGetOriginalIssuePrice::getOriginalIssuePrice");
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param strIncidentNo
	 * @param strYear
	 * @param strItemID
	 * @param strOrderedQty
	 * @param strShipNode
	 * @return
	 * @throws Exception
	 */
	private String findLastIssuedPrice(YFSEnvironment env, String strIncidentNo, String strYear, String strItemID, String strShipNode, String strTrackableId) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOriginalIssuePrice::findLastIssuedPrice");
		SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyyMMdd");
		Date prev = datetimeFormatter1.parse("20000101");
		Date curr = datetimeFormatter1.parse("20000101");
		String strLastIssuePrice = "";
		String strPrevOrderHeaderKey = "";
		if((strTrackableId.trim()).equals("")) {
			// Create an input document that will return all the issues for a given incident number using the given item id. 
			Document inDoc1 = null;
			inDoc1 = XMLUtil.newDocument();
			Element eleminDoc1Order = inDoc1.createElement("Order");
			inDoc1.appendChild(eleminDoc1Order);
			eleminDoc1Order.setAttribute("DocumentType", "0001");
			eleminDoc1Order.setAttribute("ShipNode", strShipNode);
			Element eleminDoc1OrderLine = inDoc1.createElement("OrderLine");
			eleminDoc1Order.appendChild(eleminDoc1OrderLine);
			Element eleminDoc1Item = inDoc1.createElement("Item");
			eleminDoc1OrderLine.appendChild(eleminDoc1Item);
			eleminDoc1Item.setAttribute("ItemID", strItemID);
			Element eleminDoc1Extn = inDoc1.createElement("Extn");
			eleminDoc1Order.appendChild(eleminDoc1Extn);
			eleminDoc1Extn.setAttribute("ExtnIncidentNo", strIncidentNo);
			eleminDoc1Extn.setAttribute("ExtnIncidentYear", strYear);
			Element eleminDoc1DataSecurity = inDoc1.createElement("DataSecurity");
			eleminDoc1Order.appendChild(eleminDoc1DataSecurity);
			eleminDoc1DataSecurity.setAttribute("Operator", "AND");
			Element eleminDoc1And = inDoc1.createElement("And");
			eleminDoc1DataSecurity.appendChild(eleminDoc1And);
			Element eleminDoc1Or = inDoc1.createElement("Or");
			eleminDoc1And.appendChild(eleminDoc1Or);
			Element eleminDoc1Exp1 = inDoc1.createElement("Exp");
			eleminDoc1Or.appendChild(eleminDoc1Exp1);
			eleminDoc1Exp1.setAttribute("Name", "EnterpriseCode");
			eleminDoc1Exp1.setAttribute("Value", "NWCG");
			Element eleminDoc1Exp2 = inDoc1.createElement("Exp");
			eleminDoc1Or.appendChild(eleminDoc1Exp2);
			eleminDoc1Exp2.setAttribute("Name", "BuyerOrganizationCode");
			eleminDoc1Exp2.setAttribute("Value", "NWCG");
			Element eleminDoc1Exp3 = inDoc1.createElement("Exp");
			eleminDoc1Or.appendChild(eleminDoc1Exp3);
			eleminDoc1Exp3.setAttribute("Name", "SellerOrganizationCode");
			eleminDoc1Exp3.setAttribute("Value", "NWCG");
			Element eleminDoc1Exp4 = inDoc1.createElement("Exp");
			eleminDoc1Or.appendChild(eleminDoc1Exp4);
			eleminDoc1Exp4.setAttribute("Name", "EnterpriseCode");
			eleminDoc1Exp4.setAttribute("Value", "NWCG");
			Document outDoc1 = CommonUtilities.invokeAPI(env, "NWCGGetOriginalIssuePrice_getOrderList", "getOrderList", inDoc1);
			
			// Iterate through the issues for the given item id until you find the item id you are looking for and return its originally issued price
			String strOrderHeaderKey = "";
			String strOrderNo = "";
			NodeList listoutDoc1Order = outDoc1.getElementsByTagName("Order");
			// Most likely the LATEST will be LAST, therefore count starting at the end of the list backward
			for(int listCount = (listoutDoc1Order.getLength()-1); listCount >= 0; listCount--) {
				Node nodeoutDoc1Order = listoutDoc1Order.item(listCount);
				Element elemoutDoc1Order = (Element)nodeoutDoc1Order;
				strOrderHeaderKey = elemoutDoc1Order.getAttribute("OrderHeaderKey");
				strOrderNo = elemoutDoc1Order.getAttribute("OrderNo");
				// need to find the LATEST issue with the given item id
				if(!strPrevOrderHeaderKey.equals("")) {
					prev = datetimeFormatter1.parse(strPrevOrderHeaderKey.substring(0, 7));
					curr = datetimeFormatter1.parse(strOrderHeaderKey.substring(0, 7));
				}
				if(curr.after(prev) || strPrevOrderHeaderKey.equals("")) {
					// get the OrderLine with the given item id from the previously retrieved OrderHeaderKey. 
					Document indoc2 = null;
					indoc2 = XMLUtil.newDocument();
					Element elemindoc2OrderLine = indoc2.createElement("OrderLine");
					indoc2.appendChild(elemindoc2OrderLine);
					elemindoc2OrderLine.setAttribute("OrderHeaderKey", strOrderHeaderKey);
					logger.verbose("@@@@@ Taking the price from this OrderHeaderKey :: " + strOrderHeaderKey);
					Element elemindoc2OrderLineItem = indoc2.createElement("Item");
					elemindoc2OrderLine.appendChild(elemindoc2OrderLineItem);
					elemindoc2OrderLineItem.setAttribute("ItemID", strItemID);
					Document outDoc2 = CommonUtilities.invokeAPI(env, "NWCGGetOriginalIssuePrice_getOrderLineList", "getOrderLineList", indoc2);
					
					// Get the Originally Issued Price for that item id
					Node nodeoutDoc2LinePriceInfo = XPathUtil.getNode(outDoc2.getDocumentElement(), "/OrderLineList/OrderLine/LinePriceInfo");
					Element elemoutDoc2LinePriceInfo = (Element)nodeoutDoc2LinePriceInfo;
					strLastIssuePrice = elemoutDoc2LinePriceInfo.getAttribute("UnitPrice");
					strPrevOrderHeaderKey = strOrderHeaderKey;
				} 
			}
		} else {
			// Create an input document that will return all the issues for a given incident number using the given item id for the given trackable id. 
			Document inDoc3 = null;
			inDoc3 = XMLUtil.newDocument();
			Element eleminDoc3NWCGIssueTrackableList = inDoc3.createElement("NWCGIssueTrackableList");
			inDoc3.appendChild(eleminDoc3NWCGIssueTrackableList);
			eleminDoc3NWCGIssueTrackableList.setAttribute("IncidentNo", strIncidentNo);
			eleminDoc3NWCGIssueTrackableList.setAttribute("IncidentYear", strYear);
			eleminDoc3NWCGIssueTrackableList.setAttribute("SerialNo", strTrackableId);
			Document outDoc3 = CommonUtilities.invokeService(env, "NWCGGetIssueTrackableListService", inDoc3);
			String strOrderHeaderKey = "";
			String strOrderLineKey = "";
			String strIssueNo = "";
			NodeList listoutDoc3NWCGIssueTrackableList = outDoc3.getElementsByTagName("NWCGIssueTrackableList");
			if(listoutDoc3NWCGIssueTrackableList.getLength() > 0) {
				// Most likely the LATEST will be LAST, therefore count starting at the end of the list backward
				for(int listCount = (listoutDoc3NWCGIssueTrackableList.getLength()-1); listCount >= 0; listCount--) {
					Node nodeoutDoc3NWCGIssueTrackableList = listoutDoc3NWCGIssueTrackableList.item(listCount);
					Element elemoutDoc3NWCGIssueTrackableList = (Element)nodeoutDoc3NWCGIssueTrackableList;
					strOrderHeaderKey = elemoutDoc3NWCGIssueTrackableList.getAttribute("OrderHeaderKey");
					strOrderLineKey = elemoutDoc3NWCGIssueTrackableList.getAttribute("OrderLineKey");
					strIssueNo = elemoutDoc3NWCGIssueTrackableList.getAttribute("IssueNo");
					// Get the LATEST issue for the given Incident number
					if(!strPrevOrderHeaderKey.equals("")) {
						prev = datetimeFormatter1.parse(strPrevOrderHeaderKey.substring(0, 7));
						curr = datetimeFormatter1.parse(strOrderHeaderKey.substring(0, 7));
					}
					if(curr.after(prev) || strPrevOrderHeaderKey.equals("")) {
						Document inDoc4 = null;
						inDoc4 = XMLUtil.newDocument();
						Element eleminDoc4Order = inDoc4.createElement("Order");
						inDoc4.appendChild(eleminDoc4Order);
						eleminDoc4Order.setAttribute("OrderHeaderKey", strOrderHeaderKey);
						logger.verbose("@@@@@ Taking the price from this OrderHeaderKey :: " + strOrderHeaderKey);
						eleminDoc4Order.setAttribute("OrderNo", strIssueNo);
						Document outDoc4 = CommonUtilities.invokeAPI(env, "NWCGGetOriginalIssuePrice_getOrderDetails_TrackableItems", "getOrderDetails", inDoc4);
						// Get the Originally Issued Price of the item id for the given trackable id
						Node nodeoutDoc4OrderLine = XPathUtil.getNode(outDoc4.getDocumentElement(), "/Order/OrderLines/OrderLine[@OrderLineKey='" + strOrderLineKey + "']");
						Node nodeoutDoc4OrderLine_LinePriceInfo = nodeoutDoc4OrderLine.getFirstChild();
						Element elemoutDoc4OrderLine_LinePriceInfo = (Element)nodeoutDoc4OrderLine_LinePriceInfo;
						strLastIssuePrice = elemoutDoc4OrderLine_LinePriceInfo.getAttribute("UnitPrice");
						strPrevOrderHeaderKey = strOrderHeaderKey;
					} 
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetOriginalIssuePrice::findLastIssuedPrice");
		return strLastIssuePrice;
	}
	
	/**
	 * 
	 * @param strItemID
	 * @param strProductClass
	 * @param strTransactionalUOM
	 * @return
	 */
	private String getCurrentSystemPrice(YFSEnvironment env, String strItemID, String strTransactionalUOM) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOriginalIssuePrice::getCurrentSystemPrice");
		Document inDoc = null;
		inDoc = XMLUtil.newDocument();
		Element eleminDocItem = inDoc.createElement("Item");
		inDoc.appendChild(eleminDocItem);
		eleminDocItem.setAttribute("ItemID", strItemID);
		eleminDocItem.setAttribute("OrganizationCode", "NWCG");	
		eleminDocItem.setAttribute("UnitOfMeasure", strTransactionalUOM);
		Document outDoc = CommonUtilities.invokeAPI(env, "NWCGGetOriginalIssuePrice_getItemDetails", "getItemDetails", inDoc);
		Node nodeoutDocPrimaryInformation = XPathUtil.getNode(outDoc.getDocumentElement(), "/Item/PrimaryInformation");
		Element elemoutDocPrimaryInformation = (Element)nodeoutDocPrimaryInformation;
		String strLastIssuePrice = elemoutDocPrimaryInformation.getAttribute("UnitCost");
		logger.verbose("@@@@@ Exiting NWCGGetOriginalIssuePrice::getCurrentSystemPrice");
		return strLastIssuePrice;
	}
}