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

package com.nwcg.icbs.yantra.commCore;

import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGDeliverOperationResultsAPI {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGDeliverOperationResultsAPI.class);
	
	private Properties props;
	
	private static final String className = NWCGDeliverOperationResultsAPI.class.getName();
	
	public void setProperties(Properties prop) throws Exception {
		this.props = prop;
	}
	
	public Document deliverOperationResults(YFSEnvironment env, Document inDoc) {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsAPI::deliverOperationResults ");
		logger.verbose("@@@@@ Indoc :: " + XMLUtil.getXMLString(inDoc));
		
	    String userName = NWCGConstants.EMPTY_STRING;
		String latest_system_key = ResourceUtil.get("NWCG_DO_NO_AUTH_DB_KEY", "9011917122761465");
    	logger.verbose("@@@@@ latest_system_key :: " + latest_system_key);
		String loginID = NWCGAAConstants.USER_NAME;
    	logger.verbose("@@@@@ LoginID :: " + loginID);
    	String system_name = env.getSystemName(); 
    	logger.verbose("@@@@@ System Name :: " + system_name);
    	
    	userName = loginID;
    	String pw_key = NWCGTimeKeyManager.createKey(latest_system_key, loginID, system_name);
    	logger.verbose("@@@@@ pw_key :: " + pw_key);
    	
		String message_name = inDoc.getDocumentElement().getNodeName();
		logger.verbose("@@@@@ message_name :: " + message_name);
    	
		String string_token = NWCGJAXRPCWSHandlerUtils.getStringToken(userName, loginID, pw_key, message_name);
    	logger.verbose("@@@@@ string token :: " + string_token);
	    try {
	    	//sendResultsReq(string_token,inDoc,distID);
	    }catch(Exception e){
	    	logger.error("!!!!! Caught General Exception " ,e);
	    	
	        // Raising alert. 
	        CommonUtilities.raiseAlert(env, NWCGAAConstants.QUEUEID_FAULT, e.toString(), inDoc, e, null);
	    }
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsAPI::deliverOperationResults ");
		return null;
	}
}