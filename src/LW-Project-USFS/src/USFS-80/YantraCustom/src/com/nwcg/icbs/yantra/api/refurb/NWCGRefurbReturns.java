/**
 * @author sgunda
 */

package com.nwcg.icbs.yantra.api.refurb;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class would be called as part of
 * - service after confirming a workorder in VAS Station
 * - after entering the item status disposition jsp to update dispostion status - NWCGRefurbDisposition 
 * @author sgunda
 *
 */
public class NWCGRefurbReturns implements YIFCustomApi {
	
	private Properties _properties;
	private static Logger log = Logger.getLogger(NWCGRefurbReturns.class.getName()); 
	
	public void setProperties(Properties prop) throws Exception
	{
		_properties = prop;
	}
	
	/**
	 * This method is called after confirming the workorder in VASStation. Input is the
	 * output of confirmWorkOrderActivity.SNO_ACTIVITIES_COMPLETED event.
	 * At any point of time, we are going to get only one record as output from NWCG_Incident_Return
	 * table as we are searching based on serialized id.
	 * @param env
	 * @param inXML
	 * @return
	 */
	public Document processRefurbSerializedQuantity(YFSEnvironment env, Document inXML)
	throws NWCGException {
		log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, Entered");
		if (env == null)
		{
			log.error("NWCGRefurbReturns::processRefurbSerializedQuantity, YFSEnvironment is null");
			throw new NWCGException("NWCG_ENV_NULL");
		}
		if (inXML == null)
		{
			log.error("NWCGRefurbReturns::processRefurbSerializedQuantity, Input Document is null");
			throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
		}
		
		try {
			if (log.isVerboseEnabled()){
				log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, Input XML : " + XMLUtil.getXMLString(inXML));
			}
			Element rootNode = inXML.getDocumentElement();
			String itemID = rootNode.getAttribute("ItemID");
			String cacheID = rootNode.getAttribute("NodeKey");
			log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, Item ID : " + itemID + ", Cache ID : " + cacheID);
			Element extnElm = (Element) XMLUtil.getChildNodeByName(rootNode, "Extn");
			String incidentNum = "";
			if (extnElm != null) {
				incidentNum = extnElm.getAttribute("ExtnIncidentNo");
			}
			
			Element woActDtlElm = (Element) XMLUtil.getChildNodeByName(rootNode, "WorkOrderActivityDtl");
			String serialNum = woActDtlElm.getAttribute("SerialNo");
			log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, incidentNum : " + incidentNum + ", serialNum : " + serialNum);
			
			if (incidentNum == null || incidentNum.length() < 1){
				String workOrderKey = XMLUtil.getAttribute(rootNode, "WorkOrderKey");
				incidentNum = getIncidentNumber(env, workOrderKey);
			}
			log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, incidentNum : " + incidentNum);
			
			if (itemID == null || cacheID == null || incidentNum == null){
				throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
			}
			
			// 1. Make a call to NWCG_Incident_Returns table to get all the entries for that incident,
			//    item, cache and serialized item (if not null) order by ship_date ascending
			// 2. This will return more than one entry. Loop through the data and check the entry for
			//    which the QuantityNRFI - (QuantityRFIRefurb + QuantityUnsRefurb + QuantityUnsNwtRefurb) > 0
			// 3. Update the entry with the appropriate status. Before updating, there can be a scenario where
			//    we have the quantity from input as more than that of the above value. If that is the case, then
			//    increment the entry for that IncidentReturnKey and update the remaining values for the next 
			//    IncidentReturnKey
			Document rtnEntry = getEntryForIncidentItem(env, incidentNum, cacheID, itemID, serialNum);
			System.out.println("rtnEntry XML "+ XMLUtil.getXMLString(rtnEntry));
			if (rtnEntry == null){
				log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, There is no entry for the given input");
				throw new NWCGException("NWCG_RFB_GET_RETN_INC_001", new String[] {incidentNum, cacheID, itemID, serialNum});
			}
			
			Element outputRootNode = rtnEntry.getDocumentElement();
			Element incRtnElm = (Element) XMLUtil.getChildNodeByName(outputRootNode, "NWCGIncidentReturn");
			String strNrfiQty = incRtnElm.getAttribute("QuantityNRFI");
			String strRfiRfbQty = incRtnElm.getAttribute("QuantityRFIRefurb");
			String strUnsRfbQty = incRtnElm.getAttribute("QuantityUnsRefurb");
			String strUnsNwtRfbQty = incRtnElm.getAttribute("QuantityUnsNwtRefurb");
			if (strNrfiQty == null || strRfiRfbQty == null || strUnsRfbQty == null || strUnsNwtRfbQty == null){
				log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, Quantities are invalid");
				throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
			}
			
			int nrfiQty = new Double(strNrfiQty).intValue();
			int rfiRfbQty = new Double(strRfiRfbQty).intValue();
			int unsRfbQty = new Double(strUnsRfbQty).intValue();
			int unsNwtRfbQty = new Double(strUnsNwtRfbQty).intValue();
			
			if ((nrfiQty - (rfiRfbQty + unsRfbQty + unsNwtRfbQty)) < 1){
				log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, Serial Number already updated");
				throw new NWCGException("NWCG_RFB_GET_RETN_INC_002", 
																new String[] {serialNum, new Integer(nrfiQty).toString(), 
																							new Integer(rfiRfbQty).toString(), 
																							new Integer(unsRfbQty).toString(), 
																							new Integer(unsNwtRfbQty).toString()});
			}
			else {
				log.verbose("NWCGRefurbReturns::processRefurbSerializedQuantity, updating entry in INCIDENT_RETURN table");
				int newRfiRfbQty = 1;
				String incidentRtnKey = incRtnElm.getAttribute("IncidentReturnKey");
				updateIncidentItem(env, incidentNum, cacheID, itemID, serialNum, 
													 incidentRtnKey, "QuantityRFIRefurb", newRfiRfbQty);
			}
		}
		catch(Exception e){
			log.error("NWCGRefurbReturns::processRefurbSerializedQuantity, Exception Msg : " + e.getMessage());
			log.error("NWCGRefurbReturns::processRefurbSerializedQuantity, StackTrace : " + e.getStackTrace());
			throw new NWCGException(e);
		}
		return inXML;
	}
	
	
	/**
	 * This method is called after confirming the workorder in VASStation. Input is the
	 * output of confirmWorkOrderActivity.SKU_ACTIVITIES_COMPLETED event.
	 * @param env
	 * @param inXML
	 * @return
	 */
	public Document processRefurbNonSerQuantity(YFSEnvironment env, Document inXML)
	throws NWCGException {
		log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Entered");
		if (log.isVerboseEnabled()){
			log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Input XML : " + XMLUtil.getXMLString(inXML));
		}
		if (env == null)
		{
			log.error("NWCGRefurbReturns::processRefurbNonSerQuantity, YFSEnvironment is null");
			throw new NWCGException("NWCG_ENV_NULL");
		}
		if (inXML == null)
		{
			log.error("NWCGRefurbReturns::processRefurbNonSerQuantity, Input Document is null");
			throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
		}
		
		try {
			Element rootNode = inXML.getDocumentElement();
			String itemID = rootNode.getAttribute("ItemID");
			log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Item ID : " + itemID);
			String cacheID = rootNode.getAttribute("NodeKey");
			
			Element extnElm = (Element) XMLUtil.getChildNodeByName(rootNode, "Extn");
			String incidentNum = "";
			if (extnElm != null) {
				incidentNum = extnElm.getAttribute("ExtnIncidentNo");
			}
			log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Extn Incident Number : " + incidentNum);
			
			Element woActDtl = (Element) XMLUtil.getChildNodeByName(rootNode, "WorkOrderActivityDtl");
			
			//Element woActDtl = (Element) XMLUtil.getChildNodeByName(inXML.getDocumentElement(), "WorkOrderActivityDtl");			
			String qtyBeingConf = woActDtl.getAttribute("QuantityBeingConfirmed");
			log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Quantity Being Conf : " + qtyBeingConf);
			
			if (incidentNum == null || incidentNum.length() < 1){
				String workOrderKey = rootNode.getAttribute("WorkOrderKey");
				//String workOrderKey = woElm.getAttribute("WorkOrderKey");
				incidentNum = getIncidentNumber(env, workOrderKey);
				log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Incident Num : " + incidentNum);
			}
			
			if (itemID == null || cacheID == null || incidentNum == null){
				log.error("NWCGRefurbReturns::processRefurbNonSerQuantity, Item or Cache or Incident Num is NULL");
				throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
			}
			
			// 1. Make a call to NWCG_Incident_Returns table to get all the entries for that incident,
			//    item, cache and serialized item (if not null) order by ship_date ascending
			// 2. This will return more than one entry. Loop through the data and check the entry for
			//    which the QuantityNRFI - (QuantityRFIRefurb + QuantityUnsRefurb + QuantityUnsNwtRefurb) > 0
			// 3. Update the entry with the appropriate status. Before updating, there can be a scenario where
			//    we have the quantity from input as more than that of the above value. If that is the case, then
			//    increment the entry for that IncidentReturnKey and update the remaining values for the next 
			//    IncidentReturnKey
			log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Making the call to get incident entry");
			Document rtnEntryList = getEntryForIncidentItem(env, incidentNum, cacheID, itemID, "");
			//Document rtnEntryList = getDummyDocument();
			log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Obtained incident entry");
			if (rtnEntryList == null){
				log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, There is no entry for the given input");
				throw new NWCGException("NWCG_RFB_GET_RETN_INC_003", new String[] {incidentNum, cacheID, itemID});
			}
			
			NodeList rtnEntries = rtnEntryList.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
			
			boolean bUpdtQty = false;
			int confirmQty = new Double(qtyBeingConf).intValue();
			for (int i=0; i < rtnEntries.getLength() && !bUpdtQty; i++){
				Element rtnEntry = (Element) rtnEntries.item(i);
				log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Processing entry : " + i);
				confirmQty = processRefurbEntryUpdation(env, rtnEntry, incidentNum, cacheID, 
																								itemID, "QuantityRFIRefurb", confirmQty);
				if (confirmQty < 1){
					bUpdtQty = true;
				}
				else if ((confirmQty > 0) && (i == rtnEntries.getLength()-1)){
					log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, There are no items available from DB to update the refurb quantity");
					throw new NWCGException("NWCG_RFB_GET_RETN_INC_004");
				}
			}
			log.verbose("NWCGRefurbReturns::processRefurbNonSerQuantity, Exiting");
		}
		catch(Exception e){
			log.error("NWCGRefurbReturns::processRefurbNonSerQuantity, Exception Msg : " + e.getMessage());
			log.error("NWCGRefurbReturns::processRefurbNonSerQuantity, StackTrace : " + e.getStackTrace());
			throw new NWCGException(e);
		}
		return inXML;
	}
	
	
	/**
	 * This method updates the refurb quantity 
	 * @param env
	 * @param rtnEntry
	 * @param qtyBeingConf
	 * @return
	 * @throws NWCGException
	 */
	private int processRefurbEntryUpdation(YFSEnvironment env, Element rtnEntry, String incidentNum, 
																				 String cacheID, String itemID, String varToUpdate, int confirmQty)
	throws NWCGException{
		log.verbose("NWCGRefurbReturns::processRefurbEntryUpdation, Entered");
		int result = 0;
		String strNrfiQty = rtnEntry.getAttribute("QuantityNRFI");
		String strRfiRfbQty = rtnEntry.getAttribute("QuantityRFIRefurb");
		String strUnsRfbQty = rtnEntry.getAttribute("QuantityUnsRefurb");
		String strUnsNwtRfbQty = rtnEntry.getAttribute("QuantityUnsNwtRefurb");
		if (strNrfiQty == null || strRfiRfbQty == null || strUnsRfbQty == null || strUnsNwtRfbQty == null){
			log.verbose("NWCGRefurbReturns::processRefurbEntryUpdation, Quantities are invalid");
			throw new NWCGException("NWCG_RFB_GET_RETN_INC_005");
		}
		
		int nrfiQty = new Double(strNrfiQty).intValue();
		int rfiRfbQty = new Double(strRfiRfbQty).intValue();
		int unsRfbQty = new Double(strUnsRfbQty).intValue();
		int unsNwtRfbQty = new Double(strUnsNwtRfbQty).intValue();
		
		int availQtyToUpdate = nrfiQty - (rfiRfbQty + unsRfbQty + unsNwtRfbQty); // available to confirm
		
		if (availQtyToUpdate < 1){
			result = confirmQty;
		}
		else {
			int updtQty = 0;
			if (confirmQty <= availQtyToUpdate){
				updtQty = confirmQty;
				result = 0;
			}
			else if (confirmQty > availQtyToUpdate){
				updtQty = availQtyToUpdate;
				result = confirmQty - availQtyToUpdate;
			}
			
			// Until now, updtQty is the possible quantity that we can update. If we do a blind update, then
			// we will loose the existing data. So, we need to get the appropriate variable and increment the
			// updtQty to that variable.
			if (varToUpdate.equalsIgnoreCase("QuantityRFIRefurb")){
				updtQty = updtQty + rfiRfbQty;
			}
			else if (varToUpdate.equalsIgnoreCase("QuantityUnsRefurb")){
				updtQty = updtQty + unsRfbQty;
			}
			else if (varToUpdate.equalsIgnoreCase("QuantityUnsNwtRefurb")){
				updtQty = updtQty + unsNwtRfbQty;
			}
			
			String incidentRtnKey = rtnEntry.getAttribute("IncidentReturnKey");
			updateIncidentItem(env, incidentNum, cacheID, itemID, "", incidentRtnKey, varToUpdate, updtQty);
		}
		
		log.verbose("NWCGRefurbReturns::processRefurbEntryUpdation, Exiting");
		return result;
	}
	
