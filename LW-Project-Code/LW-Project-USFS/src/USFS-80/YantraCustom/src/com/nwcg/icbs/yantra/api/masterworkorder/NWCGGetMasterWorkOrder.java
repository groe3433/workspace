package com.nwcg.icbs.yantra.api.masterworkorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetMasterWorkOrder implements YIFCustomApi {
	private static Logger logger = Logger.getLogger(NWCGGetMasterWorkOrder.class.getName());

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Entering NWCGGetMasterWorkOrder::throwAlert @@@@@");
		Message.append(" ExceptionType='MWO_List' InboxType='MWO_List' QueueId='MWO_List' />");
		if(logger.isVerboseEnabled()) logger.verbose("!!!!! Throw Alert Method called with message : " + Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("GET_MWO_LIST_ERROR");
		}
		if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Exiting NWCGGetMasterWorkOrder::throwAlert @@@@@");
	}

	public Document GetMWOList(YFSEnvironment env, Document inXML) throws Exception {
		if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Entering NWCGGetMasterWorkOrder::GetMWOList @@@@@");
		if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Input document : /n" + XMLUtil.getXMLString(inXML));

		Document MWOList = null;
		Document MWOListOP = XMLUtil.newDocument();
		Element RootOPElem = MWOListOP.createElement("NWCGMasterWorkOrderList");
		MWOListOP.appendChild(RootOPElem);

		try {
			Element RootElem = inXML.getDocumentElement();
			String OpenOrder = RootElem.getAttribute("OpenOrder");
			String ItemID = RootElem.getAttribute("ItemID");
			String ItemQryType = RootElem.getAttribute("ItemIDQryType");

			// BEGIN - CR 835 - Jan 21, 2013
			
			/* CR 835 - Logging MaximumRecords value as it will no longer be hardcoded to a value of 6000, 
			 *          and thus by default the value of 200 gets passed in as input anyway. 
			 */ 
			String strMaximumRecords = RootElem.getAttribute("MaximumRecords");
			inXML.getDocumentElement().setAttribute("MaximumRecords", strMaximumRecords);
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ strMaximumRecords : " + strMaximumRecords);
			
			/* CR 835 - Logging CreatetsQryType value as it is being read from a "hidden" field on the jsp page. 
			 * 			Note: if you are not going to use it, then you should not pass it into the NWCGGetMasterWorkOrderListService service. 
			 */
			String strCreatetsQryType = RootElem.getAttribute("CreatetsQryType");
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ strCreatetsQryType : " + strCreatetsQryType);
			
			/* CR 835 - Verify that the new feature, "search by date range", contains valid dates. 
			 * 
			 * 			If you search by date, then the date values show like this:
			 * 			strFromCreatets: 2010-08-01T00:00:00
			 *			strToCreatets: 2010-08-03T00:00:00
			 *			strFromCreatets.length: 19
			 *			strToCreatets.length: 19
			 *			And your CreatetsQryType value should be "BETWEEN"
			 *
			 *			If you do not search by date, then the date values show like this:
			 *			strFromCreatets: 
			 *			strToCreatets: 
			 *			strFromCreatets.length: 0
			 *			strToCreatets.length: 0
			 *			And your CreatetsQryType value should be ""
			 */
			String strFromCreatets = RootElem.getAttribute("FromCreatets");
			String strToCreatets = RootElem.getAttribute("ToCreatets");
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ strFromCreatets: " + strFromCreatets);
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ strToCreatets: " + strToCreatets);
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ strFromCreatets.length: " + strFromCreatets.length());
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ strToCreatets.length: " + strToCreatets.length());
			if(strFromCreatets.length() < 10 || strToCreatets.length() < 10) {
				if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Unset the CreatetsQryType value for the new \"search by date range\" feature. ");
				// dates for the "search by date range" feature did not contain proper dates, so unset the CreatetsQryType
				inXML.getDocumentElement().setAttribute("CreatetsQryType", "");
			} else {
				if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Do not unset the CreatetsQryType value for the new \"search by date range\" feature. ");
				// dates for the "search by date range" feature did contain proper dates, so do not unset the CreatetsQryType
			}
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Final XML Here : /n" + XMLUtil.getXMLString(inXML));
			MWOList = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderListService", inXML);		
			
			// END - CR 835 - Jan 21, 2013
			
			NodeList listOfMWOs = MWOList.getElementsByTagName("NWCGMasterWorkOrder");
			if(logger.isVerboseEnabled()) logger.verbose("@@@@@ MWO Length : " + listOfMWOs.getLength());
			int numberCount = 0;
			List<String> wos = new ArrayList<String>();
			for (int i = 0; i < listOfMWOs.getLength(); i++) {
				Element curMWO = (Element) listOfMWOs.item(i);
				String MWOKey = curMWO.getAttribute("MasterWorkOrderKey");
				String MWOStatus = "";
				Document MWOLineList = null;
				int opencnt = 0, completecnt = 0, partialcnt = 0, linecnt = 0;
				MWOLineList = getMWOLineList(env, MWOKey, ItemID, ItemQryType);
				NodeList listOfMWOLines = MWOLineList.getDocumentElement().getElementsByTagName("NWCGMasterWorkOrderLine");
				linecnt = listOfMWOLines.getLength();
				if(logger.isVerboseEnabled()) logger.verbose("@@@@@ NWCGMasterWorkOrderLine Length " + linecnt);

				if (linecnt > 0 && ItemID != null && ItemID.trim().length() > 0) {
					/* CR 752 aoadio
					 * Means an Item id filter was specified and there was a hit on the item ID
					 * The Work Order status is calculated on the entire work order line not a single work order line
					 * Get the enter work order line for this work order
					 * */
					MWOLineList = getMWOLineList(env, MWOKey, "", ItemQryType);
					listOfMWOLines = MWOLineList.getDocumentElement()
							.getElementsByTagName("NWCGMasterWorkOrderLine");
					linecnt = listOfMWOLines.getLength();
				}

				if (linecnt > 0) {
					numberCount++;
					wos.add(MWOKey);

					for (int j = 0; j < listOfMWOLines.getLength(); j++) {
						Element curMWOLine = (Element) listOfMWOLines.item(j);
						String Status = curMWOLine.getAttribute("Status");
						if (Status.equals("Awaiting Work Order Creation")) {
							opencnt++;
						} else if (Status
								.equals("Work Order Partially Completed")) {
							partialcnt++;
						} else {
							completecnt++;
						}

					}

					if(logger.isVerboseEnabled()) logger.verbose("@@@@@ linecnt :: " + linecnt);
					if(logger.isVerboseEnabled()) logger.verbose("@@@@@ completecnt ::" + completecnt);
					if(logger.isVerboseEnabled()) logger.verbose("@@@@@ opencnt :: " + opencnt);
					if(logger.isVerboseEnabled()) logger.verbose("@@@@@ partialcnt :: " + partialcnt);

					if ((linecnt - completecnt) > 0) {
						if (partialcnt > 0 || completecnt > 0) {
							MWOStatus = "Work Order Partially Completed";
						} else {
							MWOStatus = "Awaiting Work Order Creation";
						}
					}
					if ((linecnt - completecnt) == 0) {
						MWOStatus = "Work Order Completed";
					}

					if (OpenOrder.equals("Y")
							&& MWOStatus.equals("Awaiting Work Order Creation")) {
						Element MWOOpElem = MWOListOP
								.createElement("NWCGMasterWorkOrder");
						RootOPElem.appendChild(MWOOpElem);
						XMLUtil.copyElement(MWOListOP, curMWO, MWOOpElem);
						MWOOpElem.setAttribute("MWOStatus", MWOStatus);
						MWOOpElem.setAttribute("MWOOrderStatus",
								"Awaiting Work Order Creation");
					}

					if (OpenOrder.equals("N")
							&& MWOStatus.equals("Work Order Completed")) {
						Element MWOOpElem = MWOListOP
								.createElement("NWCGMasterWorkOrder");
						RootOPElem.appendChild(MWOOpElem);
						XMLUtil.copyElement(MWOListOP, curMWO, MWOOpElem);
						MWOOpElem.setAttribute("MWOStatus", MWOStatus);
						MWOOpElem.setAttribute("MWOOrderStatus",
								"Work Order Completed");
					}

					if (OpenOrder.equals("P")
							&& MWOStatus
									.equals("Work Order Partially Completed")) {
						Element MWOOpElem = MWOListOP
								.createElement("NWCGMasterWorkOrder");
						RootOPElem.appendChild(MWOOpElem);
						XMLUtil.copyElement(MWOListOP, curMWO, MWOOpElem);
						MWOOpElem.setAttribute("MWOStatus", MWOStatus);
						MWOOpElem.setAttribute("MWOOrderStatus",
								"Work Order Partially Completed");
					}

					if (OpenOrder.equals("A")) {
						Element MWOOpElem = MWOListOP
								.createElement("NWCGMasterWorkOrder");
						RootOPElem.appendChild(MWOOpElem);
						XMLUtil.copyElement(MWOListOP, curMWO, MWOOpElem);
						MWOOpElem.setAttribute("MWOStatus", MWOStatus);
						MWOOpElem.setAttribute("MWOOrderStatus", "");
					}

				}
			}
		} catch (Exception e) {
			if(logger.isVerboseEnabled()) logger.verbose("GetMWOList::GetMWOList Caught Exception " + e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGGetMasterWorkOrder' "
							+ "DetailDescription='GetMWOList Failed '");
			throwAlert(env, stbuf);
		}

		if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Exiting NWCGGetMasterWorkOrder::GetMWOList @@@@@");
		return MWOListOP;
	}

	public Document getMWOLineList(YFSEnvironment env, String MWOKey, String ItemID, String ItemQryType) throws Exception {
		if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Entering NWCGGetMasterWorkOrder::getMWOLineList @@@@@");
		
		Document getMWOLineIP = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element GetMWOLineIPElem = getMWOLineIP.getDocumentElement();
		GetMWOLineIPElem.setAttribute("MasterWorkOrderKey", MWOKey);
		//cr kjs add ItemID to search criteria
		GetMWOLineIPElem.setAttribute("ItemID", ItemID);
		//CR 679 ML BEGIN - Added ItemQrytype to lookup
		GetMWOLineIPElem.setAttribute("ItemIDQryType", ItemQryType);
		//CR 679 ML END - Added ItemQrytype to lookup
		Document getMWOLineOP = null;

		try {
			getMWOLineOP = CommonUtilities.invokeService(env,
					"NWCGGetMasterWorkOrderLineListService", getMWOLineIP);

		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGGetMasterWorkOrder' "
							+ "DetailDescription='GetMWOLineList Failed for Master Work Order Key : "
							+ MWOKey + "'");
			throwAlert(env, stbuf);
		}
		if(logger.isVerboseEnabled()) logger.verbose("getMWOLineIP: " + XMLUtil.getXMLString(getMWOLineIP));
		if(logger.isVerboseEnabled()) logger.verbose("getMWOLineOP: " + XMLUtil.getXMLString(getMWOLineOP));
		
		if(logger.isVerboseEnabled()) logger.verbose("@@@@@ Exiting NWCGGetMasterWorkOrder::getMWOLineList @@@@@");
		return getMWOLineOP;
	}

}
