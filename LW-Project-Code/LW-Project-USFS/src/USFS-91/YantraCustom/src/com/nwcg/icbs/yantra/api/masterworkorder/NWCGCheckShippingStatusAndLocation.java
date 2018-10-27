/* Copyright 2015, Lightwell, Inc. All rights reserved. */
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

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Validate Target Location and Inventory Status. 
 * 
 * @author Lightwell
 * @version 1.0
 * @date June 3, 2015
 */
public class NWCGCheckShippingStatusAndLocation {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGCheckShippingStatusAndLocation.class);

	private Properties props;

	public void setProperties(Properties arg0) throws Exception {
		this.props = arg0;
	}
	
	/**
	 * This Class designed for USFS CR 1421. 
	 * Date: June 8, 2015. 
	 * 
	 * The decision to come into this service is based on a condition where 
	 * TargetLocationId="SHIP-SORT01" and InventoryStatus="NRFI-RFB". 
	 * 
	 * Purpose is to NOT let the Trackable Item on an open MWO be processed. 
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document checkShippingStatusAndLocation(YFSEnvironment env, Document inXML) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCheckShippingStatusAndLocation::checkShippingStatusAndLocation @@@@@");
		logger.verbose("!!!!! Inventory Status is NRFI-RFB and Target Location is SHIP-SORT01...Cannot ship this item...");
		YFSException ne = new YFSException();
		ne.setErrorCode("Inventory Status And Target Location Exception");
		ne.setErrorDescription("!!!!! Error: This trackable item has a status of NRFI-RFB, " +
			"which means it is on an open MWO. Items on open MWOs cannot shipped out from SHIP-SORT01. " +
			"Please review and complete the open MWO before continuing. ");
		logger.verbose("!!!!! Exiting NWCGCheckShippingStatusAndLocation::checkShippingStatusAndLocation !!!!!");
		throw ne;
	}
}