	/**
	 * This method gets an entry for the given input from NWCG_INCIDENT_RETURN table
	 * @param env
	 * @param incidentNum
	 * @param cacheID
	 * @param itemID
	 * @param serialNum
	 * @return
	 */
	private Document getEntryForIncidentItem(YFSEnvironment env, String incidentNum, 
																					 String cacheID, String itemID, String serialNum){
		log.verbose("NWCGRefurbReturns::getEntryForIncidentItem, Entered");
		Document result = null;
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element incidentElem = inputDoc.createElement("NWCGIncidentReturn");
			inputDoc.appendChild(incidentElem);
			incidentElem.setAttribute("CacheID", cacheID);
			incidentElem.setAttribute("IncidentNo", incidentNum);
			incidentElem.setAttribute("ItemID", itemID);
			incidentElem.setAttribute("QuantityRFIRefurb","0");
			incidentElem.setAttribute("QuantityUnsRefurb","0");
			incidentElem.setAttribute("QuantityUnsNwtRefurb","0");
			if (serialNum != null && serialNum.length() > 1){
				incidentElem.setAttribute("TrackableID", serialNum);
			}
			
			log.verbose("NWCGRefurbReturns::getEntryForIncidentItem, Making the call : " + NWCGConstants.NWCG_RFB_GET_INC_RTN_SERVICE);
			log.verbose("NWCGRefurbReturns::getEntryForIncidentItem, inputDoc : " + XMLUtil.getXMLString(inputDoc));
			result = CommonUtilities.invokeService(env, NWCGConstants.NWCG_RFB_GET_INC_RTN_SERVICE, inputDoc);
		}
		catch(ParserConfigurationException pce){
			log.error("NWCGRefurbReturns::getEntryForIncidentItem, ParserConfigurationException Msg : " + pce.getMessage());
			log.error("NWCGRefurbReturns::getEntryForIncidentItem, StackTrace : " + pce.getStackTrace());
		}
		catch(Exception e){
			log.error("NWCGRefurbReturns::getEntryForIncidentItem, Exception Msg : " + e.getMessage());
			log.error("NWCGRefurbReturns::getEntryForIncidentItem, StackTrace : " + e.getStackTrace());
		}
		
