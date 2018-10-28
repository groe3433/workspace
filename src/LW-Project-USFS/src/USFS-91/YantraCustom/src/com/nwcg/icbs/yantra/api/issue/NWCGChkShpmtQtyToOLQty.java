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
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGChkShpmtQtyToOLQty implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGChkShpmtQtyToOLQty.class);
	
	private String orderHdrKey = "";
	
	private Hashtable<String, String> htOLKey2ShpmtQty = new Hashtable<String, String>();

	public void setProperties(Properties arg0) throws Exception {
	}
	
	public Document chkShpmtQtyToOLQty(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkShpmtQtyToOLQty::chkShpmtQtyToOLQty @@@@@");
		logger.verbose("@@@@@ Input XML : " + XMLUtil.extractStringFromDocument(inDoc));
		populateOLKey2ShpmtQty(inDoc);
		if ((htOLKey2ShpmtQty == null) || (htOLKey2ShpmtQty.size() < 1)){
			logger.verbose("@@@@@ Not updating OL with shipment quantity as there are no shipment lines");
			logger.verbose("@@@@@ Exiting NWCGChkShpmtQtyToOLQty::chkShpmtQtyToOLQty (1) @@@@@");
			return inDoc;
		}
		logger.verbose("@@@@@ No of shipment lines in hashtable : " + htOLKey2ShpmtQty.size());
		Document docOrderDtls = getOrderDetails(env);
		verifyAndUpdtOLQuantities(env, docOrderDtls);
		logger.verbose("@@@@@ Exiting NWCGChkShpmtQtyToOLQty::chkShpmtQtyToOLQty @@@@@");
		return inDoc;
	}

	/**
	 * This method will populate the hashtable OLKey to shipments actual quantity
	 * @param inDoc
	 */
	private void populateOLKey2ShpmtQty(Document inDoc){
		logger.verbose("@@@@@ Entering NWCGChkShpmtQtyToOLQty::populateOLKey2ShpmtQty @@@@@");
		Element elmConfShpmtDoc = inDoc.getDocumentElement();
		NodeList nlShpmtLine = elmConfShpmtDoc.getElementsByTagName(NWCGConstants.SHIPMENT_LINE_ELEMENT);
		if (nlShpmtLine != null && nlShpmtLine.getLength() > 0){
			for (int i=0; i < nlShpmtLine.getLength(); i++){
				Element elmShpmtLine = (Element) nlShpmtLine.item(i);
				htOLKey2ShpmtQty.put(elmShpmtLine.getAttribute(NWCGConstants.ORDER_LINE_KEY), elmShpmtLine.getAttribute(NWCGConstants.ACTUAL_QUANTITY));
				orderHdrKey = elmShpmtLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			}
		} else {
			logger.error("@@@@@ There are no shipment lines for this shipment");
		}
		logger.verbose("@@@@@ Exiting NWCGChkShpmtQtyToOLQty::populateOLKey2ShpmtQty @@@@@");
	}
	
	/**
	 * This method will get the order details for a given template
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private Document getOrderDetails(YFSEnvironment env) throws Exception{
		logger.verbose("@@@@@ Entering NWCGChkShpmtQtyToOLQty::getOrderDetails @@@@@");
		Document docOrderDtlsOP = null;
		try {
			Document docOrderDtlsIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + orderHdrKey + "\"/>");
			docOrderDtlsOP = CommonUtilities.invokeAPI(env, "NWCGShipmentQty2OLQty_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, docOrderDtlsIP);
		}
		catch(ParserConfigurationException pce){
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			throw pce;
		} catch (SAXException sae) {
			logger.error("!!!!! Caught SAXException : " + sae);
			throw sae;
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException : " + ioe);
			throw ioe;
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			throw e;
		}
		logger.verbose("@@@@@ Exiting NWCGChkShpmtQtyToOLQty::getOrderDetails @@@@@");
		return docOrderDtlsOP;
	}

	/**
	 * This method will get the order line quantities for each line and will check with
	 * shipments actual quantity. If there is no difference, then it will not do anything.
	 * If it is different, then code will update the OrderedQty with shipments actual quantity
	 * and will add the difference of (OrderedQty - Shipments Actual Quantity) to existing
	 * UTF Quantity
	 * @param env
	 * @param docOrderDtls
	 */
	private void verifyAndUpdtOLQuantities(YFSEnvironment env, Document docOrderDtls) throws Exception{
		logger.verbose("@@@@@ Entering NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities @@@@@");
		boolean updtOLs = false;
		try {
			Document docChgOrderIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + orderHdrKey + "\" Action=\"MODIFY\"/>");
			Element elmChgOrderIP = docChgOrderIP.getDocumentElement();
			Element elmChgOLs = docChgOrderIP.createElement(NWCGConstants.ORDER_LINES);
			elmChgOrderIP.appendChild(elmChgOLs);
			
			Element elmOrderDtls = docOrderDtls.getDocumentElement();
			NodeList nlOL = elmOrderDtls.getElementsByTagName(NWCGConstants.ORDER_LINE);
			if (nlOL != null && nlOL.getLength() > 0){
				logger.verbose("@@@@@ NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities, No of order lines : " + nlOL.getLength());
				for (int i=0; i < nlOL.getLength(); i++){
					Element elmOL = (Element) nlOL.item(i);
					String olKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					String orderedQty = elmOL.getAttribute(NWCGConstants.ORDERED_QTY);
					String actualQtyFromShpmtLine = htOLKey2ShpmtQty.get(olKey);
					if (actualQtyFromShpmtLine == null || actualQtyFromShpmtLine.trim().length() < 1){
						continue;
					}
					
					float qtyFromOL = (new Float(orderedQty)).floatValue();
					float qtyFromSL = (new Float(actualQtyFromShpmtLine)).floatValue();
					if (qtyFromOL > qtyFromSL){
						updtOLs = true;
						Element elmChgOL = docChgOrderIP.createElement(NWCGConstants.ORDER_LINE);
						elmChgOLs.appendChild(elmChgOL);
						elmChgOL.setAttribute(NWCGConstants.ORDER_LINE_KEY, olKey);
						elmChgOL.setAttribute(NWCGConstants.ACTION, "MODIFY");
						elmChgOL.setAttribute(NWCGConstants.ORDERED_QTY, actualQtyFromShpmtLine);
						
						String oldUTFQty = ((Element)elmOL.getElementsByTagName(NWCGConstants.EXTN).item(0))
														.getAttribute(NWCGConstants.EXTN_UTF_QTY);
						float utfQty = new Float(oldUTFQty).floatValue();
						String newUTFQty = new Float(utfQty + (qtyFromOL - qtyFromSL)).toString();
						Element elmChgExtnOL = docChgOrderIP.createElement(NWCGConstants.EXTN);
						elmChgOL.appendChild(elmChgExtnOL);
						elmChgExtnOL.setAttribute(NWCGConstants.EXTN_UTF_QTY, newUTFQty);
					}
					else {
						logger.verbose("@@@@@ Quantities are same");
					}
				}
			}
			else {
				logger.verbose("@@@@@ There are no order lines on this order");
			}
			
			if (updtOLs){
				logger.verbose("@@@@@ Updating order : " + XMLUtil.extractStringFromDocument(docChgOrderIP));
				CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, docChgOrderIP);
			}
			else {
				logger.verbose("@@@@@ OL and Shipment Quantities are in sync");
			}	
		} catch(ParserConfigurationException pce){
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (SAXException sae) {
			logger.error("!!!!! Caught SAXException : " + sae);
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException : " + ioe);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		}catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}			
			
		logger.verbose("@@@@@ Exiting NWCGChkShpmtQtyToOLQty::verifyAndUpdtOLQuantities @@@@@");
	}
}