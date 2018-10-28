package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.ydm.japi.ue.YDMBeforeCreateShipment;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * This UE is called before the createShipment/changeShipment APIs.  
 * 
 * @author Lightwellinc
 * @version 2.0
 * @date August 21, 2014
 */
public class NWCGGenerateIssueShipmentNo implements YDMBeforeCreateShipment {

	private static Logger log = Logger.getLogger(NWCGInsertIncidentRecord.class.getName());

	public NWCGGenerateIssueShipmentNo() {
		super();
	}

	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws YFSUserExitException
	 */
	public Document beforeCreateShipment(YFSEnvironment env, Document inDoc) throws YFSUserExitException {
		System.out.println("@@@@@ Entering com.nwcg.icbs.yantra.ue.NWCGGenerateIssueShipmentNo::beforeCreateShipment @@@@@");
		System.out.println("@@@@@ inDoc :: " + XMLUtil.getXMLString(inDoc));

		// Begin PI 1405 - Added by Vishy for ReturnNo issue with uniqueExceptionID
        // Extract ShipmentNo and DocumentType from the Input XML
        String strShipmentNo = inDoc.getDocumentElement().getAttribute("ShipmentNo");
		System.out.println("@@@@@ strShipmentNo :: " + strShipmentNo);
        String strDocumentType = inDoc.getDocumentElement().getAttribute("DocumentType");
		System.out.println("@@@@@ strDocumentType :: " + strDocumentType);
        // First Validation make sure the ShipmentNo and DocumentType are not empty (Both have to have values to go inside)
        if(!(StringUtil.isEmpty(strShipmentNo) && StringUtil.isEmpty(strDocumentType))) {
        	// if NOT true and there are values in strShipmentNo & strDocumentType, 
        	// then check if strDocumentType is '0010' and that strShipmentNo is valid, 
        	// and then do not proceed further into this method and return the inDoc 
        	System.out.println("!!!!! strShipmentNo & strDocumentType are NOT Empty, so we need to check them here...");
        	if(strDocumentType.equals("0010") && ! StringUtil.isEmpty(strShipmentNo) && strShipmentNo.startsWith("S")) {
        		// strDocumentType equals 0010 & strShipmentNo is NOT empty and begins with an S character, stop and return the inDoc
        		System.out.println("!!!!! strDocumentType equals 0010 & strShipmentNo is NOT empty and begins with an S character...");
        		System.out.println("!!!!! Exiting com.nwcg.icbs.yantra.ue.NWCGGenerateIssueShipmentNo::beforeCreateShipment @@@@@");
        		return inDoc;
            }
        }
        // End PI 1405
		
		// get the current count shipments
		try {
			String orderNo = inDoc.getDocumentElement().getAttribute("OrderNo");
			if (StringUtil.isEmpty(orderNo)) {
				//try to get the order numbers from release line
				orderNo = XPathUtil.getString(inDoc.getDocumentElement(), "/Shipment/ShipmentLines/ShipmentLine[1]/@OrderNo");
			}
			System.out.println("@@@@@ orderNo :: " + orderNo);

			//Begin CR844 11302012
			if (orderNo.equals("") || (orderNo.equalsIgnoreCase("null")) || (orderNo.length() == 0)) {
				System.out.println("!!!!! orderNo is blank, null, or missing...");
			} else {
				System.out.println("!!!!! Probably should be displaying this statement...");
			}
			//End CR844 11302012

			String shipmentNo = inDoc.getDocumentElement().getAttribute("ShipmentNo");
			System.out.println("@@@@@ shipmentNo :: " + shipmentNo);

			Document getShipmentListInput = XMLUtil.createDocument("Shipment");
			getShipmentListInput.getDocumentElement().setAttribute("OrderNo", orderNo);
			env.setApiTemplate("getShipmentList", "NWCGGenerateIssueShipmentNo_getShipmentList");

			Document getshipmentListResult = CommonUtilities.invokeAPI(env, "getShipmentList", getShipmentListInput);
			String totalRec = getshipmentListResult.getDocumentElement().getAttribute("TotalNumberOfRecords");

			env.clearApiTemplate("getShipmentList");

			// default to 0 if we don't find any records
			if (StringUtil.isEmpty(totalRec))
				totalRec = "0"; 

			long currentSeq = Long.parseLong(totalRec);
			// increment the sequence
			long nextSeq = currentSeq + 1; 

			String nextshipmentNo = StringUtil.prepadStringWithZeros(Long.toString(nextSeq), NWCGConstants.MAX_DIGITS_SEQ_NWCG_ISSUE_SHIPMENTNO);

			if (StringUtil.isEmpty(shipmentNo)) {
				//Begin CR844 11302012
				String strOrderNo = orderNo + "-" + nextshipmentNo;
				String strPrefixInShipmentNo = strOrderNo.substring(0, 1);
				if (strPrefixInShipmentNo.equals("-")) {
					System.out.println("NWCGGenerateIssueShipmentNo::beforeCreateShipment: Exception: strOrderNo " + strOrderNo + " should not start with a -");
					return null;
				}
				//End CR844 11302012
				inDoc.getDocumentElement().setAttribute("ShipmentNo", strOrderNo);
			}
		} catch (Exception e) {
			System.out.println("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
			throw new YFSUserExitException(e.getMessage());
		}
		
		System.out.println("@@@@@ inDoc :: " + XMLUtil.getXMLString(inDoc));
		System.out.println("@@@@@ Exiting com.nwcg.icbs.yantra.ue.NWCGGenerateIssueShipmentNo::beforeCreateShipment @@@@@");
		return inDoc;
	}
}