		return result;
	}


	
	/**
	 * This method updates the incident item as part of refurb process
	 * @param env
	 * @param incidentNum
	 * @param cacheID
	 * @param itemID
	 * @param serialNum
	 * @param incidentRtnKey
	 * @param varToUpdate
	 * @param updtQty
	 * @return
	 */
	private Document updateIncidentItem(YFSEnvironment env, String incidentNum, 
																		 String cacheID, String itemID, 
																		 String serialNum, String incidentRtnKey, 
																		 String varToUpdate, int updtQty){
		Document result = null;
		log.verbose("NWCGRefurbReturns::updateIncidentItem, Entered");
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element incidentElem = inputDoc.createElement("NWCGIncidentReturn");
			inputDoc.appendChild(incidentElem);
			incidentElem.setAttribute("CacheID", cacheID);
			incidentElem.setAttribute("IncidentNo", incidentNum);
			incidentElem.setAttribute("ItemID", itemID);
			if (incidentRtnKey != null && incidentRtnKey.length() > 1){
				incidentElem.setAttribute("IncidentReturnKey", incidentRtnKey);
			}
			if (serialNum != null && serialNum.length() > 1){
				incidentElem.setAttribute("TrackableID", serialNum);
			}
			incidentElem.setAttribute(varToUpdate, new Integer(updtQty).toString());
			
			if (log.isVerboseEnabled()){
				log.verbose("NWCGRefurbReturns::updateIncidentItem, Input Doc for update : " + XMLUtil.getXMLString(inputDoc));
			}
			log.verbose("NWCGRefurbReturns::updateIncidentItem, Invoking service " + NWCGConstants.NWCG_RFB_MOD_INC_RTN_SERVICE);
			result = CommonUtilities.invokeService(env, NWCGConstants.NWCG_RFB_MOD_INC_RTN_SERVICE, inputDoc);
		}
		catch(ParserConfigurationException pce){
			log.error("NWCGRefurbReturns::updateIncidentItem, ParserConfigurationException Msg : " + pce.getMessage());
			log.error("NWCGRefurbReturns::updateIncidentItem, StackTrace : " + pce.getStackTrace());
		}
		catch(Exception e){
			log.error("NWCGRefurbReturns::updateIncidentItem, Exception Msg : " + e.getMessage());
			log.error("NWCGRefurbReturns::updateIncidentItem, StackTrace : " + e.getStackTrace());
		}
		
		log.verbose("NWCGRefurbReturns::updateIncidentItem, Returning");
		return result;
	}
	

	/**
	 * This method is called from NWCGRefurbDisposition class. We are updating the INCIDENT_RETURN
	 * table with the appropriate status picked in the dropdrown of Disposition JSP page. Currently
	 * we have only UNSERVICE and UNSRV-NWT (Supply type of DISPOSITION)
	 * @param env
	 * @param incidentNum
	 * @param cacheID
	 * @param itemID
	 * @param serialNum
	 * @param varToUpdate
	 * @param isTracked
	 * @param quantity
	 * @param workOrderKey
	 * @return
	 * @throws NWCGException
	 */
	public String processRefurbDispositionStatus(YFSEnvironment env, String incidentNum, String cacheID,
																							String itemID, String serialNum, String varToUpdate,
																							String isTracked, String quantity, String workOrderKey)
	throws NWCGException{
		log.verbose("NWCGRefurbReturns::processRefurbDispositionStatus, Entered");
		String result = "";
		int qty = new Double(quantity).intValue();
		if (varToUpdate.equalsIgnoreCase(NWCGConstants.RFI_STATUS)){
			varToUpdate = "QuantityRFIRefurb";
		}
		else if (varToUpdate.equalsIgnoreCase(NWCGConstants.DISP_UNSERVICE) || 
						 varToUpdate.equalsIgnoreCase(NWCGConstants.DISP_UNSERVICE_INT)){
			varToUpdate = "QuantityUnsRefurb";
		}
		else if (varToUpdate.equalsIgnoreCase(NWCGConstants.DISP_UNSERVICE_NWT) || 
						 varToUpdate.equalsIgnoreCase(NWCGConstants.DISP_UNSERVICE_NWT_INT)){
			varToUpdate = "QuantityUnsNwtRefurb";
		}
		
		// We don't get incident number as part of DISPOSITION jsp page. So, we are making a call in
		// specific to get the incident number
		if (incidentNum == null || incidentNum.length() < 1){
			incidentNum = this.getIncidentNumber(env, workOrderKey);
			result = incidentNum;
		}
		
		if (isTracked.equalsIgnoreCase(NWCGConstants.YES)){
			updateIncidentItem(env, incidentNum, cacheID, itemID, serialNum, "", varToUpdate, qty);
		}
		else {
			// Get the appropriate record and update it accordingly
			Document rtnEntryList = getEntryForIncidentItem(env, incidentNum, cacheID, itemID, "");
			if (rtnEntryList == null){
				log.verbose("NWCGRefurbReturns::processRefurbDispositionStatus, There is no entry for the given input");
				throw new NWCGException("NWCG_RFB_GET_RETN_INC_003", new String[] {incidentNum, cacheID, itemID});
			}
			
			NodeList rtnEntries = rtnEntryList.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
			
			boolean bUpdtQty = false;
			for (int i=0; i < rtnEntries.getLength() && !bUpdtQty; i++){
				Element rtnEntry = (Element) rtnEntries.item(i);
				qty = processRefurbEntryUpdation(env, rtnEntry, incidentNum, cacheID, itemID, varToUpdate, qty);
				if (qty < 1){
					bUpdtQty = true;
				}
				else if ((qty > 0) && (i == rtnEntries.getLength()-1)){
					log.verbose("NWCGRefurbReturns::processRefurbDispositionStatus, There are no items available from DB to update the refurb quantity");
					throw new NWCGException("NWCG_RFB_GET_RETN_INC_004");
				}
			}
			
		}
		log.verbose("NWCGRefurbReturns::processRefurbDispositionStatus, Exiting");
		return result;
	}
	
	/**
	 * This method gets the incident number based on work order key. This is used while
	 * updating NWCG_INCIDENT_RETURN table
	 * @param env
	 * @param workOrderKey
	 * @return
	 */
	private String getIncidentNumber(YFSEnvironment env, String workOrderKey){
		log.verbose("NWCGRefurbReturns::getIncidentNumber, Entered");
		String result = "";
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element workOrderElem = inputDoc.createElement("WorkOrder");
			inputDoc.appendChild(workOrderElem);
			workOrderElem.setAttribute("WorkOrderKey", workOrderKey);			
			
			Document outputTemplate = XMLUtil.getDocument();
			Element woElem = outputTemplate.createElement("WorkOrder");
			outputTemplate.appendChild(woElem);
			Element extnIncidentElem = outputTemplate.createElement("Extn");
			woElem.appendChild(extnIncidentElem);
			
			log.verbose("NWCGRefurbReturns::getIncidentNumber, Making API call : " + NWCGConstants.API_GET_WORK_ORDER_DTLS);;
			Document outputDoc = CommonUtilities.invokeAPI(env, outputTemplate, NWCGConstants.API_GET_WORK_ORDER_DTLS, inputDoc);
			
			if (outputDoc != null){
				if (log.isVerboseEnabled()){
					log.verbose("NWCGRefurbReturns::getIncidentNumber, Output XML : " + XMLUtil.getXMLString(outputDoc));
				}
				Element rootNode = outputDoc.getDocumentElement();
				Element extnElm = (Element) XMLUtil.getChildNodeByName(rootNode, "Extn");
				result = extnElm.getAttribute("ExtnIncidentNo");
			}
			else {
				log.verbose("NWCGRefurbReturns::getIncidentNumber, getWorkOrderDetails returned NULL output");
			}
		}
		catch(ParserConfigurationException pce){
			log.error("NWCGRefurbReturns::updateIncidentItem, ParserConfigurationException Msg : " + pce.getMessage());
			log.error("NWCGRefurbReturns::updateIncidentItem, StackTrace : " + pce.getStackTrace());
		}
		catch(Exception e){
			log.error("NWCGRefurbReturns::updateIncidentItem, Exception Msg : " + e.getMessage());
			log.error("NWCGRefurbReturns::updateIncidentItem, StackTrace : " + e.getStackTrace());
		}
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
}
