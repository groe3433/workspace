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

package com.nwcg.icbs.yantra.api.masterworkorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * NWCGCreateMasterWorkOrderFromReturns
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date March 21, 2013
 */
public class NWCGCreateMasterWorkOrderFromReturns implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCreateMasterWorkOrderFromReturns.class);

	/**
	 * This method will handle all exceptions and log an Alert for them as they occur. 
	 * 
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		Message.append(" ExceptionType='" + NWCGConstants.NWCG_RETURN_ALERTTYPE
				+ "'" + " InboxType='" + NWCGConstants.NWCG_RETURN_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_RETURN_QUEUEID + "' />");
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION, inTemplate);
		} catch (Exception ex1) {
			//logger.printStackTrace(ex1);
		}
	}
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document createMasterWorkOrderFromReturns(YFSEnvironment env, Document inXML) throws Exception {
		Element elemReturns = inXML.getDocumentElement();
		
		// SETTING VALUES!
		// From inXML: CacheID
		String strCacheID = elemReturns.getAttribute("CacheID");
		// From inXML: IncidentName
		String strIncidentName = elemReturns.getAttribute("IncidentName");
		// From inXML: IncidentNo
		String strIncidentNo = elemReturns.getAttribute("IncidentNo");
		// From inXML: IncidentYear
		String strIncidentYear = elemReturns.getAttribute("IncidentYear");
		// From inXML: ReceiptNo
		String strReceiptNo = elemReturns.getAttribute("ReceiptNo");

		// This method call will take in the 5 values, call an API to get Incident details, and return the output document from that call. 
		Document docNWCGMasterWorkOrder = InvokeIncidentAPI(env, strCacheID, strIncidentName, strIncidentNo, strIncidentYear, strReceiptNo);

		// BEGIN CRJB2 - April 2, 2013 
		
		// SETTING VALUES!
		// From outDoc4 in NWCGPerformIncidentReturn via YFS Environment variable: ReceiptHeaderKey
		String strReceiptHeaderKey = (String)env.getTxnObject("ReceiptHeaderKey");
		
		// BEGIN - Production Issue - CR 928 - May 29, 2013
		
		// validate the strReceiptHeaderKey before using it, also check the null FIRST then only check the empty string. 
		if (strReceiptHeaderKey != null) {
			if(!strReceiptHeaderKey.equals("")) {
				
				// END - Production Issue - CR 928 - May 29, 2013
							
				// Create input document for getReceiptDetails API
				Document docInXML_getReceiptDetails = null;
				docInXML_getReceiptDetails = XMLUtil.newDocument();
				Element elemInXML_etReceiptDetails = docInXML_getReceiptDetails.createElement("Receipt");
				docInXML_getReceiptDetails.appendChild(elemInXML_etReceiptDetails);
				elemInXML_etReceiptDetails.setAttribute("ReceiptHeaderKey", strReceiptHeaderKey);
			
				// validate docInXML_getReceiptDetails before using it
				if(docInXML_getReceiptDetails != null) {
				
					// call getReceiptDetails API before using it
					Document docOutXML_getReceiptDetails = CommonUtilities.invokeAPI(env, "getReceiptDetails_createMasterWorkOrderLine", "getReceiptDetails", docInXML_getReceiptDetails);
				
					// validate docOutXML_getReceiptDetails before using it
					if (docOutXML_getReceiptDetails != null) {
					
						// get list of ReceiptLine elements from docOutXML_getReceiptDetails document
						NodeList listOutXML_getReceiptDetails_ReceiptLine = docOutXML_getReceiptDetails.getElementsByTagName("ReceiptLine");
					
						// validate list of ReceiptLine elements before using it
						if (listOutXML_getReceiptDetails_ReceiptLine != null) {
						
							// get the length of the list of ReceiptLine elements
							int iTotal = listOutXML_getReceiptDetails_ReceiptLine.getLength();
						
							// make sure there is at least 1 Receipt Line
							if(iTotal > 0) {
							
								// This value will be set inside the loop
								String strDispositionCode = "";
								// This value will be set inside the loop
								String strMWOKeyCreated = "";
								
								// loop through and process all Receipt Lines that have a Disposition Code of "NRFI"
								for (int iCounter = 0; iCounter < iTotal; iCounter++) {
								
									// get a Receipt Line, and set it into elemOutXML_getReceiptDetails_ReceiptLine
									Element elemOutXML_getReceiptDetails_ReceiptLine = (Element) listOutXML_getReceiptDetails_ReceiptLine.item(iCounter);
							
									// validate elemOutXML_getReceiptDetails_ReceiptLine before using it
									if(elemOutXML_getReceiptDetails_ReceiptLine != null) {
								
										// SETTING VALUES!
										// From elemOutXML_getReceiptDetails_ReceiptLine: DispositionCode
										strDispositionCode = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("DispositionCode");
							
										// IF AND ONLY IF the return line is NRFI, then create the MWO. 
										if(strDispositionCode.equals("NRFI")) {
											if (strMWOKeyCreated.equals("")) {
												// if strMWOKeyCreated is NOT set, it is the FIRST NRFI Receipt Line, therefore set the MWO Header AND THEN the FIRST MWO Line
												strMWOKeyCreated = createMasterWorkOrderLine(env, elemOutXML_getReceiptDetails_ReceiptLine, null, strCacheID, docNWCGMasterWorkOrder, inXML);
											} else {
												// if strMWOKeyCreated is set, it is NOT the FIRST NRFI Receipt Line, so set it on the existing MWO. 
												strMWOKeyCreated = createMasterWorkOrderLine(env, elemOutXML_getReceiptDetails_ReceiptLine, strMWOKeyCreated, strCacheID, docNWCGMasterWorkOrder, inXML);
											}
										}
							
										// reset strDispositionCode to be ready for the next Receipt Line
										strDispositionCode = "";
									}
								}
							}
						}
					}
				}
			}
		} else {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGCreateMasterWorkOrderFromReturns' " + " strReceiptHeaderKey is null. ");
			throwAlert(env, stbuf);
		}
		
		// END CRJB2 - April 2, 2013 
		
		/*
		NodeList nlReceiptLine = elemReturns.getElementsByTagName("ReceiptLine");
		if (nlReceiptLine != null) {
			int iTotal = nlReceiptLine.getLength();
			String strMWOKeyCreated = null;
			for (int iCounter = 0; iCounter < iTotal; iCounter++) {
				Element elemReceiptLine = (Element) nlReceiptLine.item(iCounter);
				NodeList nlComponent = elemReceiptLine.getElementsByTagName("Component");
				// if no components its just an item or a KIT
				//				 i.e. no components
				if (nlComponent == null || nlComponent.getLength() <= 0) {
					//we force the first iteration of MWOKeyCreated to null, so check for null and pass if first iteration
					if (strMWOKeyCreated == null) {
						strMWOKeyCreated = createMasterWorkOrderLine(env, elemReceiptLine, null, strCacheID, false, docNWCGMasterWorkOrder);
					}
					//A MWO has already been created, issues all MWO Lines to that MWO Key
					else {
						strMWOKeyCreated = createMasterWorkOrderLine(env, elemReceiptLine, strMWOKeyCreated, strCacheID, false, docNWCGMasterWorkOrder);
					}
				}
				// end if its received as kit or simple item
				if (nlComponent != null && nlComponent.getLength() > 0) {
					// it has got components process the components
					int iTotalComponents = nlComponent.getLength();
					// processing components
					for (int iCounterComponents = 0; iCounterComponents < iTotalComponents; iCounterComponents++) {
						Element elemComponent = (Element) nlComponent.item(iCounterComponents);
						// create the master work order line if and only if the line has got NRFI status
						//set MWOKey based on the returned key from createMasterWorkOrderLine
						strMWOKeyCreated = createMasterWorkOrderLine(env, elemComponent, strMWOKeyCreated, strCacheID, true, docNWCGMasterWorkOrder);
					}
				}
			}
		}*/
		
		return inXML;
	}

	/**
	 * looks out for the attributes of the Component or the main item
	 * if the item is serially tracked - creating MWO line per serial number, quantity will always be 1
	 * if the item is not serially tracked - creating only one line for all quantity 
	 * 
	 * @param env
	 * @param elem
	 * @param strMasterWorkOrderKey
	 * @param strCacheID
	 * @param bIsComponent
	 * @param docNWCGMasterWorkOrder
	 * @return
	 * @throws Exception
	 */
	private String createMasterWorkOrderLine(YFSEnvironment env, Element elemOutXML_getReceiptDetails_ReceiptLine, String strMasterWorkOrderKey, String strCacheID, Document docNWCGMasterWorkOrder, Document inXML) throws Exception {		
		// SETTING VALUES!
		// From elemOutXML_getReceiptDetails_ReceiptLine: ShipByDate
		String strShipByDate = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ShipByDate");
		try {
			// try/catch to prevent parse exceptions
			// date input sample from yfs_receipt_line: "01/1/2500"
			if (!strShipByDate.equals("") && strShipByDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date dShipByDate = sdf.parse(strShipByDate);
				SimpleDateFormat sdfTarget = new SimpleDateFormat("yyyy-MM-dd");
				strShipByDate = sdfTarget.format(dShipByDate);
			}
		} catch (Exception ex) {
			// if for some reason we are unable to parse the date, just set it to "" as this is an optional field. 
			strShipByDate = "";
		}
		
		// SETTING VALUES!
		// From elemOutXML_getReceiptDetails_ReceiptLine: Quantity
		String strNRFI = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("Quantity");
		// From elemOutXML_getReceiptDetails_ReceiptLine: ItemID
		String strItemID = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ItemID");
		// From elemOutXML_getReceiptDetails_ReceiptLine: UnitOfMeasure
		String strUOM = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("UnitOfMeasure");
		// From elemOutXML_getReceiptDetails_ReceiptLine: EnterpriseCode
		String strOrganizationCode = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("EnterpriseCode");
		// From elemOutXML_getReceiptDetails_ReceiptLine: DispositionCode
		String strDispositionCode = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("DispositionCode");
		// From elemOutXML_getReceiptDetails_ReceiptLine: ProductClass
		String strProductClass = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ProductClass");
		// From elemOutXML_getReceiptDetails_ReceiptLine: ReceiptHeaderKey
		String strReceiptHeaderKey = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ReceiptHeaderKey");
		// From elemOutXML_getReceiptDetails_ReceiptLine: ReceiptLineKey
		String strReceiptLineKey = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ReceiptLineKey");
		
		NodeList listOutXML_getReceiptDetails_ReceiptLine_Extn = elemOutXML_getReceiptDetails_ReceiptLine.getElementsByTagName("Extn");
		Element elemOutXML_getReceiptDetails_ReceiptLine_Extn = (Element) listOutXML_getReceiptDetails_ReceiptLine_Extn.item(0);
		// From elemOutXML_getReceiptDetails_ReceiptLine_Extn: ExtnReceivedAsComponent
		String strExtnReceivedAsComponent = elemOutXML_getReceiptDetails_ReceiptLine_Extn.getAttribute("ExtnReceivedAsComponent");
		// From elemOutXML_getReceiptDetails_ReceiptLine_Extn: ExtnReceivingPrice
		String strExtnReceivingPrice = elemOutXML_getReceiptDetails_ReceiptLine_Extn.getAttribute("ExtnReceivingPrice");
		
		// pass values to method that will call getItemDetails and return a String for ShortDescription
		String strShortDescription = InvokeItemDetailsAPI(env, strItemID, strUOM, strOrganizationCode);
		
		String strPrimarySerialNo = "";
		String strSecondarySerialNo = "";

		// get Serial number from ReceiptLine
		String strSerialNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("SerialNo");
		
		// this will be true if/when you find your serial info
		boolean isFound = false;
		
		// This Receipt Line has a Trackable Item
		if(!strSerialNo.equals("")) {
			// get Serial list (if there are trackable components there will be stuff in this list)
			NodeList listinXML_Serial = inXML.getElementsByTagName("Serial");
			// get SerialInfoMainItem list (if there are trackable items with NO components there will be stuff in this list)
			NodeList listinXML_SerialInfoMainItem = inXML.getElementsByTagName("SerialInfoMainItem");
			// loop through listinXML_Serial
			for(int iSerial = 0; iSerial < listinXML_Serial.getLength(); iSerial++) {
				Element eleminXML_Serial = (Element) listinXML_Serial.item(iSerial);
				// is this a trackable component?
				if (strSerialNo.equals(eleminXML_Serial.getAttribute("serialID"))) {
					// set Primary Serial number
					strPrimarySerialNo = eleminXML_Serial.getAttribute("serialID");
					// set Secondary Serial number
					strSecondarySerialNo = eleminXML_Serial.getAttribute("SecondarySerialNo");
					// break out as we have found our Serial number and have set it's values, no need to continue
					isFound = true;
					break;
				}
			}
			// Have we previously found a trackable component and set it's Serial details?
			if(!isFound) {
				// loop through listinXML_SerialInfoMainItem
				for(int iSerialInfoMainItem = 0; iSerialInfoMainItem < listinXML_SerialInfoMainItem.getLength(); iSerialInfoMainItem++) {
					Element eleminXML_SerialInfoMainItem = (Element) listinXML_SerialInfoMainItem.item(iSerialInfoMainItem);
					// is this a trackable item, NOT returned as it's components?
					if(strSerialNo.equals(eleminXML_SerialInfoMainItem.getAttribute("PrimarySerialNo"))) {
						// set Primary Serial number
						strPrimarySerialNo = eleminXML_SerialInfoMainItem.getAttribute("PrimarySerialNo");
						// set Secondary Serial number
						strSecondarySerialNo = eleminXML_SerialInfoMainItem.getAttribute("SecondarySerialNo");
						// break out as we have found our Serial number and have set it's values, no need to continue
						isFound = true;
						break;
					}
				}
			}	
		} else {
			// This Receipt Line does NOT have a Trackable Item
		}		
		// SETTING VALUES!
		// From elemOutXML_getReceiptDetails_ReceiptLine: ManufacturerName (Example: "PACIFIC")
		String strManufacturerName = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotAttribute1");
		// From elemOutXML_getReceiptDetails_ReceiptLine: ManufacturerModel (Example: "MKIII")
		String strManufacturerModel = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotAttribute3");
		// From elemOutXML_getReceiptDetails_ReceiptLine: LotNo (Example: "GBK-0148-599")
		String strLotNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotNumber");
		// From elemOutXML_getReceiptDetails_ReceiptLine: RevisionNo (Example: "11/14/2011")
		String strRevisionNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("RevisionNo");
		// From elemOutXML_getReceiptDetails_ReceiptLine: BatchNo (Example: "")
		String strBatchNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("BatchNo");
		// From elemOutXML_getReceiptDetails_ReceiptLine: OwnerUnitID (Example: "IDGBK")
		String strOwnerUnitID = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotAttribute2");
		
		/*
		Element elemTagAttributesMainItem = null;
		if(strExtnReceivedAsComponent.equalsIgnoreCase("Y")) {
			Element elemReturns = inXML.getDocumentElement();
			NodeList nlReceiptLine = elemReturns.getElementsByTagName("ReceiptLine");
			if(nlReceiptLine != null) {
				int iTotal = nlReceiptLine.getLength();
				for(int iCounter = 0 ; iCounter < iTotal ; iCounter ++) {
					Element elemReceiptLine = (Element) nlReceiptLine.item(iCounter);
					NodeList nlComponent = elemReceiptLine.getElementsByTagName("Component");
					if(nlComponent != null && nlComponent.getLength() > 0) {
						int iTotalComponents = nlComponent.getLength();
						for(int iCounterComponents = 0 ; iCounterComponents < iTotalComponents ; iCounterComponents ++) {
							Element elemComponent = (Element) nlComponent.item(iCounterComponents);
							String strInXMLCompItemid = elemComponent.getAttribute("ItemID");
							String strInXMLCompProdClass = elemComponent.getAttribute("ProductClass");
							String strInXMLCompUOM = elemComponent.getAttribute("UOM");
							if(strItemID.equals(strInXMLCompItemid.trim()) && strProductClass.equals(strInXMLCompProdClass.trim()) && strUOM.equals(strInXMLCompUOM.trim())) {
								// set elemTagAttributesMainItem with TagAttributes
								NodeList nlTagAttributesMainItem = elemComponent.getElementsByTagName("TagAttributes");
								if(nlTagAttributesMainItem != null && nlTagAttributesMainItem.getLength() > 0) {
									elemTagAttributesMainItem = (Element) nlTagAttributesMainItem.item(0);
								}
								// break out, no need to continue this search...
								break;
							}
						}
					}
				}
			}
		} else {
			Element elemReturns = inXML.getDocumentElement();
			NodeList nlReceiptLine = elemReturns.getElementsByTagName("ReceiptLine");
			if(nlReceiptLine != null) {
				int iTotal = nlReceiptLine.getLength();
				for(int iCounter = 0 ; iCounter < iTotal ; iCounter ++) {
					Element elemReceiptLine = (Element) nlReceiptLine.item(iCounter);
					String strInXMLNonCompItemid = elemReceiptLine.getAttribute("ItemID");
					String strInXMLNonCompProdClass = elemReceiptLine.getAttribute("ProductClass");
					String strInXMLNonCompUOM = elemReceiptLine.getAttribute("UOM");
					if(strItemID.equals(strInXMLNonCompItemid.trim()) && strProductClass.equals(strInXMLNonCompProdClass.trim()) && strUOM.equals(strInXMLNonCompUOM.trim())) {
						//set elemTagAttributesMainItem with TagAttributesMainItem
						NodeList nlTagAttributesMainItem = elemReceiptLine.getElementsByTagName("TagAttributesMainItem");
						if(nlTagAttributesMainItem != null && nlTagAttributesMainItem.getLength() > 0) {
							elemTagAttributesMainItem = (Element) nlTagAttributesMainItem.item(0);
						}
						// break out, no need to continue this search...
						break;
					}
				}
			}
		}
		*/
		
		// get the quantity, and since we cannot get here unless this is NRFI item, quantity must be greater than or equal to 0. 
		try {
			// Quantity Example: Quantity="1.00"
			int iNRFIQty = (int)Double.parseDouble(strNRFI);
			if (iNRFIQty <= 0) {
				// do not create a mwo line
				return strMasterWorkOrderKey;
			}
		} catch (NumberFormatException nfe) {
			// if exception dont create any MWO line
			return strMasterWorkOrderKey;
		} catch (Exception ex) {
		}
		
		// if and only if masterworkorderkey is null, create a new MWO
		if (strMasterWorkOrderKey == null) {
			Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
			strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
		}
		// creating MWO line per serial number
		Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, strNRFI, strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo, strUOM, strCacheID, strShipByDate, strReceiptHeaderKey, strReceiptLineKey, strExtnReceivingPrice, strManufacturerName, strManufacturerModel, strLotNo, strRevisionNo, strBatchNo, strOwnerUnitID);
		// create master work order line

		CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME, docNWCGMasterWorkOrderLine);
		// we are done with processing all the components return the masterwork order key
		return strMasterWorkOrderKey;
	}
	
		/*
		// if its component, attributes has to be extracted from Serial Tag
		if (bIsComponent) {
			NodeList nlSerialInfoMainItem = elem.getElementsByTagName("Serial");

			if (nlSerialInfoMainItem != null && nlSerialInfoMainItem.getLength() > 0) {
				for (int index = 0; index < nlSerialInfoMainItem.getLength(); index++) {
					Element elemSerialInfoMainItem = (Element) nlSerialInfoMainItem.item(index);
					strPrimarySerialNo = elemSerialInfoMainItem.getAttribute("serialID");
					strSecondarySerialNo1 = elemSerialInfoMainItem.getAttribute("SecondarySerialNo");
					strDispositionCode = elemSerialInfoMainItem.getAttribute("DispositionCode");
					if (strDispositionCode != null && (!strDispositionCode.equals("NRFI"))) {
						//if the disposition code is not NRFI dont create any MWO line
						continue;
					}

					//if and only if masterworkorderkey is null, create a new MWO
					if (strMasterWorkOrderKey == null) {
						Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
						strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
					}
					// creating MWO line per serial number
					Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, "1", strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo1, strUOM, elemSerialInfoMainItem, strCacheID, strShipByDate);
					//				 create master work order line

					CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME, docNWCGMasterWorkOrderLine);
				}
				// we are done with processing all the components return the masterwork order key
				return strMasterWorkOrderKey;
			}

			NodeList nlTagAttributesMainItem = elem.getElementsByTagName("TagAttributes");

			if (nlTagAttributesMainItem != null && nlTagAttributesMainItem.getLength() > 0) {
				elemTagAttributesMainItem = (Element) nlTagAttributesMainItem.item(0);
			}

		} else {
			//			 else extract from SerialInfoMainItem
			NodeList nlSerialInfoMainItem = elem.getElementsByTagName("SerialInfoMainItem");
			if (nlSerialInfoMainItem != null && nlSerialInfoMainItem.getLength() > 0) {
				for (int index = 0; index < nlSerialInfoMainItem.getLength(); index++) {
					Element elemSerialInfoMainItem = (Element) nlSerialInfoMainItem.item(index);
					strPrimarySerialNo = elemSerialInfoMainItem.getAttribute("PrimarySerialNo");
					strSecondarySerialNo1 = elemSerialInfoMainItem.getAttribute("SecondarySerialNo");
					strDispositionCode = elemSerialInfoMainItem.getAttribute("DispositionCode");
					if (strDispositionCode != null && (!strDispositionCode.equals("NRFI"))) {
						//if the disposition code is not NRFI dont create any MWO line
						continue;
					}
					//if and only if masterworkorderkey is null, create a MWO
					if (strMasterWorkOrderKey == null) {
						Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
						strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
					}
					// creating MWO line per serial number
					Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, "1", strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo1, strUOM, elemSerialInfoMainItem, strCacheID, strShipByDate);

					//				 create master work order line
					CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME, docNWCGMasterWorkOrderLine);
				}
				//				 we are done with processing all the KIT item return master work order key
				return strMasterWorkOrderKey;
			}
			NodeList nlTagAttributesMainItem = elem.getElementsByTagName("TagAttributesMainItem");
			if (nlTagAttributesMainItem != null && nlTagAttributesMainItem.getLength() > 0) {
				elemTagAttributesMainItem = (Element) nlTagAttributesMainItem.item(0);
			}
		}

		//control will come here IF AND ONLY IF the item is not serially tracked, in that case there will be ONLY ONE line per return
		try {
			int iNRFIQty = Integer.parseInt(strNRFI);
			//do not create a mwo line
			if (iNRFIQty <= 0) {
				if (logger.isVerboseEnabled())
					logger
							.verbose("Returning the strMasterWorkOrderKey as it was sent from try loop...");
				//return master work order key on error
				return strMasterWorkOrderKey;
			}
		} catch (NumberFormatException ex) {
			// if exception dont create any MWO line
			if (logger.isVerboseEnabled())
				logger
						.verbose("Returning the strMasterWorkOrderKey as it was sent from catch...");
			//return master work order key on error
			return strMasterWorkOrderKey;
		}

		if (strMasterWorkOrderKey == null) {
			Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
			strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
		}
		Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, strNRFI, strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo1, strUOM, elemTagAttributesMainItem, strCacheID, strShipByDate);
		//				 create master work order line

		CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME, docNWCGMasterWorkOrderLine);
		return strMasterWorkOrderKey;
	}
	*/

	/**
	 * 
	 * @param strMasterWorkOrderKey
	 * @param strNRFI
	 * @param strShortDescription
	 * @param strItemID
	 * @param strPrimarySerialNo
	 * @param strProductClass
	 * @param strSecondarySerialNo1
	 * @param strUOM
	 * @param elemSerialInfoMainItem
	 * @param strNode
	 * @param strShipByDate
	 * @return
	 * @throws ParserConfigurationException
	 */
	private Document getMasterWorkOrderLineDocument(String strMasterWorkOrderKey, String strNRFI, String strShortDescription, String strItemID, String strPrimarySerialNo, String strProductClass, String strSecondarySerialNo, String strUOM, String strNode, String strShipByDate, String strReceiptHeaderKey, String strReceiptLineKey, String strExtnReceivingPrice, String strManufacturerName, String strManufacturerModel, String strLotNo, String strRevisionNo, String strBatchNo, String strOwnerUnitID) throws ParserConfigurationException {
		// Create a NWCGMasterWorkOrderLine document, this will be used to call the API that creates the MWO Line later
		Document docNWCGMasterWorkOrderLine = XMLUtil.createDocument("NWCGMasterWorkOrderLine");
		Element elemNWCGMasterWorkOrderLine = docNWCGMasterWorkOrderLine.getDocumentElement();

		// Set all values on the NWCGMasterWorkOrderLine document
		elemNWCGMasterWorkOrderLine.setAttribute("MasterWorkOrderKey", strMasterWorkOrderKey);
		elemNWCGMasterWorkOrderLine.setAttribute("ActualQuantity", strNRFI);
		elemNWCGMasterWorkOrderLine.setAttribute("ItemDesc", strShortDescription);
		elemNWCGMasterWorkOrderLine.setAttribute("ItemID", strItemID);
		elemNWCGMasterWorkOrderLine.setAttribute("PrimarySerialNo", strPrimarySerialNo);
		elemNWCGMasterWorkOrderLine.setAttribute("ProductClass", strProductClass);
		elemNWCGMasterWorkOrderLine.setAttribute("SecondarySerialNo1", strSecondarySerialNo);
		elemNWCGMasterWorkOrderLine.setAttribute("UnitOfMeasure", strUOM);
		elemNWCGMasterWorkOrderLine.setAttribute("Node", strNode);
		elemNWCGMasterWorkOrderLine.setAttribute("ShipByDate", strShipByDate);
		elemNWCGMasterWorkOrderLine.setAttribute("ReceiptHeaderKey", strReceiptHeaderKey);
		elemNWCGMasterWorkOrderLine.setAttribute("ReceiptLineKey", strReceiptLineKey);
		elemNWCGMasterWorkOrderLine.setAttribute("ReceivingPrice", strExtnReceivingPrice);
			elemNWCGMasterWorkOrderLine.setAttribute("ManufacturerName", StringUtil.nonNull(strManufacturerName));
			elemNWCGMasterWorkOrderLine.setAttribute("ManufacturerModel", StringUtil.nonNull(strManufacturerModel));
			elemNWCGMasterWorkOrderLine.setAttribute("LotNo", StringUtil.nonNull(strLotNo));
			elemNWCGMasterWorkOrderLine.setAttribute("RevisionNo", StringUtil.nonNull(strRevisionNo));
			elemNWCGMasterWorkOrderLine.setAttribute("BatchNo", StringUtil.nonNull(strBatchNo));
			elemNWCGMasterWorkOrderLine.setAttribute("OwnerUnitID", StringUtil.nonNull(strOwnerUnitID));
		elemNWCGMasterWorkOrderLine.setAttribute("Status",NWCGConstants.NWCG_REFURB_MWOL_INITIAL_STATUS);
		return docNWCGMasterWorkOrderLine;
	}
	
	/**
	 * 
	 * @param env
	 * @param strCacheID
	 * @param strIncidentName
	 * @param strIncidentNo
	 * @param strIncidentYear
	 * @param strReceiptNo
	 * @return
	 */
	private Document InvokeIncidentAPI(YFSEnvironment env, String strCacheID, String strIncidentName, String strIncidentNo, String strIncidentYear, String strReceiptNo) throws Exception {
		
		// create the document that will hold the MWO
		Document docNWCGMasterWorkOrder = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element elemNWCGMasterWorkOrder = docNWCGMasterWorkOrder.getDocumentElement();

		// create the input document for the API that will return Incident details
		Document docIncidentIP = XMLUtil.createDocument("NWCGIncidentOrder");
		Element elemIncident = docIncidentIP.getDocumentElement();
		elemIncident.setAttribute("IncidentNo", strIncidentNo);
		if (strIncidentYear.length() == 0) {
			strIncidentYear = " ";
		}
		elemIncident.setAttribute("Year", strIncidentYear);
		
		// call the API that will return the Incident details
		Document docIncidentDetailsOut = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, docIncidentIP);
		Element elemIncidentDetails = docIncidentDetailsOut.getDocumentElement();

		// SETTING VALUES!
		// From docIncidentDetailsOut: IncidentBlmAcctCode
		String strBLMCode = elemIncidentDetails.getAttribute("IncidentBlmAcctCode");
		// From docIncidentDetailsOut: IncidentFsAcctCode
		String strFSCode = elemIncidentDetails.getAttribute("IncidentFsAcctCode");
		// From docIncidentDetailsOut: IncidentOtherAcctCode
		String strOtherCode = elemIncidentDetails.getAttribute("IncidentOtherAcctCode");
		// From docIncidentDetailsOut: OverrideCode
		String strOverrideCode = elemIncidentDetails.getAttribute("OverrideCode");
		// From docIncidentDetailsOut: IncidentType
		String strIncidentType = elemIncidentDetails.getAttribute("IncidentType");

		// Setting the values for the MWO header on the MWO document
		elemNWCGMasterWorkOrder.setAttribute("Enterprise", NWCGConstants.ENTERPRISE_CODE);
		elemNWCGMasterWorkOrder.setAttribute("IncidentNo", strIncidentNo);
		elemNWCGMasterWorkOrder.setAttribute("IncidentYear", strIncidentYear);
		elemNWCGMasterWorkOrder.setAttribute("MasterWorkOrderNo", strReceiptNo);
		elemNWCGMasterWorkOrder.setAttribute("Node", strCacheID);
		elemNWCGMasterWorkOrder.setAttribute("IncidentName", strIncidentName);
		elemNWCGMasterWorkOrder.setAttribute("IncidentType", strIncidentType);
		elemNWCGMasterWorkOrder.setAttribute("BLMAccountCode", strBLMCode);
		elemNWCGMasterWorkOrder.setAttribute("FSAccountCode", strFSCode);
		elemNWCGMasterWorkOrder.setAttribute("OtherAccountCode", strOtherCode);
		elemNWCGMasterWorkOrder.setAttribute("OverrideCode", strOverrideCode);
		elemNWCGMasterWorkOrder.setAttribute("MasterWorkOrderType", NWCGConstants.NWCG_INCIDENT_RETURN);
		elemNWCGMasterWorkOrder.setAttribute("ServiceItemID", NWCGConstants.NWCG_SERVICE_ITEM_ID_REFURBISHMENT);
		elemNWCGMasterWorkOrder.setAttribute("MasterWorkOrderType",NWCGConstants.NWCG_INCIDENT_RETURN);
		return docNWCGMasterWorkOrder;
	}	
	
	/**
	 * <Item ItemID="000709" UnitOfMeasure="EA" OrganizationCode="NWCG" /></Item>
	 * 
	 * @param env
	 * @param strItemId
	 * @param strUOM
	 * @param strOrgCode
	 * @return
	 * @throws Exception
	 */
	private String InvokeItemDetailsAPI(YFSEnvironment env, String strItemId, String strUOM, String strOrgCode) throws Exception {
		String strShortDescription = "";
		try {
			// create the input docInXML_getItemDetails document
			Document docInXML_getItemDetails = null;
			docInXML_getItemDetails = XMLUtil.newDocument();
			Element elemInXML_getItemDetails = docInXML_getItemDetails.createElement("Item");
			docInXML_getItemDetails.appendChild(elemInXML_getItemDetails);
			elemInXML_getItemDetails.setAttribute("ItemID", strItemId);
			elemInXML_getItemDetails.setAttribute("UnitOfMeasure", strUOM);
			elemInXML_getItemDetails.setAttribute("OrganizationCode", strOrgCode);
			// call getItemDetails API
			if(docInXML_getItemDetails != null) {
				Document docOutXML_getItemDetails = CommonUtilities.invokeAPI(env, "getItemDetails", docInXML_getItemDetails);
				// validate docOutXML_getItemDetails document before using it
				if(docOutXML_getItemDetails != null) {
					
					// retrieve the ShortDescription and store it in a string
					NodeList listdocOutXML_getItemDetails_PrimaryInformation = docOutXML_getItemDetails.getElementsByTagName("PrimaryInformation");
					if (listdocOutXML_getItemDetails_PrimaryInformation != null) {
						int iTotal = listdocOutXML_getItemDetails_PrimaryInformation.getLength();
						Node nodedocOutXML_getItemDetails_PrimaryInformation = listdocOutXML_getItemDetails_PrimaryInformation.item(0);
						Element elemdocOutXML_getItemDetails_PrimaryInformation = (Element)nodedocOutXML_getItemDetails_PrimaryInformation;
						
						// SETTING VALUES!
						// From elemdocOutXML_getItemDetails_PrimaryInformation: ShortDescription
						strShortDescription = elemdocOutXML_getItemDetails_PrimaryInformation.getAttribute("ShortDescription");
					} else {
						throw new NullPointerException("listdocOutXML_getItemDetails_PrimaryInformation returned null. ");
					}
				} else {
					throw new NullPointerException("docOutXML_getItemDetails returned null. ");
				}
			} else {
				throw new NullPointerException("docInXML_getItemDetails returned null. ");
			}
		} catch (NullPointerException npe) {
			//logger.printStackTrace(npe);
		} catch (Exception exp) {
			//logger.printStackTrace(exp);
		}
		
		return strShortDescription;
	}
}