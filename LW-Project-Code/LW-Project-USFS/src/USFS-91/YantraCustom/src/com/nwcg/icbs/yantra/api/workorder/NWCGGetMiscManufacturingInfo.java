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

package com.nwcg.icbs.yantra.api.workorder;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/* NWCGGetMiscManufacturingInfo.java
 *   
 * Takes the OUTPUT of getNWCGmasterWorkOrderLine and 
 * updates the XML to add the manufactuing date
 * from NWCG_TRACKABLE_ITEM via NWCGGetTrackableItemListService
 *  
 * Input Example for NWCGGetTrackableItemListService:
 * <TrackableItem EnterpriseCode="NWCG" ItemID="000148" SerialNo="NWK-0148-78" />
 * 
 * Output Example for NWCGGetTrackableItemListService:
 * <?xml version="1.0" encoding="UTF-8" ?> 
 * <NWCGTrackableItemList>
 * <NWCGTrackableItem AcquisitionCost="2837.00" AcquisitionDate="2010-01-21" BLMAccountCode="LLFA241000.LF20000SP.HU0000.LFSPEK2A0000" Createprogid="NWCGTrackableInventoryMessageConsumer" Createts="2010-01-21T04:31:30-05:00" Createuserid="NWCGTrackableInventoryMessageConsumer" FSAccountCode="P6EK2A" ItemID="000148" ItemShortDescription="PUMP - PORTABLE,HIGH PRESSURE W/FUEL LINE" LastBuyerOrnanizationCode="ORDEF" LastDocumentNo="AZPFK001149" LastIncidentNo="OR-DEF-000034" LastIncidentYear="2010" LastIssuePrice="2811.00" LastTransactionDate="2011-04-13" Lockid="14" LotAttribute1="WILDFIRE" LotAttribute3="MARK III" ManufacturingDate="1997-03-25" Modifyprogid="Console" Modifyts="2011-04-13T13:39:47-04:00" Modifyuserid="dalexander" OverrideCode="0601" OwnerUnitID="ORNWK" OwnerUnitName="Northwest Interagency Fire Cache" RevisionNo="10/04/2010" SecondarySerial="1256205" SerialNo="NWK-0148-78" SerialStatus="N" SerialStatusDesc="NRFI" StatusBuyerOrganizationCode="ORDEF" StatusCacheID="AZPFK" StatusIncidentNo="OR-DEF-000034" StatusIncidentYear="2010" TrackableItemKey="2010012104313114558372" Type="Return" UnitOfMeasure="EA" isHistory="N" /> 
 * </NWCGTrackableItemList>
 */
public class NWCGGetMiscManufacturingInfo implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetMiscManufacturingInfo.class);

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {

		Message.append(" ExceptionType='TID_List' InboxType='TID_List' QueueId='TID_List' />");
		if (logger.isVerboseEnabled())
			logger.verbose("Throw Alert Method called with message:-"
					+ Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("GET_TID_LIST_ERROR");
		}

	}

	public Document getManufacturingDate(YFSEnvironment env, Document inDoc)
			throws Exception {
		String strItemID = "";
		String strSerial = "";
		String manufDate = "";

		// Get ItemID and Serial Number
		try {
			Element RootElem = inDoc.getDocumentElement();
			strItemID = RootElem.getAttribute("ItemID");
			strSerial = RootElem.getAttribute("PrimarySerialNo");

			// Build service input doc
			Document svcInDoc = XMLUtil.newDocument();
			Element newTrackableItemElement = svcInDoc
					.createElement("TrackableItem");
			svcInDoc.appendChild(newTrackableItemElement);
			newTrackableItemElement.setAttribute("EnterpriseCode", "NWCG");
			newTrackableItemElement.setAttribute("ItemID", strItemID);
			newTrackableItemElement.setAttribute("SerialNo", strSerial);

			// call NWCGGetTrackableItemListService and Manufactuing date
			Document trkItemList = CommonUtilities.invokeService(env,
					"NWCGGetTrackableItemListService", svcInDoc);

			NodeList listOfTIDs = trkItemList
					.getElementsByTagName("NWCGTrackableItem");
			for (int i = 0; i < listOfTIDs.getLength(); i++) {
				Element curTID = (Element) listOfTIDs.item(i);
				manufDate = curTID.getAttribute("ManufacturingDate");
			}
			// Update received document to add Manufacturing Date

			RootElem.setAttribute("ManufacturingDate", manufDate);

		} catch (Exception e) {
			logger.error("!!!!! NWCGGetMiscManufacturingInfo::getManufacturingDate Caught Exception "
						,e);
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGGetMiscManufacturingInfo' "
							+ "DetailDescription='getManufacturingDate Failed '");
			throwAlert(env, stbuf);
		}
		// return document
		return inDoc;
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}