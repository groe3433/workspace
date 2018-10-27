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

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
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
	
	private static YFCLogCategory logger = YFCLogCategory.instance(NWCGCreateMasterWorkOrderFromReturns.class);

	/**
	 * This method will handle all exceptions and log an Alert for them as they occur. 
	 * 
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		System.out.println("@@@@@ Entering NWCGAutomateMoveRequest::throwAlert @@@@@");
		Message.append(" ExceptionType='" + NWCGConstants.NWCG_RETURN_ALERTTYPE
				+ "'" + " InboxType='" + NWCGConstants.NWCG_RETURN_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_RETURN_QUEUEID + "' />");
		try {
			System.out.println("@@@@@ Trying to log the Alert...");
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION, inTemplate);
			System.out.println("@@@@@ Logged the Alert.");
		} catch (Exception ex1) {
			System.out.println("@@@@@ Caught exception while trying to log the Alert, choosing to do nothing about it...");
			ex1.printStackTrace();
		}
		System.out.println("@@@@@ Exiting NWCGAutomateMoveRequest::throwAlert @@@@@");
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
		System.out.println("@@@@@ Entering NWCGCreateMasterWorkOrderFromReturns::createMasterWorkOrderFromReturns @@@@@");
		System.out.println("@@@@@ inXML: " + XMLUtil.getXMLString(inXML));

		Element elemReturns = inXML.getDocumentElement();
		
		// SETTING VALUES!
		// From inXML: CacheID
		String strCacheID = elemReturns.getAttribute("CacheID");
		System.out.println("@@@@@ strCacheID: " + strCacheID);
		// From inXML: IncidentName
		String strIncidentName = elemReturns.getAttribute("IncidentName");
		System.out.println("@@@@@ strIncidentName: " + strIncidentName);
		// From inXML: IncidentNo
		String strIncidentNo = elemReturns.getAttribute("IncidentNo");
		System.out.println("@@@@@ strIncidentNo: " + strIncidentNo);
		// From inXML: IncidentYear
		String strIncidentYear = elemReturns.getAttribute("IncidentYear");
		System.out.println("@@@@@ strIncidentYear: " + strIncidentYear);
		// From inXML: ReceiptNo
		String strReceiptNo = elemReturns.getAttribute("ReceiptNo");
		System.out.println("@@@@@ strReceiptNo: " + strReceiptNo);

		// This method call will take in the 5 values, call an API to get Incident details, and return the output document from that call. 
		Document docNWCGMasterWorkOrder = InvokeIncidentAPI(env, strCacheID, strIncidentName, strIncidentNo, strIncidentYear, strReceiptNo);
		System.out.println("@@@@@ createMasterWorkOrderFromReturns: " + XMLUtil.getXMLString(docNWCGMasterWorkOrder));

		// BEGIN CRJB2 - April 2, 2013 
		
		// SETTING VALUES!
		// From outDoc4 in NWCGPerformIncidentReturn via YFS Environment variable: ReceiptHeaderKey
		String strReceiptHeaderKey = (String)env.getTxnObject("ReceiptHeaderKey");
		System.out.println("@@@@@ strReceiptHeaderKey: " + strReceiptHeaderKey);
		
		// BEGIN - Production Issue - CR 928 - May 29, 2013
		
		// validate the strReceiptHeaderKey before using it, also check the null FIRST then only check the empty string. 
		if (strReceiptHeaderKey != null) {
			if(!strReceiptHeaderKey.equals("")) {
				
				// END - Production Issue - CR 928 - May 29, 2013
			
				System.out.println("@@@@@ strReceiptHeaderKey is valid...");
				
				// Create input document for getReceiptDetails API
				Document docInXML_getReceiptDetails = null;
				docInXML_getReceiptDetails = XMLUtil.newDocument();
				Element elemInXML_etReceiptDetails = docInXML_getReceiptDetails.createElement("Receipt");
				docInXML_getReceiptDetails.appendChild(elemInXML_etReceiptDetails);
				elemInXML_etReceiptDetails.setAttribute("ReceiptHeaderKey", strReceiptHeaderKey);
				System.out.println("@@@@@ docInXML_getReceiptDetails : " + XMLUtil.getXMLString(docInXML_getReceiptDetails));
			
				// validate docInXML_getReceiptDetails before using it
				if(docInXML_getReceiptDetails != null) {
				
					// call getReceiptDetails API before using it
					Document docOutXML_getReceiptDetails = CommonUtilities.invokeAPI(env, "getReceiptDetails_createMasterWorkOrderLine", "getReceiptDetails", docInXML_getReceiptDetails);
					System.out.println("@@@@@ docOutXML_getReceiptDetails : " + XMLUtil.getXMLString(docOutXML_getReceiptDetails));
				
					// validate docOutXML_getReceiptDetails before using it
					if (docOutXML_getReceiptDetails != null) {
					
						// get list of ReceiptLine elements from docOutXML_getReceiptDetails document
						NodeList listOutXML_getReceiptDetails_ReceiptLine = docOutXML_getReceiptDetails.getElementsByTagName("ReceiptLine");
					
						// validate list of ReceiptLine elements before using it
						if (listOutXML_getReceiptDetails_ReceiptLine != null) {
						
							// get the length of the list of ReceiptLine elements
							int iTotal = listOutXML_getReceiptDetails_ReceiptLine.getLength();
							System.out.println("@@@@@ listOutXML_getReceiptDetails_ReceiptLine length: " + iTotal);
						
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
										System.out.println("@@@@@ strDispositionCode: " + strDispositionCode);
							
										// IF AND ONLY IF the return line is NRFI, then create the MWO. 
										if(strDispositionCode.equals("NRFI")) {
											System.out.println("@@@@@ This is an NRFI item...");
										
											System.out.println("@@@@@ strMWOKeyCreated is: " + strMWOKeyCreated);
											if (strMWOKeyCreated.equals("")) {
												System.out.println("@@@@@ The MWO Key is \"\", it is the FIRST NRFI Receipt Line, therefore set the MWO Header AND THEN the FIRST MWO Line. ");
												// if strMWOKeyCreated is NOT set, it is the FIRST NRFI Receipt Line, therefore set the MWO Header AND THEN the FIRST MWO Line
												strMWOKeyCreated = createMasterWorkOrderLine(env, elemOutXML_getReceiptDetails_ReceiptLine, null, strCacheID, docNWCGMasterWorkOrder, inXML);
											} else {
												System.out.println("@@@@@ The MWO Key is NOT \"\", it is NOT the FIRST NRFI Receipt Line, so set it on the existing MWO. ");
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
			System.out.println("!!!!! strReceiptHeaderKey is null. ");
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGCreateMasterWorkOrderFromReturns' " + " strReceiptHeaderKey is null. ");
			throwAlert(env, stbuf);
		}
		
		// END CRJB2 - April 2, 2013 
		
		/*
		NodeList nlReceiptLine = elemReturns.getElementsByTagName("ReceiptLine");
		if (nlReceiptLine != null) {
			int iTotal = nlReceiptLine.getLength();
			System.out.println("@@@@@ Setting strMWOKeyCreated = null;");
			String strMWOKeyCreated = null;
			for (int iCounter = 0; iCounter < iTotal; iCounter++) {
				Element elemReceiptLine = (Element) nlReceiptLine.item(iCounter);
				NodeList nlComponent = elemReceiptLine.getElementsByTagName("Component");
				// if no components its just an item or a KIT
				//				 i.e. no components
				if (nlComponent == null || nlComponent.getLength() <= 0) {
					//we force the first iteration of MWOKeyCreated to null, so check for null and pass if first iteration
					System.out.println("@@@@@ There are no components, creating a MWOLine...");
					if (strMWOKeyCreated == null) {
						strMWOKeyCreated = createMasterWorkOrderLine(env, elemReceiptLine, null, strCacheID, false, docNWCGMasterWorkOrder);
					}
					//A MWO has already been created, issues all MWO Lines to that MWO Key
					else {
						strMWOKeyCreated = createMasterWorkOrderLine(env, elemReceiptLine, strMWOKeyCreated, strCacheID, false, docNWCGMasterWorkOrder);
					}
					System.out.println("@@@@@ strMWOKeyCreated is: " + strMWOKeyCreated);
				}
				// end if its received as kit or simple item
				if (nlComponent != null && nlComponent.getLength() > 0) {
					// it has got components process the components
					int iTotalComponents = nlComponent.getLength();
					System.out.println("@@@@@ Processing " + iTotalComponents + " components...");
					// processing components
					for (int iCounterComponents = 0; iCounterComponents < iTotalComponents; iCounterComponents++) {
						Element elemComponent = (Element) nlComponent.item(iCounterComponents);
						// create the master work order line if and only if the line has got NRFI status
						System.out.println("@@@@@ creating MWOLine for item: " + iCounterComponents);
						//set MWOKey based on the returned key from createMasterWorkOrderLine
						strMWOKeyCreated = createMasterWorkOrderLine(env, elemComponent, strMWOKeyCreated, strCacheID, true, docNWCGMasterWorkOrder);
					}
				}
			}
		}*/
		
		System.out.println("@@@@@ Exiting NWCGCreateMasterWorkOrderFromReturns::createMasterWorkOrderFromReturns @@@@@");
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
		System.out.println("@@@@@ Entering NWCGCreateMasterWorkOrderFromReturns::createMasterWorkOrderLine @@@@@");
		
		// SETTING VALUES!
		// From elemOutXML_getReceiptDetails_ReceiptLine: ShipByDate
		String strShipByDate = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ShipByDate");
		System.out.println("@@@@@ BEFORE: strShipByDate: " + strShipByDate);
		try {
			// try/catch to prevent parse exceptions
			// date input sample from yfs_receipt_line: "01/1/2500"
			if (!strShipByDate.equals("") && strShipByDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date dShipByDate = sdf.parse(strShipByDate);
				SimpleDateFormat sdfTarget = new SimpleDateFormat("yyyy-MM-dd");
				strShipByDate = sdfTarget.format(dShipByDate);
				System.out.println("@@@@@ AFTER: strShipByDate: " + strShipByDate);
			}
		} catch (Exception ex) {
			// if for some reason we are unable to parse the date, just set it to "" as this is an optional field. 
			System.out.println("!!!!! Unable to parse the ShipByDate, just set it to \"\". ");
			strShipByDate = "";
		}
		
		// SETTING VALUES!
		// From elemOutXML_getReceiptDetails_ReceiptLine: Quantity
		String strNRFI = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("Quantity");
		System.out.println("@@@@@ strNRFI: " + strNRFI);
		// From elemOutXML_getReceiptDetails_ReceiptLine: ItemID
		String strItemID = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ItemID");
		System.out.println("@@@@@ strItemID: " + strItemID);
		// From elemOutXML_getReceiptDetails_ReceiptLine: UnitOfMeasure
		String strUOM = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("UnitOfMeasure");
		System.out.println("@@@@@ strUOM: " + strUOM);
		// From elemOutXML_getReceiptDetails_ReceiptLine: EnterpriseCode
		String strOrganizationCode = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("EnterpriseCode");
		System.out.println("@@@@@ strOrganizationCode: " + strOrganizationCode);
		// From elemOutXML_getReceiptDetails_ReceiptLine: DispositionCode
		String strDispositionCode = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("DispositionCode");
		System.out.println("@@@@@ strDispositionCode: " + strDispositionCode);
		// From elemOutXML_getReceiptDetails_ReceiptLine: ProductClass
		String strProductClass = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ProductClass");
		System.out.println("@@@@@ strProductClass: " + strProductClass);
		// From elemOutXML_getReceiptDetails_ReceiptLine: ReceiptHeaderKey
		String strReceiptHeaderKey = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ReceiptHeaderKey");
		System.out.println("@@@@@ strReceiptHeaderKey: " + strReceiptHeaderKey);
		// From elemOutXML_getReceiptDetails_ReceiptLine: ReceiptLineKey
		String strReceiptLineKey = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("ReceiptLineKey");
		System.out.println("@@@@@ strReceiptLineKey: " + strReceiptLineKey);
		
		NodeList listOutXML_getReceiptDetails_ReceiptLine_Extn = elemOutXML_getReceiptDetails_ReceiptLine.getElementsByTagName("Extn");
		Element elemOutXML_getReceiptDetails_ReceiptLine_Extn = (Element) listOutXML_getReceiptDetails_ReceiptLine_Extn.item(0);
		// From elemOutXML_getReceiptDetails_ReceiptLine_Extn: ExtnReceivedAsComponent
		String strExtnReceivedAsComponent = elemOutXML_getReceiptDetails_ReceiptLine_Extn.getAttribute("ExtnReceivedAsComponent");
		System.out.println("@@@@@ strExtnReceivedAsComponent: " + strExtnReceivedAsComponent);
		// From elemOutXML_getReceiptDetails_ReceiptLine_Extn: ExtnReceivingPrice
		String strExtnReceivingPrice = elemOutXML_getReceiptDetails_ReceiptLine_Extn.getAttribute("ExtnReceivingPrice");
		System.out.println("@@@@@ strExtnReceivingPrice: " + strExtnReceivingPrice);
		
		// pass values to method that will call getItemDetails and return a String for ShortDescription
		String strShortDescription = InvokeItemDetailsAPI(env, strItemID, strUOM, strOrganizationCode);
		
		String strPrimarySerialNo = "";
		String strSecondarySerialNo = "";

		System.out.println("@@@@@ Entering Serial Logic! ");
		
		// get Serial number from ReceiptLine
		String strSerialNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("SerialNo");
		System.out.println("@@@@@ strSerialNo: " + strSerialNo);
		
		// this will be true if/when you find your serial info
		boolean isFound = false;
		
		// This Receipt Line has a Trackable Item
		if(!strSerialNo.equals("")) {
			// get Serial list (if there are trackable components there will be stuff in this list)
			NodeList listinXML_Serial = inXML.getElementsByTagName("Serial");
			System.out.println("@@@@@ listinXML_Serial: " + listinXML_Serial.getLength());
			// get SerialInfoMainItem list (if there are trackable items with NO components there will be stuff in this list)
			NodeList listinXML_SerialInfoMainItem = inXML.getElementsByTagName("SerialInfoMainItem");
			System.out.println("@@@@@ listinXML_SerialInfoMainItem: " + listinXML_SerialInfoMainItem.getLength());
			// loop through listinXML_Serial
			for(int iSerial = 0; iSerial < listinXML_Serial.getLength(); iSerial++) {
				Element eleminXML_Serial = (Element) listinXML_Serial.item(iSerial);
				// is this a trackable component?
				System.out.println("@@@@@ Serial list item: " + iSerial);
				if (strSerialNo.equals(eleminXML_Serial.getAttribute("serialID"))) {
					System.out.println("@@@@@ Found a Trackable Component! ");
					// set Primary Serial number
					strPrimarySerialNo = eleminXML_Serial.getAttribute("serialID");
					System.out.println("@@@@@ strPrimarySerialNo: " + strPrimarySerialNo);
					// set Secondary Serial number
					strSecondarySerialNo = eleminXML_Serial.getAttribute("SecondarySerialNo");
					System.out.println("@@@@@ strSecondarySerialNo: " + strSecondarySerialNo);
					// break out as we have found our Serial number and have set it's values, no need to continue
					System.out.println("@@@@@ Breaking out of the loop because we have found and set our serial details! ");
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
					System.out.println("@@@@@ Serial list item: " + iSerialInfoMainItem);
					if(strSerialNo.equals(eleminXML_SerialInfoMainItem.getAttribute("PrimarySerialNo"))) {
						System.out.println("@@@@@ Found a Trackable item, NOT returned as it's components! ");
						// set Primary Serial number
						strPrimarySerialNo = eleminXML_SerialInfoMainItem.getAttribute("PrimarySerialNo");
						System.out.println("@@@@@ strPrimarySerialNo: " + strPrimarySerialNo);
						// set Secondary Serial number
						strSecondarySerialNo = eleminXML_SerialInfoMainItem.getAttribute("SecondarySerialNo");
						System.out.println("@@@@@ strSecondarySerialNo: " + strSecondarySerialNo);
						// break out as we have found our Serial number and have set it's values, no need to continue
						System.out.println("@@@@@ Breaking out of the loop because we have found and set our serial details! ");
						isFound = true;
						break;
					}
				}
			}	
		} else {
			// This Receipt Line does NOT have a Trackable Item
			System.out.println("@@@@@ This ReceiptLine is not a Trackable component or a Trackable item NOT returned as components...");
		}
		
		System.out.println("@@@@@ Exiting Serial Logic! ");
		System.out.println("@@@@@ Entering Tag Attribute Logic! ");
		
		// SETTING VALUES!
		// From elemOutXML_getReceiptDetails_ReceiptLine: ManufacturerName (Example: "PACIFIC")
		String strManufacturerName = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotAttribute1");
		System.out.println("@@@@@ strManufacturerName: " + strManufacturerName);
		// From elemOutXML_getReceiptDetails_ReceiptLine: ManufacturerModel (Example: "MKIII")
		String strManufacturerModel = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotAttribute3");
		System.out.println("@@@@@ strManufacturerModel: " + strManufacturerModel);
		// From elemOutXML_getReceiptDetails_ReceiptLine: LotNo (Example: "GBK-0148-599")
		String strLotNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotNumber");
		System.out.println("@@@@@ strLotNo: " + strLotNo);
		// From elemOutXML_getReceiptDetails_ReceiptLine: RevisionNo (Example: "11/14/2011")
		String strRevisionNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("RevisionNo");
		System.out.println("@@@@@ strRevisionNo: " + strRevisionNo);
		// From elemOutXML_getReceiptDetails_ReceiptLine: BatchNo (Example: "")
		String strBatchNo = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("BatchNo");
		System.out.println("@@@@@ strBatchNo: " + strBatchNo);
		// From elemOutXML_getReceiptDetails_ReceiptLine: OwnerUnitID (Example: "IDGBK")
		String strOwnerUnitID = elemOutXML_getReceiptDetails_ReceiptLine.getAttribute("LotAttribute2");
		System.out.println("@@@@@ strOwnerUnitID: " + strOwnerUnitID);
		
		/*
		Element elemTagAttributesMainItem = null;
		if(strExtnReceivedAsComponent.equalsIgnoreCase("Y")) {
			System.out.println("@@@@@ This was received as a component, therfore if there is an attribute tag, it needs to be matched and set using TagAttributes element. ");
			Element elemReturns = inXML.getDocumentElement();
			NodeList nlReceiptLine = elemReturns.getElementsByTagName("ReceiptLine");
			if(nlReceiptLine != null) {
				System.out.println("@@@@@ Receipt Line is NOT null! ");
				int iTotal = nlReceiptLine.getLength();
				for(int iCounter = 0 ; iCounter < iTotal ; iCounter ++) {
					Element elemReceiptLine = (Element) nlReceiptLine.item(iCounter);
					NodeList nlComponent = elemReceiptLine.getElementsByTagName("Component");
					if(nlComponent != null && nlComponent.getLength() > 0) {
						System.out.println("@@@@@ component list: " + nlComponent.getLength());
						int iTotalComponents = nlComponent.getLength();
						for(int iCounterComponents = 0 ; iCounterComponents < iTotalComponents ; iCounterComponents ++) {
							Element elemComponent = (Element) nlComponent.item(iCounterComponents);
							String strInXMLCompItemid = elemComponent.getAttribute("ItemID");
							String strInXMLCompProdClass = elemComponent.getAttribute("ProductClass");
							String strInXMLCompUOM = elemComponent.getAttribute("UOM");
							System.out.println("@@@@@ " + strInXMLCompItemid + " : " + strInXMLCompProdClass + " : " + strInXMLCompUOM);
							if(strItemID.equals(strInXMLCompItemid.trim()) && strProductClass.equals(strInXMLCompProdClass.trim()) && strUOM.equals(strInXMLCompUOM.trim())) {
								System.out.println("@@@@@ Found a match! Try to set TagAttributes. ");
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
			System.out.println("@@@@@ This was NOT received as a component, therfore if there is an attribute tag, it needs to be matched and set using TagAttributesMainItem element. ");
			Element elemReturns = inXML.getDocumentElement();
			NodeList nlReceiptLine = elemReturns.getElementsByTagName("ReceiptLine");
			if(nlReceiptLine != null) {
				System.out.println("@@@@@ Receipt Line is NOT null! ");
				int iTotal = nlReceiptLine.getLength();
				System.out.println("@@@@@ iTotal: " + iTotal);
				for(int iCounter = 0 ; iCounter < iTotal ; iCounter ++) {
					Element elemReceiptLine = (Element) nlReceiptLine.item(iCounter);
					String strInXMLNonCompItemid = elemReceiptLine.getAttribute("ItemID");
					String strInXMLNonCompProdClass = elemReceiptLine.getAttribute("ProductClass");
					String strInXMLNonCompUOM = elemReceiptLine.getAttribute("UOM");
					System.out.println("@@@@@ " + strInXMLNonCompItemid + " : " + strInXMLNonCompProdClass + " : " + strInXMLNonCompUOM);
					if(strItemID.equals(strInXMLNonCompItemid.trim()) && strProductClass.equals(strInXMLNonCompProdClass.trim()) && strUOM.equals(strInXMLNonCompUOM.trim())) {
						System.out.println("@@@@@ Found a match! Try to set TagAttributesMainItem. ");
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
		
		System.out.println("@@@@@ Exiting Tag Attribute Logic! ");

		// get the quantity, and since we cannot get here unless this is NRFI item, quantity must be greater than or equal to 0. 
		try {
			// Quantity Example: Quantity="1.00"
			int iNRFIQty = (int)Double.parseDouble(strNRFI);
			if (iNRFIQty <= 0) {
				// do not create a mwo line
				System.out.println("@@@@@ Do NOT Create the MWO Line because the NRFI Quantity is <= 0. ");
				return strMasterWorkOrderKey;
			}
		} catch (NumberFormatException nfe) {
			// if exception dont create any MWO line
			System.out.println("@@@@@ Do NOT Create the MWO Line because we encountered an exception while parsing the NRFI Quantity. " + nfe);
			return strMasterWorkOrderKey;
		} catch (Exception ex) {
			System.out.println("@@@@@ Do NOT Create the MWO Line because we encountered an exception while parsing the NRFI Quantity. " + ex);
		}
		
		System.out.println("@@@@@ strMasterWorkOrderKey is : " + strMasterWorkOrderKey);
		// if and only if masterworkorderkey is null, create a new MWO
		if (strMasterWorkOrderKey == null) {
			System.out.println("@@@@@ strMasterWorkOrderKey is null, creating a MWO...");
			Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
			strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
			System.out.println("@@@@@ (strMasterWorkOrderKey is now AFTER creating work order: " + strMasterWorkOrderKey);
		}
		// creating MWO line per serial number
		Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, strNRFI, strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo, strUOM, strCacheID, strShipByDate, strReceiptHeaderKey, strReceiptLineKey, strExtnReceivingPrice, strManufacturerName, strManufacturerModel, strLotNo, strRevisionNo, strBatchNo, strOwnerUnitID);
		// create master work order line

		System.out.println("@@@@@ createMasterWorkOrderFromReturns :: IP " + XMLUtil.getXMLString(docNWCGMasterWorkOrderLine));
		System.out.println("@@@@@ invoking CreateMasterWorkOrderServiceLine");
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME, docNWCGMasterWorkOrderLine);
		// we are done with processing all the components return the masterwork order key
		return strMasterWorkOrderKey;
	}
	
		/*
		// if its component, attributes has to be extracted from Serial Tag
		if (bIsComponent) {
			NodeList nlSerialInfoMainItem = elem.getElementsByTagName("Serial");
			System.out.println("@@@@@ Entering a component item...");

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

					System.out.println("@@@@@ strMasterWorkOrderKey is : "
								+ strMasterWorkOrderKey);
					//if and only if masterworkorderkey is null, create a new MWO
					if (strMasterWorkOrderKey == null) {
						System.out.println("@@@@@ strMasterWorkOrderKey is null, creating a MWO...");
						Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
						strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
						System.out.println("@@@@@ (strMasterWorkOrderKey is now AFTER creating work order: " + strMasterWorkOrderKey);
					}
					// creating MWO line per serial number
					Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, "1", strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo1, strUOM, elemSerialInfoMainItem, strCacheID, strShipByDate);
					//				 create master work order line

					System.out.println("@@@@@ createMasterWorkOrderFromReturns :: IP " + XMLUtil.getXMLString(docNWCGMasterWorkOrderLine));
					System.out.println("@@@@@ invoking CreateMasterWorkOrderServiceLine");
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
			System.out.println("@@@@@ Entering a non-component item...");
			NodeList nlSerialInfoMainItem = elem.getElementsByTagName("SerialInfoMainItem");
			if (nlSerialInfoMainItem != null && nlSerialInfoMainItem.getLength() > 0) {
				for (int index = 0; index < nlSerialInfoMainItem.getLength(); index++) {
					System.out.println("@@@@@ Entering 'else' for loop...");
					Element elemSerialInfoMainItem = (Element) nlSerialInfoMainItem.item(index);
					strPrimarySerialNo = elemSerialInfoMainItem.getAttribute("PrimarySerialNo");
					strSecondarySerialNo1 = elemSerialInfoMainItem.getAttribute("SecondarySerialNo");
					strDispositionCode = elemSerialInfoMainItem.getAttribute("DispositionCode");
					if (strDispositionCode != null && (!strDispositionCode.equals("NRFI"))) {
						System.out.println("@@@@@ strDispositionCode is: " + strDispositionCode);
						//if the disposition code is not NRFI dont create any MWO line
						continue;
					}
					//if and only if masterworkorderkey is null, create a MWO
					if (strMasterWorkOrderKey == null) {
						System.out.println("@@@@@ (strMasterWorkOrderKey is null, creating master work order....");
						Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
						strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
						System.out.println("@@@@@ (strMasterWorkOrderKey is now: " + strMasterWorkOrderKey);
					}
					// creating MWO line per serial number
					System.out.println("@@@@@ Getting MWO Line Document...");
					Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, "1", strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo1, strUOM, elemSerialInfoMainItem, strCacheID, strShipByDate);

					//				 create master work order line
					System.out.println("@@@@@ createMasterWorkOrderFromReturns :: IP "
										+ XMLUtil
												.getXMLString(docNWCGMasterWorkOrderLine));
					System.out.println("@@@@@ MWOLine retreived... creating MWOLine");
					CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME, docNWCGMasterWorkOrderLine);
				}
				//				 we are done with processing all the KIT item return master work order key
				System.out.println("@@@@@ strMasterWorkOrderKey is: " + strMasterWorkOrderKey);
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
			System.out.println("@@@@@ strMasterWorkOrderKey is null, creating a MWO...");
			Document docOutNWCGMasterWorkOrder = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME, docNWCGMasterWorkOrder);
			strMasterWorkOrderKey = docOutNWCGMasterWorkOrder.getDocumentElement().getAttribute("MasterWorkOrderKey");
			System.out.println("@@@@@ strMasterWorkOrderKey is now AFTER creating work order: "+ strMasterWorkOrderKey);
		}
		Document docNWCGMasterWorkOrderLine = getMasterWorkOrderLineDocument(strMasterWorkOrderKey, strNRFI, strShortDescription, strItemID, strPrimarySerialNo, strProductClass, strSecondarySerialNo1, strUOM, elemTagAttributesMainItem, strCacheID, strShipByDate);
		//				 create master work order line

		System.out.println("@@@@@ createMasterWorkOrderFromReturns :: IP " + XMLUtil.getXMLString(docNWCGMasterWorkOrderLine));
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME, docNWCGMasterWorkOrderLine);
		System.out.println("@@@@@ Returning strMasterWorkOrderKey " + strMasterWorkOrderKey + "from end of method...");
		System.out.println("@@@@@ Exiting NWCGCreateMasterWorkOrderFromReturns::createMasterWorkOrderLine @@@@@");
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
		System.out.println("@@@@@ Entering NWCGCreateMasterWorkOrderFromReturns::getMasterWorkOrderLineDocument @@@@@");

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
			System.out.println("@@@@@ ManufacturerName: " + StringUtil.nonNull(strManufacturerName));
			elemNWCGMasterWorkOrderLine.setAttribute("ManufacturerModel", StringUtil.nonNull(strManufacturerModel));
			System.out.println("@@@@@ ManufacturerModel: " + StringUtil.nonNull(strManufacturerModel));
			elemNWCGMasterWorkOrderLine.setAttribute("LotNo", StringUtil.nonNull(strLotNo));
			System.out.println("@@@@@ LotNo: " + StringUtil.nonNull(strLotNo));
			elemNWCGMasterWorkOrderLine.setAttribute("RevisionNo", StringUtil.nonNull(strRevisionNo));
			System.out.println("@@@@@ RevisionNo: " + StringUtil.nonNull(strRevisionNo));
			elemNWCGMasterWorkOrderLine.setAttribute("BatchNo", StringUtil.nonNull(strBatchNo));
			System.out.println("@@@@@ BatchNo: " + StringUtil.nonNull(strBatchNo));
			elemNWCGMasterWorkOrderLine.setAttribute("OwnerUnitID", StringUtil.nonNull(strOwnerUnitID));
			System.out.println("@@@@@ OwnerUnitID: " + StringUtil.nonNull(strOwnerUnitID));
		elemNWCGMasterWorkOrderLine.setAttribute("Status",NWCGConstants.NWCG_REFURB_MWOL_INITIAL_STATUS);

		System.out.println("@@@@@ docNWCGMasterWorkOrderLine: " + XMLUtil.getXMLString(docNWCGMasterWorkOrderLine));
		System.out.println("@@@@@ Exiting NWCGCreateMasterWorkOrderFromReturns::getMasterWorkOrderLineDocument @@@@@");
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
		System.out.println("@@@@@ Entering NWCGCreateMasterWorkOrderFromReturns::InvokeIncidentAPI @@@@@");
		
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
		System.out.println("@@@@@ docIncidentIP: " + XMLUtil.getXMLString(docIncidentIP));
		
		// call the API that will return the Incident details
		Document docIncidentDetailsOut = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, docIncidentIP);
		System.out.println("@@@@@ docIncidentDetailsOut: " + XMLUtil.getXMLString(docIncidentDetailsOut));

		Element elemIncidentDetails = docIncidentDetailsOut.getDocumentElement();

		// SETTING VALUES!
		// From docIncidentDetailsOut: IncidentBlmAcctCode
		String strBLMCode = elemIncidentDetails.getAttribute("IncidentBlmAcctCode");
		System.out.println("@@@@@ strBLMCode: " + strBLMCode);
		// From docIncidentDetailsOut: IncidentFsAcctCode
		String strFSCode = elemIncidentDetails.getAttribute("IncidentFsAcctCode");
		System.out.println("@@@@@ strFSCode: " + strFSCode);
		// From docIncidentDetailsOut: IncidentOtherAcctCode
		String strOtherCode = elemIncidentDetails.getAttribute("IncidentOtherAcctCode");
		System.out.println("@@@@@ strOtherCode: " + strOtherCode);
		// From docIncidentDetailsOut: OverrideCode
		String strOverrideCode = elemIncidentDetails.getAttribute("OverrideCode");
		System.out.println("@@@@@ strOverrideCode: " + strOverrideCode);
		// From docIncidentDetailsOut: IncidentType
		String strIncidentType = elemIncidentDetails.getAttribute("IncidentType");
		System.out.println("@@@@@ strIncidentType: " + strIncidentType);

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
		
		System.out.println("@@@@@ Exiting NWCGCreateMasterWorkOrderFromReturns::InvokeIncidentAPI @@@@@");
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
		System.out.println("@@@@@ Entering NWCGCreateMasterWorkOrderFromReturns::InvokeItemDetailsAPI @@@@@");
		
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
			System.out.println("@@@@@ docInXML_getItemDetails : " + XMLUtil.getXMLString(docInXML_getItemDetails));
			
			// call getItemDetails API
			if(docInXML_getItemDetails != null) {
				Document docOutXML_getItemDetails = CommonUtilities.invokeAPI(env, "getItemDetails", docInXML_getItemDetails);
				System.out.println("@@@@@ docOutXML_getItemDetails : " + XMLUtil.getXMLString(docOutXML_getItemDetails));
				
				// validate docOutXML_getItemDetails document before using it
				if(docOutXML_getItemDetails != null) {
					
					// retrieve the ShortDescription and store it in a string
					NodeList listdocOutXML_getItemDetails_PrimaryInformation = docOutXML_getItemDetails.getElementsByTagName("PrimaryInformation");
					if (listdocOutXML_getItemDetails_PrimaryInformation != null) {
						int iTotal = listdocOutXML_getItemDetails_PrimaryInformation.getLength();
						System.out.println("@@@@@ listdocOutXML_getItemDetails_PrimaryInformation length: " + iTotal);
						Node nodedocOutXML_getItemDetails_PrimaryInformation = listdocOutXML_getItemDetails_PrimaryInformation.item(0);
						Element elemdocOutXML_getItemDetails_PrimaryInformation = (Element)nodedocOutXML_getItemDetails_PrimaryInformation;
						
						// SETTING VALUES!
						// From elemdocOutXML_getItemDetails_PrimaryInformation: ShortDescription
						strShortDescription = elemdocOutXML_getItemDetails_PrimaryInformation.getAttribute("ShortDescription");
						System.out.println("@@@@@ strShortDescription: " + strShortDescription);
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
			System.out.println("!!!!! Caught Null Pointer Exception: " + npe);
			npe.printStackTrace();
		} catch (Exception exp) {
			System.out.println("!!!!! Caught General Exception: " + exp);
			exp.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGCreateMasterWorkOrderFromReturns::InvokeItemDetailsAPI @@@@@");
		return strShortDescription;
	}
}