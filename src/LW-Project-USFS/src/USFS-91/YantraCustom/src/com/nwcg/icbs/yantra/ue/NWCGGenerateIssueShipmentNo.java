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

package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.ydm.japi.ue.YDMBeforeCreateShipment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * This UE is called before the createShipment/changeShipment APIs.  
 * 
 * @author Lightwellinc
 * @version 2.0
 * @date August 21, 2014
 */
public class NWCGGenerateIssueShipmentNo implements YDMBeforeCreateShipment {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGenerateIssueShipmentNo.class);

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
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.ue.NWCGGenerateIssueShipmentNo::beforeCreateShipment @@@@@");
		logger.verbose("@@@@@ inDoc :: " + XMLUtil.getXMLString(inDoc));

		// Begin PI 1405 - Added by Vishy for ReturnNo issue with uniqueExceptionID
        // Extract ShipmentNo and DocumentType from the Input XML
        String strShipmentNo = inDoc.getDocumentElement().getAttribute("ShipmentNo");
		logger.verbose("@@@@@ strShipmentNo :: " + strShipmentNo);
        String strDocumentType = inDoc.getDocumentElement().getAttribute("DocumentType");
		logger.verbose("@@@@@ strDocumentType :: " + strDocumentType);
        // First Validation make sure the ShipmentNo and DocumentType are not empty (Both have to have values to go inside)
        if(!(StringUtil.isEmpty(strShipmentNo) && StringUtil.isEmpty(strDocumentType))) {
        	// if NOT true and there are values in strShipmentNo & strDocumentType, 
        	// then check if strDocumentType is '0010' and that strShipmentNo is valid, 
        	// and then do not proceed further into this method and return the inDoc 
        	logger.verbose("@@@@@ strShipmentNo & strDocumentType are NOT Empty, so we need to check them here...");
        	if(strDocumentType.equals("0010") && ! StringUtil.isEmpty(strShipmentNo) && strShipmentNo.startsWith("S")) {
        		// strDocumentType equals 0010 & strShipmentNo is NOT empty and begins with an S character, stop and return the inDoc
        		logger.verbose("@@@@@ strDocumentType equals 0010 & strShipmentNo is NOT empty and begins with an S character...");
        		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.ue.NWCGGenerateIssueShipmentNo::beforeCreateShipment @@@@@");
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
			logger.verbose("@@@@@ orderNo :: " + orderNo);

			//Begin CR844 11302012
			if (orderNo.equals("") || (orderNo.equalsIgnoreCase("null")) || (orderNo.length() == 0)) {
				logger.verbose("@@@@@ orderNo is blank, null, or missing...");
			} else {
				logger.verbose("@@@@@ Probably should be displaying this statement...");
			}
			//End CR844 11302012

			String shipmentNo = inDoc.getDocumentElement().getAttribute("ShipmentNo");
			logger.verbose("@@@@@ shipmentNo :: " + shipmentNo);

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
					logger.verbose("NWCGGenerateIssueShipmentNo::beforeCreateShipment: Exception: strOrderNo " + strOrderNo + " should not start with a -");
					return null;
				}
				//End CR844 11302012
				inDoc.getDocumentElement().setAttribute("ShipmentNo", strOrderNo);
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e,e);
			throw new YFSUserExitException(e.getMessage());
		}
		
		logger.verbose("@@@@@ inDoc :: " + XMLUtil.getXMLString(inDoc));
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.ue.NWCGGenerateIssueShipmentNo::beforeCreateShipment @@@@@");
		return inDoc;
	}
}
