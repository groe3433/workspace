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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Properties;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;

/**
 * CR 1693 - the ShipToKey is not getting populated on ROSS place requests. 
 * This is causing the orderline to get left in "Released" status. (should be "Included in Shipment")
 * 
 * @author lightwell
 * @date July 30, 2015
 */
public class NWCGGetAndPasteShipToKey implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGGetAndPasteShipToKey.class);
	
	public void setProperties(Properties props) throws Exception {
	}
	
	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document GetAndPasteShipToKey(YFSEnvironment env, Document inXML) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetAndPasteShipToKey::GetAndPasteShipToKey @@@@@");
		logger.verbose("@@@@@ inXML :: " + XMLUtil.getXMLString(inXML));

		try {
			String strDocumentType = XPathUtil.getString(inXML.getDocumentElement(), "/Order/@DocumentType");
			logger.verbose("@@@@@ strDocumentType :: " + strDocumentType);
			if(strDocumentType.equals(null) || strDocumentType == null) {
				logger.error("!!!!! strDocumentType :: " + strDocumentType);
			}
			String strOrderHeaderKey = XPathUtil.getString(inXML.getDocumentElement(), "/Order/@OrderHeaderKey");
			logger.verbose("@@@@@ strOrderHeaderKey :: " + strOrderHeaderKey);
			if(strOrderHeaderKey.equals(null) || strOrderHeaderKey == null) {
				logger.error("!!!!! strOrderHeaderKey :: " + strOrderHeaderKey);
			}
			
			// We only need to consider checking ShipToKey if the DocumentType is "0001". 
			if(strDocumentType.equals("0001")) {
				
				// Get ExtnSystemOfOrigin and ShipToKey from getOrderDetails
				StringBuffer sbTemp_getOrderDetails = new StringBuffer("<Order ShipToKey=\"\" ShipToID=\"\"></Order>");
				String strTemp_getOrderDetails = sbTemp_getOrderDetails.toString();
				logger.verbose("@@@@@ strTemp_getOrderDetails :: " + strTemp_getOrderDetails);
				StringBuffer sbInput_getOrderDetails = new StringBuffer("<Order OrderHeaderKey=\"" + strOrderHeaderKey + "\"/>");
				String strInput_getOrderDetails = sbInput_getOrderDetails.toString();
				logger.verbose("@@@@@ strInput_getOrderDetails :: " + strInput_getOrderDetails);
				Document outDoc_getOrderDetails = CommonUtilities.invokeAPI(env, XMLUtil.getDocument(strTemp_getOrderDetails), "getOrderDetails", XMLUtil.getDocument(strInput_getOrderDetails));
				logger.verbose("@@@@@ outDoc_getOrderDetails :: " + XMLUtil.getXMLString(outDoc_getOrderDetails));
				String strNewShipToKey = XPathUtil.getString(outDoc_getOrderDetails.getDocumentElement(), "/Order/@ShipToKey");
				logger.verbose("@@@@@ strNewShipToKey :: " + strNewShipToKey);
				if(strNewShipToKey.equals(null) || strNewShipToKey == null) {
					logger.error("!!!!! strNewShipToKey :: " + strNewShipToKey);
				}
				String strNewShipToId = XPathUtil.getString(outDoc_getOrderDetails.getDocumentElement(), "/Order/@ShipToID");
				logger.verbose("@@@@@ strNewShipToId :: " + strNewShipToId);
				if(strNewShipToId.equals(null) || strNewShipToId == null) {
					logger.error("!!!!! strNewShipToId :: " + strNewShipToId);
				}
				
				Element elemInXML_Order = (Element)XPathUtil.getNode(inXML.getDocumentElement(), "/Order");
				elemInXML_Order.setAttribute("Action", "MODIFY");
					
				// figure out which order lines our new ShipToKey
				boolean bIsNewShipToDetails = false;
				NodeList nlInXML_OrderLine = XPathUtil.getNodeList(inXML.getDocumentElement(), "/Order/OrderLines/OrderLine");
				logger.verbose("@@@@@ nlInXML_OrderLine.getLength() :: " + nlInXML_OrderLine.getLength());
				for(int orderlinecounter = 0; orderlinecounter < nlInXML_OrderLine.getLength(); orderlinecounter++) {
					Element elemInXML_OrderLine = (Element)nlInXML_OrderLine.item(orderlinecounter);
					String strMaxLineStatus = elemInXML_OrderLine.getAttribute("MaxLineStatus");
					logger.verbose("@@@@@ strMaxLineStatus :: " + strMaxLineStatus);
					if(strMaxLineStatus.equals(null) || strMaxLineStatus == null) {
						logger.error("!!!!! strMaxLineStatus :: " + strMaxLineStatus);
					}
					
					// If the order line is 1100 (Created) then we need to consider setting ShipToKey. 
					if(strMaxLineStatus.equals("1100")) {
						// Here paste the ShipToKey on the input doc and call changeOrder
						elemInXML_OrderLine.setAttribute("ShipToKey", strNewShipToKey);
						elemInXML_OrderLine.setAttribute("ShipToID", strNewShipToId);
							
						// if we set a ShipToKey on an OrderLine then mark it so we can call changeOrder API. 
						bIsNewShipToDetails = true;
					}
				}
					
				// call changeOrder only 1 time if we have marked it as having a new ShipToKey
				if(bIsNewShipToDetails) {
					logger.verbose("@@@@@ Calling changeOrder API because at least 1 OrderLine needs a ShipToKey value... ");
					CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, inXML);
				}
			} 
		} catch(NullPointerException npe) {
			logger.error("!!!!! NullPointerException :: " + npe);
			npe.printStackTrace();
		} catch(Exception ex) {
			logger.error("!!!!! Caught General Exception :: " + ex);
			ex.printStackTrace();
		}
		
		logger.verbose("@@@@@ Exiting NWCGGetAndPasteShipToKey::GetAndPasteShipToKey @@@@@");
		return null;
	}
}