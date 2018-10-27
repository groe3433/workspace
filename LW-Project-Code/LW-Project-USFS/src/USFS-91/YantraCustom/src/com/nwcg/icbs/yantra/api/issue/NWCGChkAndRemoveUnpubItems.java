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

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author sgunda
 *
 */
public class NWCGChkAndRemoveUnpubItems implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGChkAndRemoveUnpubItems.class);
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
	 * This method does the following
	 *   - Remove items that are not published to ROSS
	 *   - Set publish status. This parameter will be used later
	 *     in the service for sending the create request message to ROSS. If atleast one line
	 *     is present, then SendRequest field will be set to TRUE, else it will be set to FALSE
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document setPublishStatusAndRemoveUnpubItems(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkAndRemoveUnpubItems::setPublishStatusAndRemoveUnpubItems @@@@@");
		logger.verbose("@@@@@ setPublishStatusAndRemoveUnpubItems: Input XML - \n" + XMLUtil.extractStringFromDocument(docIP));

		Element elmRootIP = docIP.getDocumentElement();
		Element elmOLs = null;
		NodeList nlRootChilds = elmRootIP.getChildNodes();
		for (int rootNode=0; rootNode < nlRootChilds.getLength(); rootNode++){
			Node tmpNode = nlRootChilds.item(rootNode);
			if (tmpNode.getNodeName().equals("OrderLines")){
				elmOLs = (Element) tmpNode; 
			}
		}
		NodeList nlOrderLine = elmOLs.getElementsByTagName("OrderLine");
		String sendReq = "FALSE";
		Hashtable<String, String> olKey2ItemID = new Hashtable<String, String>();
		Vector<String> vecItemList = new Vector<String>();
		
		if (nlOrderLine != null && nlOrderLine.getLength() > 0){
			for (int i=0; i < nlOrderLine.getLength(); i++){
				Element elmOL = (Element) nlOrderLine.item(i);
				String olKey = elmOL.getAttribute("OrderLineKey");
				String itemID = ((Element) elmOL.getElementsByTagName("Item").item(0)).getAttribute("ItemID");
				olKey2ItemID.put(olKey, itemID);
				if (!vecItemList.contains(itemID)){
					vecItemList.add(itemID);
				}
			}

			Hashtable <String, String> htItem2PublishStatus = getItemDtlsAndSetPublishVales(env, vecItemList);
			boolean sendLinesToROSS = false;
			int noOfOLs = nlOrderLine.getLength();
			for (int i=noOfOLs; i > 0; i--){
				Element elmOL = (Element) nlOrderLine.item(i-1);
				String olKey = elmOL.getAttribute("OrderLineKey");
				String itemID = olKey2ItemID.get(olKey);
				String publishStatus = htItem2PublishStatus.get(itemID);
				
				if (publishStatus.equalsIgnoreCase(NWCGConstants.YES)){
					sendLinesToROSS = true;
				}
				else {
					// Delete the orderline
					elmOLs.removeChild(nlOrderLine.item(i-1));
				}
			}
			// If there is atleast one line that has publish to status as "Y", then send the message to ROSS
			if (sendLinesToROSS){
				sendReq = "TRUE";
			}
		}
		else {
			sendReq = "FALSE";
		}
		
		elmRootIP.setAttribute("SendRequest", sendReq);
		logger.verbose("@@@@@ setPublishStatusAndRemoveUnpubItems: Are there any ROSS published items? " + sendReq);
		logger.verbose("@@@@@ Exiting NWCGChkAndRemoveUnpubItems::setPublishStatusAndRemoveUnpubItems @@@@@");
		return docIP;
	}
	
	/**
	 * This method will make getItemList for all the items and returns a hashtable mentioning
	 * whether they are published or not
	 * @param env
	 * @param vecItemList
	 * @return
	 */
	private Hashtable <String, String> getItemDtlsAndSetPublishVales(YFSEnvironment env, Vector<String> vecItemList){
		logger.verbose("@@@@@ Entering NWCGChkAndRemoveUnpubItems::getItemDtlsAndSetPublishVales @@@@@");
		Hashtable <String, String> htItem2PublishStatus = new Hashtable<String, String>();
		try {
			Document docGetItemListIP = XMLUtil.createDocument("Item");
			Element elmItemList = docGetItemListIP.getDocumentElement();
			elmItemList.setAttribute(NWCGConstants.ORGANIZATION_CODE, "NWCG");
			
			Element elmComplexQry = docGetItemListIP.createElement("ComplexQuery");
			elmItemList.appendChild(elmComplexQry);
			elmComplexQry.setAttribute("Operator", "AND");
			
			Element elmComplexOrQry = docGetItemListIP.createElement("Or");
			elmComplexQry.appendChild(elmComplexOrQry);
			
			logger.verbose("@@@@@ No of items : " + vecItemList.size());

			for (int i=0; i < vecItemList.size(); i++){
				String itemID = vecItemList.get(i);
				//Element elmComplexOrItemQry = docGetItemListIP.createElement("Or");
				//elmComplexOrQry.appendChild(elmComplexOrItemQry);
				Element elmComplexOrItemExprQry = docGetItemListIP.createElement("Exp");
				elmComplexOrQry.appendChild(elmComplexOrItemExprQry);
				elmComplexOrItemExprQry.setAttribute("Name", "ItemID");
				elmComplexOrItemExprQry.setAttribute("Value", itemID);
			}
			logger.verbose("@@@@@ Item input XML : " + XMLUtil.extractStringFromDocument(docGetItemListIP));
			
			Document docGetItemListOP = CommonUtilities.invokeAPI(env, 
										"NWCGCreateRequestMsgToROSS_getItemList", 
										NWCGConstants.API_GET_ITEM_LIST, docGetItemListIP);
			
			logger.verbose("@@@@@ Item output XML : " + XMLUtil.extractStringFromDocument(docGetItemListOP));

			NodeList nlItems = docGetItemListOP.getElementsByTagName("Item");
			if (nlItems != null && nlItems.getLength() > 0){
				for (int j=0; j < nlItems.getLength(); j++){
					Element elmItem = (Element) nlItems.item(j);
					String publishToROSS = ((Element) elmItem.getElementsByTagName("Extn").item(0))
												.getAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS);
					htItem2PublishStatus.put(elmItem.getAttribute(NWCGConstants.ITEM_ID), publishToROSS);
				}
			}
			
		}
		catch(ParserConfigurationException pce){
			logger.error("!!!!! Caught Parser Configuration Exception while making " + NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE + pce);
		}
		catch (Exception e){
			logger.error("!!!!! Caught General Exception while making " + NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE + e);
		}
		
		logger.verbose("@@@@@ Exiting NWCGChkAndRemoveUnpubItems::getItemDtlsAndSetPublishVales @@@@@");
		return htItem2PublishStatus;
	}
}